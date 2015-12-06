package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import java.util.Arrays;
import java.util.ArrayList;

import controllers.Query;
import controllers.HttpHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class Criteria extends Controller{//Controller for all Criteria in the database
	private static ArrayList<String>	fields		= new ArrayList(Arrays.asList("ID", "alarmID", "instrumentName",
															"fieldName", "alarmTrigger", "triggerTime", "value"));
	private static ArrayList<String>	fillable	= new ArrayList(Arrays.asList("alarmID", "instrumentName",
															"fieldName", "alarmTrigger", "triggerTime", "value"));
	private static ArrayList<String> 	count		= new ArrayList(Arrays.asList("COUNT(*)"));
	private static ArrayList<String> 	tables		= new ArrayList(Arrays.asList("table_name"));
	private static ArrayList<String> 	columns		= new ArrayList(Arrays.asList("column_name"));
	private static ArrayList<String> 	j2ksec		= new ArrayList(Arrays.asList("j2ksec"));
	private static ArrayList<String> 	stAndEt		= new ArrayList(Arrays.asList("st", "et"));

	private static String				primaryKey	= "ID";
	
	@Security.Authenticated(Authenticate.class)
	public static Result getCriteria(Integer id){//returns a Result object containing all criteria for the specified alarm in the requestBody
		Query q= new Query();
		
		q.select(new ArrayList<String>()).from("criteria").where("AlarmID", "=", Integer.toString(id));
		
		return HttpHelper.query(q.query, fields);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result postCriteria(Integer alarmId){//adds a new Criteria for the specified Alarm, and returns a Result object 
		Query q= new Query();
		RequestBody body= request().body();
		ObjectNode obj = (ObjectNode) body.asJson();
		 
		if(alarmId == null) 
			return badRequest("Invalid alarmId");
		
		if(!instrumentCheck(alarmId, obj)) 
			return badRequest("Invalid instrument/field combination");
		
		//RESTRICTIONS
		//	refresh rate must be valid for the instrument/channel
		//  triggers cannot conflict			
		
		obj.put("refreshRate", getRefresh(obj));
    	obj.put("alarmID", alarmId);
        
    	q.insert("criteria", fillable, (JsonNode) obj);
		
		return HttpHelper.update(q.query);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result putCriteria(Integer alarmId, Integer id){//modifies the specified Criteria for the specified Alarm, and returns a Result object
		Query q= new Query();
		RequestBody body= request().body();
		
		ObjectNode obj = (ObjectNode) body.asJson();

		if(!instrumentCheck(alarmId, obj)) 
			return badRequest("Invalid instrument/field combination");
		
		//RESTRICTIONS
		//	refresh rate must be valid for the instrument/channel
		//  triggers cannot conflict
		obj.put("refreshRate", getRefresh(obj));
		
		// null alarmId will not be accepted by the database, and will return 400 even without this if statement
		if(obj.get("alarmID") != null && alarmId != obj.get("alarmID").asInt()) 
			return badRequest("This criteria does not belong to the specified alarm");
    	
		q.update("criteria", fillable, (JsonNode) obj).where(primaryKey, "=", Integer.toString(id));
		
		return HttpHelper.update(q.query);
	}

	@Security.Authenticated(Authenticate.class)
	public static Result deleteCriteria(Integer alarmId, Integer id){//deletes the specified Criteria for the specified Alarm, and returns a Result object
		Query q= new Query();
		RequestBody body= request().body();
		
		q.delete()
			.from("criteria")
			.where(primaryKey, "=", Integer.toString(id))
			.andWhere("alarmID", "=", Integer.toString(alarmId));	
		
		return HttpHelper.update(q.query);
	}	
	
	public static boolean instrumentCheck(Integer alarmId, ObjectNode obj){//checks for the existence of the instrument and it's field, returns true if present
		Query q= new Query();
//		instrument name must exist
		String instrumentName =obj.get("instrumentName").toString();
		instrumentName = instrumentName.substring(1,instrumentName.length()-1);
		q.select(count).from("instruments")
			.where("name", "=", instrumentName);
		if(!HttpHelper.usgsPresent(q.query))
			return false;
		q.query = "";

//		field name must exist
		String fieldName = obj.get("fieldName").toString();
		fieldName = fieldName.substring(1,fieldName.length()-1);
		q.select(new ArrayList(Arrays.asList("column_name")))
			.from("information_schema.columns")
			.where("table_name", "LIKE", instrumentName + "$%")
			.andWhereOr("column_type", "LIKE", "int%", "column_type", "=", "double")
			.groupBy("column_name");
		String innerSelect = q.query;
		q.query="";
		q.select(count)
			.from("("+innerSelect+") X")
			.where("column_name", "=", fieldName);
		if(!HttpHelper.usgsPresent(q.query))
			return false;
		q.query = "";
		return true;
	}
	
	public static int getRefresh(ObjectNode obj){
		Query q= new Query();
		int refresh = Integer.MAX_VALUE;
		String instrumentName =obj.get("instrumentName").toString();
		instrumentName = instrumentName.substring(1,instrumentName.length()-1);
		ArrayNode tableArray;
		ArrayNode columnArray;
		ArrayNode dataArray;

		
		q.select(new ArrayList(Arrays.asList("table_name")))
		.from("information_schema.tables")
		.where("table_name", "LIKE", instrumentName + "$%");
		
		tableArray = HttpHelper.usgsGetData(q.query, tables);
		q.query="";
		
		for(int i=0; i<tableArray.size(); i++){
			String tableName = tableArray.get(i).get("table_name").toString();
			tableName = tableName.substring(1,tableName.length()-1);
			
			q.select(new ArrayList(Arrays.asList("column_name")))
			.from("information_schema.columns")
			.where("table_name", "=", tableName)
			.andWhereOrOr("column_name", "=", "j2ksec", "column_name", "=", "st", "column_name", "=", "et");
			columnArray = HttpHelper.usgsGetData(q.query, columns);
			q.query="";
			
			String col = columnArray.get(0).get("column_name").toString();
			col = col.substring(1,col.length()-1);
			
			if(col.equals("j2ksec")){//UNSAFE, if instrument readings stop coming in then the hole in j2ksec may set refresh too high 
				q.select(new ArrayList(Arrays.asList("j2ksec"))).from(tableName).limit(0,50);
				dataArray = HttpHelper.usgsGetData(q.query, j2ksec);
				q.query = "";
				
				int last;
				int current;
				String temp = dataArray.get(0).get("j2ksec").toString();
				temp = temp.substring(1,temp.length()-1);
				last = Integer.parseInt(temp);
				for(int j=1; j<50; j++){
					temp = dataArray.get(j).get("j2ksec").toString();
					temp = temp.substring(1,temp.length()-1);
					current = Integer.parseInt(temp);
					if(Math.abs(current-last) < refresh){
						refresh = Math.abs(last - current);
					}
					last = current;
				}
				
			}
			else{//st,et 
				q.select(new ArrayList(Arrays.asList("st","et"))).from(tableName).limit(0,1);
				dataArray = HttpHelper.usgsGetData(q.query, stAndEt);
				q.query = "";
				String temp = dataArray.get(0).get("st").toString();
				temp = temp.substring(1,temp.length()-1);
				int st = Integer.parseInt(temp);
				temp = dataArray.get(0).get("et").toString();
				temp = temp.substring(1,temp.length()-1);
				int et = Integer.parseInt(temp);
				refresh = et-st;
				break;
			}
		}
		
		if(refresh== Integer.MAX_VALUE)
			refresh = 1;
		
		return refresh;		
	}
	
//	public static boolean userCheck(Integer alarmId){//checks that alarm belongs to the logged in user, and returns true if it does
//		Query q= new Query();
//		q.select(count)
//			.from("alarms")
//			.where("ID", "=", Integer.toString(alarmId))
//			.andWhere("creator", "=", session("email"));
//		if(!HttpHelper.present(q.query))
//			return false;	
//		return true;
//	}
}
