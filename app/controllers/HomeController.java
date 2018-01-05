package controllers;

import com.google.inject.Inject;
import io.ebean.Ebean;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    FormFactory formFactory;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result loginSubmit() {

        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String loginUsername = dynamicForm.get("username");
        String loginPassword = dynamicForm.get("password");
        Logger.info("Username is: " + loginUsername);
        Logger.info("Password is: " + loginPassword);

        Users loginUser = Ebean.find(Users.class).where().eq("username", loginUsername).findOne();

        if (loginUser != null) {
            if (loginUser.password.equals(loginPassword)) {
                session("connected", loginUsername);
                return ok(logged_in.render("Logged in successfully!"));
            } else {
                return ok(logged_in.render("Check your password!"));
            }
        }
        return ok(logged_in.render("Not granted!"));
    }

    public Result logout() {
        session().remove("connected");
        return ok(home.render());
    }

    public Result registerSubmit() {
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String registerUsername = dynamicForm.get("username");
        String registerPassword = dynamicForm.get("password");
        String registerConfirmPassword = dynamicForm.get("confirm-password");
        String registerEmail = dynamicForm.get("email");
        String registerName = dynamicForm.get("name");

        Logger.info("Username is: " + registerUsername);
        Logger.info("Password is: " + registerPassword);
        Logger.info("Confirm Password is: " + registerConfirmPassword);
        Logger.info("Email is: " + registerEmail);
        Logger.info("Name is: " + registerName);

        Users newUser = new Users();
        newUser.username = registerUsername;
        newUser.password = registerPassword;
        newUser.email = registerEmail;
        newUser.name = registerName;

        newUser.save();

        return ok(logged_in.render("User " + registerUsername + " registered successfully!"));
    }

    public Result setup() {

        Ebean.execute(() -> {
            Users newUser = new Users();
            // newUser.id = 1L;
            newUser.username = "admin";
            newUser.password = "123";
            newUser.email = "admin@test.com";
            newUser.name = "Admin Superuser";

            newUser.save();
        });

        return ok(home.render());
    }

    public Result home() {
        return ok(home.render());
    }

    public Result getAllUsers() {
        Users adminUser = Users.find.byId(1L);
        String user = session("connected");
        if(user != null) {
            return ok(users.render(adminUser.name));
        } else {
            return unauthorized("Oops, you are not authorized!");
        }
    }

}
