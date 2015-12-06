package controllers;

import play.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import views.html.*;
//Play Json
import play.libs.Json;
import play.libs.Crypto;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.Query;
import controllers.HttpHelper;

//Play DB
import play.db.DB;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Pure Object Json
import java.lang.Object;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//Examples of how to set up different routes
public class Application extends Controller {

	@Security.Authenticated(Authenticate.class)
    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    
	public static Result login() {
        return ok(login.render(Form.form(Login.class)));
    }

	public static Result register() {
        return ok(register.render(Form.form(Register.class)));
    }
	
	public static Result logout() {
	    session().clear();
	    flash("success", "You've been logged out");
	    return redirect(
	        routes.Application.login()
	    );
	}
    
	public static Result postRegister() {
	    Form<Register> registerForm = Form.form(Register.class).bindFromRequest();

	    if (registerForm.hasErrors()) {
	        return badRequest(register.render(registerForm));
	    } else {
	        session().clear();
	        session("email", registerForm.get().email);
	        session("id", registerForm.get().user_id);
	        return redirect(
	            routes.Application.index()
	        );
	    }
	}

	public static Result authenticate() {
	    Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

	    if (loginForm.hasErrors()) {
	        return badRequest(login.render(loginForm));
	    } else {
	        session().clear();
	        session("email", loginForm.get().email);
	        session("id", loginForm.get().user_id);
	        return redirect(
	            routes.Application.index()
	        );
	    }
	}

    public static class Register {
        public String email;
        public String password;
        public String confirm;
        public String first_name;
        public String last_name;
        protected static String user_id = null;

        public String validate() {
            if (this.register(email, password, confirm, first_name, last_name) == null) {
              return "Insufficient Data to create account";
            }
            if( this.authenticate(email, password) != null ) {
                return null;
            }

            return "Failed to set up account";
        }

        private static String register( String email, String password, String confirm, String first_name, String last_name ) {

            if( email == null || password == null || first_name == null || last_name == null || password.equals(confirm) == false ) {
                return null;
            }

            if( controllers.User.postUser(email, password) != null ) {
                return "Success";
            }

            return null;
        }

        private static JsonNode authenticate( String email, String password ) {
            if( email == null || password == null ) return null;

            Query q = new Query();
            q.select(new ArrayList<String>())
                .from("users")
                .where("email", "=", email)
                .andWhere("password", "=", Crypto.encryptAES(password));

            ObjectNode result = controllers.HttpHelper.find(q.query, User.fields);

            if(result == null ) return null;

            user_id = result.get("ID").toString();
            user_id = user_id.substring(1, user_id.length()-1);

            return result.get("ID");
        }
    }

    public static class Login {
    
        public String email;
        public String password;
        protected static String user_id = null;

        public String validate() {
            if (this.authenticate(email, password) == null) {
              return "Invalid user or password";
            }
            return null;
        }
        
        private static JsonNode authenticate( String email, String password ) {
        	if( email == null || password == null ) return null;

        	Query q = new Query();
            q.select(new ArrayList<String>())
            	.from("users")
            	.where("email", "=", email)
            	.andWhere("password", "=", Crypto.encryptAES(password));

        	ObjectNode result = controllers.HttpHelper.find(q.query, User.fields);
        	
        	if(result == null ) return null;
        	
        	user_id = result.get("ID").toString();
        	user_id = user_id.substring(1, user_id.length()-1);
        	
        	return result.get("ID");
        }
    }
}