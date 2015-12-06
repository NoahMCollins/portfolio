package controllers;

import play.*;
import play.libs.Crypto;
import play.mvc.*;
import play.mvc.Http.*;

import controllers.Query;
import controllers.HttpHelper;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class User extends Controller {
	public static ArrayList<String> fields   = new ArrayList(Arrays.asList("ID", "email"));
    public static ArrayList<String> fillable = new ArrayList(Arrays.asList("email", "password"));
    private static String  primaryKey = "ID";
    
    @Security.Authenticated(Authenticate.class)
    public static int currentUser( Integer id ) {
    	int currentId = Integer.parseInt(session("id"));
    	
    	if( id == currentId )
    		return 1;
    	else 
    		return 0;
    }
    
    @Security.Authenticated(Authenticate.class)
    public static Result getUser() {
    	Query q = new Query();
    	
    	q.select(new ArrayList<String>())
    		.from("users")
    		.where(primaryKey, "=", session("id"));
    	
    	return HttpHelper.query(q.query, fields);
    }

    @Security.Authenticated(Authenticate.class)
    public static Result getUsers() {

    	Query q = new Query();
    	
    	q.select(fields)
    		.from("users");
    	
    	return HttpHelper.query(q.query, fields);
    }
    
    @Security.Authenticated(Authenticate.class)
    public static Result postUser( String email, String password ) {

    	Query q = new Query();

    	ObjectNode obj = JsonNodeFactory.instance.objectNode();

        if( password == null ||
            email == null )
            return null;

        obj.put("password", Crypto.encryptAES(password));
        obj.put("email", email);

    	q.insert("users", fillable, (JsonNode) obj);
    	
    	return HttpHelper.update(q.query);
    }

    @Security.Authenticated(Authenticate.class)
    public static Result putUser(Integer id) {
    	if( currentUser(id) == 0 ) return badRequest("Not Authorized");
    	
        RequestBody body = request().body();

        ObjectNode obj = (ObjectNode) body.asJson();
        
        if( obj.get("password") != null ) {
        	System.out.println("Password: " + obj.get("password").toString());
        	obj.put("password", Crypto.encryptAES(obj.get("password").toString().replace("\"", "")));
        }
        
        Query q = new Query();
        
        q.update("users", fillable, (JsonNode) obj)
        	.where(primaryKey, "=", Integer.toString(id));
        
        return HttpHelper.update(q.query);
    }
}
