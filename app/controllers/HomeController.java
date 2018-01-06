package controllers;

import com.google.inject.Inject;
import io.ebean.Ebean;
import models.Books;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import java.time.LocalDate;

public class HomeController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result loginSubmit() {
            if(request().method().equals("POST")){

            DynamicForm dynamicForm = formFactory.form().bindFromRequest();
            String loginUsername = dynamicForm.get("username");
            String loginPassword = dynamicForm.get("password");
            Users loginUser = Ebean.find(Users.class).where().eq("username", loginUsername).findOne();

            if (loginUser != null) {
                if (loginUser.password.equals(loginPassword)) {
                    Logger.info("Username is: " + loginUsername);
                    Logger.info("Password is: " + loginPassword);
                    session("connected", loginUsername);
                    return ok(logged_in.render(loginUser));
                } else {
                    Logger.info("Username is: " + loginUsername);
                    Logger.info("Password is: " + loginPassword);
                    return  ok(home.render());
                }
            }
                return ok(home.render());

            }
            else{
                String user = session("connected");
                Users loginUser = Ebean.find(Users.class).where().eq("username", user).findOne();
                if(loginUser != null){
                    return ok(logged_in.render(loginUser));
                }
                return ok(home.render());

            }

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

        session("connected", registerUsername);
        return ok(logged_in.render(newUser));
    }

    public Result setup() {

        Ebean.execute(() -> {
            Users newUser = new Users();
            newUser.username = "admin";
            newUser.password = "123";
            newUser.email = "admin@test.com";
            newUser.name = "Admin Superuser";
            newUser.save();


            Users ozgur = new Users();
            ozgur.username = "hiorws";
            ozgur.password = "123";
            ozgur.email = "ozgurfiratcinar@gmail.com";
            ozgur.name = "Ozgur Firat Cinar";
            ozgur.save();

            Users ozan = new Users();
            ozan.username = "ozan";
            ozan.password = "123";
            ozan.email = "ozanonurtek@gmail.com";
            ozan.name = "Ozan Onur Tek";
            ozan.save();

            Books book1 = new Books();
            book1.isAvailable = true;
            book1.author = "Douglas Hofstadter";
            book1.adder = newUser;
            book1.name = "Gödel, Escher, Bach";
            book1.additionDate = LocalDate.now();
            book1.save();
            ozan.save();
            
        });

        return ok(home.render());
    }

    public Result home() {
        String user = session("connected");
        Users loginUser = Ebean.find(Users.class).where().eq("username", user).findOne();
        if(loginUser != null){
            return ok(logged_in.render(loginUser));
        }
        else{
            return ok(home.render());
        }
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
