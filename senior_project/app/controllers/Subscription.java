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

public class Subscription extends Controller{//Controller for all Subscriptions in the database
	private static ArrayList<String>	fields		= new ArrayList(Arrays.asList("ID", "alarmName", "email"));
	private static ArrayList<String>	fillable	= new ArrayList(Arrays.asList("alarmName", "email"));
	private static String				primaryKey	= "ID";
	
	@Security.Authenticated(Authenticate.class)
	public static Result getSubscriptions(){//returns a Result object containing all Subscriptions for the current user in its body
		Query q= new Query();
	
		q.select(new ArrayList<String>())
			.from("subscriptions")
			.where("email", "=", session("email"));
		
		return HttpHelper.query(q.query, fields);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result postSubscription(){//adds a new Subscription for the current user, and returns a Result object
		Query q= new Query();
		RequestBody body = request().body();
		
		ObjectNode obj = (ObjectNode) body.asJson();
    	obj.put("email", session("email"));
		
		q.insert("subscriptions", fillable, (JsonNode) obj);
		
		return HttpHelper.update(q.query);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result putSubscription(Integer id){//modifies an existing Subscription for the current user, and returns a Result object
		Query q= new Query();
		RequestBody body= request().body();

		q.update("subscriptions", fillable, body.asJson())
			.where(primaryKey, "=", Integer.toString(id))
			.andWhere("email", "=", session("email"));
		
		return HttpHelper.update(q.query);
	}
	
	@Security.Authenticated(Authenticate.class)
	public static Result removeSubscription(Integer id){//modifies an existing Subscription for the current user, and returns a Result object
		Query q= new Query();
		
		q.delete()
			.from("subscriptions")
			.where(primaryKey, "=", Integer.toString(id))
			.andWhere("email", "=", session("email"));
		
		return HttpHelper.update(q.query);
	}
}
