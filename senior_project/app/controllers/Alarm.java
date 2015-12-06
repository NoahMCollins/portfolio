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

public class Alarm extends Controller{//Controller for all Alarms in the database
	private static ArrayList<String>	fields		= new ArrayList(Arrays.asList("ID", "alarmName", "creator",
															"triggered", "enabled"));
	private static ArrayList<String>	fillable	= new ArrayList(Arrays.asList("alarmName", "creator", "triggered",
															"enabled"));
	private static String				primaryKey	= "ID";
	
	@Security.Authenticated(Authenticate.class)
	public static Result getAlarms(){//returns a Result object containing all Alarms in its body
		Query q= new Query();
		
		q.select(new ArrayList<String>()).from("alarms");
		
		return HttpHelper.query(q.query, fields);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result postAlarm(){//adds a new Alarm for the current user, and returns a Result object 
		Query q= new Query();
		RequestBody body= request().body();
		ObjectNode obj = (ObjectNode) body.asJson();

    	obj.put("creator", session("email"));
    	obj.put("triggered", 0);
    	obj.put("enabled", 0);
		
		q.insert("alarms", fillable, (JsonNode) obj);
		
		return HttpHelper.update(q.query);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result putAlarm(Integer id){//modifies the Alarm that the id corresponds to, and returns a Result object
		Query q= new Query();
		RequestBody body= request().body();
		
		q.update("alarms", fillable, body.asJson()).where(primaryKey, "=", Integer.toString(id));
		
		return HttpHelper.update(q.query);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result deleteAlarm(Integer id){//deletes the Alarm that the id corresponds to, and returns a Result object
		Query q= new Query();
		RequestBody body= request().body();
		
		q.delete().from("alarms").where(primaryKey, "=", Integer.toString(id));
		
		return HttpHelper.update(q.query);
	}
}
