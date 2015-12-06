package controllers;

import play.*;
import play.mvc.*;

import java.util.ArrayList;


//Play DB
import play.db.DB;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Pure Object Json
import java.lang.Object;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class HttpHelper extends Controller {
    public static Result query( String query, ArrayList<String> select) {//executes a SQL query and returns a Result with the results in its body
    	Connection myDB = DB.getConnection();
    	Result response = ok();
    	
    	try {
    		Statement stmt = myDB.createStatement();
        	
    		ResultSet res = stmt.executeQuery(query);
    		
        	ObjectMapper mapper = new ObjectMapper();
            ArrayNode nodes = mapper.createArrayNode();
            while(res.next()) {
                ObjectNode jNode = mapper.createObjectNode();
                for( String field : select ) {
                    jNode.put(field,res.getString(field));
                }
                nodes.add(jNode);
            }

        	response = ok(nodes);
    		myDB.close();
    	} catch(SQLException sqlError) {
    		response = badRequest(sqlError.getMessage());
    	} finally {
    	    return response;
    	}
    }
    
    public static Result update( String query) {//executes a SQL update and returns a Result with the status of the update
    	Connection myDB = DB.getConnection();
    	Result response = ok("Success");
    	
    	try {
    		Statement stmt = myDB.createStatement();
        	
    		stmt.executeUpdate(query);
    		myDB.close();
    	} catch(SQLException sqlError) {
    		response = badRequest(sqlError.getMessage());
    	} finally {
    	    return response;
    	}
    }
    
    public static ObjectNode find( String query, ArrayList<String> select) {
    	Connection myDB = DB.getConnection();

    	ObjectMapper mapper = new ObjectMapper();
    	
    	try {
    		Statement stmt = myDB.createStatement();
        	
    		ResultSet res = stmt.executeQuery(query);
    		
    		int dup = 0;
            while(res.next()) { 
                ObjectNode jNode = mapper.createObjectNode();
                for( String field : select ) {
                    jNode.put(field,res.getString(field));
                }
                return jNode;
            }

    		return null;
    	} catch(SQLException sqlError) {

            System.out.println(sqlError.toString());
            return null;
    	}
    }
    
    public static boolean present(String query) {//executes a SQL query that checks if a DB entry is present, and returns a boolean
    	Connection myDB = DB.getConnection();
    	boolean present = false;
    	try {
    		Statement stmt = myDB.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		if(rs.next()){
				if(rs.getInt("COUNT(*)") > 0)
					present = true;
			}
    		rs.close();
    		stmt.close();
    		myDB.close();
    	} catch(SQLException e) {
    		System.out.println(e);
    	} 
    	return present;
    }
    
    public static boolean usgsPresent(String query) {//executes a SQL query that checks if a USGS DB entry is present, and returns a boolean
    	Connection myDB = DB.getConnection("usgs");
    	boolean present = false;
    	try {
    		Statement stmt = myDB.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		if(rs.next()){
    			if(rs.getInt("COUNT(*)") > 0)
    				present = true;
			}
    		rs.close();
    		stmt.close();
    		myDB.close();
    	} catch(SQLException e) {
    		System.out.println(e);
    	} 
    	return present;
    }
    
    public static Result usgsQuery( String query, ArrayList<String> select) {//executes a SQL query on the USGS DB and returns a Result with the results in its body
    	Connection myDB = DB.getConnection("usgs");
    	Result response = ok();
    	
    	try {
    		Statement stmt = myDB.createStatement();
        	
    		ResultSet res = stmt.executeQuery(query);
    		
        	ObjectMapper mapper = new ObjectMapper();
            ArrayNode nodes = mapper.createArrayNode();
            while(res.next()) {
                ObjectNode jNode = mapper.createObjectNode();
                for( String field : select ) {
                    jNode.put(field,res.getString(field));
                }
                nodes.add(jNode);
            }

        	response = ok(nodes);
    		myDB.close();
    	} catch(SQLException sqlError) {
    		response = badRequest(sqlError.getMessage());
    	} finally {
    	    return response;
    	}
    }
    
    public static ArrayNode usgsGetData(String query, ArrayList<String> select) {
    	Connection myDB = DB.getConnection("usgs");
    	ObjectMapper mapper = new ObjectMapper();
        ArrayNode nodes = mapper.createArrayNode();
        
    	try {
    		Statement stmt = myDB.createStatement();
    		ResultSet rs = stmt.executeQuery(query);
    		
    		while(rs.next()){
    			ObjectNode jNode = mapper.createObjectNode();
    			for(String field : select){
    				jNode.put(field,rs.getString(field));
    			}
    			nodes.add(jNode);
			}
    		
    		rs.close();
    		stmt.close();
    		myDB.close();
    	} catch(SQLException e) {
    		System.out.println(e);
    	} 
    	return nodes;
    }
}
