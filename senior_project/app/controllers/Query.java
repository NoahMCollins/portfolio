package controllers;

import java.util.ArrayList;
import play.*;
import play.mvc.*;

import com.fasterxml.jackson.databind.JsonNode;

public class Query{
	public String	query;
	
	public Query(){//start building a query
		this.query= "";
	}
	
	public Query delete(){//append "DELETE " to the query
		this.query+= "DELETE ";
		
		return this;
	}
	
	public Query insert(String table, ArrayList<String> fields, JsonNode obj){//appends "INSERT INTO <table> [field] VALUES [value] " to the query
		this.query+= "INSERT INTO `" + table + "` (`";
		
		int size= fields.size();
		
		for(int i= 0; i < size; i++){
			this.query+= fields.get(i);
			if(i != size - 1) this.query+= "`, `";
		}
		this.query+= "`) VALUES (";
		
		for(int i= 0; i < size; i++){
			this.query+= obj.get(fields.get(i));
			if(i != size - 1) this.query+= ", ";
		}
		
		query+= ")";
		
		return this;
	}
	
	public Query update(String table, ArrayList<String> fields, JsonNode obj){//appends "UPDATE <table> SET [<field> = <value>] " to the query
		this.query+= "UPDATE `" + table + "` SET `";
		
		int size= fields.size();
		
		for(int i= 0; i < size; i++){
			this.query+= fields.get(i) + "` = " + obj.get(fields.get(i));
			if(i != size - 1)
				this.query+= ", `";
			else
				this.query+= " ";
		}
		
		return this;
	}
	
	public Query select(ArrayList<String> fields){//appends "SELECT <[field] | *> " to the query
		this.query+= "SELECT ";
		
		int size= fields.size();
		
		if(size == 0){
			this.query+= "* ";
		}
		else{
			for(int i= 0; i < size; i++){
				this.query+= fields.get(i);
				if(i != size - 1)
					this.query+= ", ";
				else
					this.query+= " ";
			}
		}
		
		return this;
	}
	
	public Query from(String table){//appends "FROM <table> " to the query
		this.query+= "FROM ".concat(table).concat(" ");
		
		return this;
	}
	
	public Query where(String field, String operator, String value){//appends "WHERE <field> <op> <value> " to the query
		this.query+= "WHERE ".concat(field).concat(" ").concat(operator).concat(" \"").concat(value).concat("\" ");
		
		return this;
	}
	
	public Query andWhere(String field, String operator, String value){//appends "AND <field> <op> <value> " to the query
		this.query+= "AND ".concat(field).concat(" ").concat(operator).concat(" \"").concat(value).concat("\" ");
		
		return this;
	}
	
	//appends "AND <field> <op> <value> OR <field2> <op2> <value2 " to the query
	public Query andWhereOr(String field, String operator, String value, String field2, String operator2, String value2){
		this.query+= "AND (".concat(field).concat(" ").concat(operator).concat(" \"").concat(value).concat("\" ");	
		this.query+= "OR ".concat(field2).concat(" ").concat(operator2).concat(" \"").concat(value2).concat("\") ");
		
		return this;
	}
	
	public Query andWhereOrOr(String field, String operator, String value, String field2, String operator2, String value2, String field3, String operator3, String value3){
		this.query+= "AND (".concat(field).concat(" ").concat(operator).concat(" \"").concat(value).concat("\" ");
		this.query+= "OR ".concat(field2).concat(" ").concat(operator2).concat(" \"").concat(value2).concat("\" ");			
		this.query+= "OR ".concat(field3).concat(" ").concat(operator3).concat(" \"").concat(value3).concat("\") ");	
		
		return this;
	}
	
	public Query or(String field, String operator, String value){
		this.query+= "OR ".concat(field).concat(" ").concat(operator).concat(" \"").concat(value).concat("\") ");	
		
		return this;
	}
	
	public Query groupBy(String field){//appends "GROUP BY <field>" to the query
		this.query+= "GROUP BY ".concat(field);
		
		return this;
	}
	
	public Query limit(int startVal, int endVal){
		this.query+= "LIMIT ".concat(Integer.toString(startVal)).concat(",").concat(Integer.toString(endVal));
		
		return this;
	}
	
}
