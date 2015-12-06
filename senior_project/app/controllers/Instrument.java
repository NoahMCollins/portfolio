package controllers;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import java.util.Arrays;
import java.util.ArrayList;

import controllers.Query;
import controllers.HttpHelper;

public class Instrument extends Controller{//Controller for all Instruments in the USGS database
	private static ArrayList<String>	fields		= new ArrayList(Arrays.asList("iid", "name", "description"));
	private static ArrayList<String>	fieldNames	= new ArrayList(Arrays.asList("column_name", "column_type"));
	
	@Security.Authenticated(Authenticate.class)
	public static Result getInstruments(){//returns a Result object containing all Instruments in its body
		Query q= new Query();
		
		q.select(new ArrayList<String>()).from("instruments");
		
		return HttpHelper.usgsQuery(q.query, fields);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result getInstrumentFields( String name ){//returns a Result object containing all Fields for a specified Instrument in its body
		Query q= new Query();
		
		q.select(fieldNames)
			.from("information_schema.columns")
			.where("table_name", "LIKE", name + "$%")
			.andWhereOr("column_type", "LIKE", "int%", "column_type", "=", "double")
			.groupBy("column_name");
		
		
		System.out.println(q.query);
		return HttpHelper.usgsQuery(q.query, fieldNames);
	}
}
