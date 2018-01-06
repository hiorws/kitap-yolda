package controllers;

import io.ebean.Ebean;
import models.Users;

import static play.mvc.Controller.session;

public class SessionController {

    private static SessionController instance = null;
    private SessionController() {
        // Exists only to defeat instantiation.
    }
    public void setSession(String key, String value){
        session(key, value);
    }
    public String getSessionValue(String key){
        return session(key);
    }
    public void removeSession(String key){
        session().remove(key);
    }
    public Users findUserWithSession(String key){
        String value = getSessionValue(key);
        return Ebean.find(Users.class).where().eq("id", value).findOne();
    }

    public static SessionController getInstance() {
        if(instance == null) {
            instance = new SessionController();
        }
        return instance;
    }
}