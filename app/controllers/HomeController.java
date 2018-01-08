package controllers;

import com.google.inject.Inject;
import io.ebean.Ebean;
import io.ebean.Expr;
import io.ebean.Query;
import models.Books;
import models.Transitions;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.about;
import views.html.home;
import views.html.logged_in;
import views.html.users;

import java.time.LocalDate;
import java.util.List;

public class HomeController extends Controller {

    private SessionController sessionController;
    @Inject
    FormFactory formFactory;

    public HomeController(){
        sessionController = SessionController.getInstance();
    }

    public Result loginSubmit() {
            if(request().method().equals("POST")){

            DynamicForm dynamicForm = formFactory.form().bindFromRequest();
            String loginUsername = dynamicForm.get("username");
            String loginPassword = dynamicForm.get("password");
            Users loginUser = Ebean.find(Users.class).where().eq("username", loginUsername).findOne();

            if (loginUser != null) {
                if (loginUser.password.equals(loginPassword)) {
                    // Logger.info("Username is: " + loginUsername);
                    // Logger.info("Password is: " + loginPassword);
                    sessionController.setSession("connected", loginUser.id.toString());
                    // Logger.info("here");
                    return redirect(routes.HomeController.me());
                } else {

                    return  ok(home.render());
                }
            }
                }
        return ok(home.render());

    }

        public Result me(){
            Users loginUser = sessionController.findUserWithSession("connected");
            if(loginUser != null){

                return ok(logged_in.render(loginUser, getmyWishList()));
            }
            return ok(home.render());
        }

        private List<Transitions> getmyWishList(){
            Users currentUser = sessionController.findUserWithSession("connected");
            Query<Transitions> query = Ebean.createQuery(Transitions.class);
            List<Transitions> transitions = query.where(Expr.eq("wisher", currentUser)).findList();
            return transitions;
        }


    public Result logout() {
        sessionController.removeSession("connected");
        return ok(home.render());
    }

    public Result registerSubmit() {
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String registerUsername = dynamicForm.get("username");
        String registerPassword = dynamicForm.get("password");
        String registerConfirmPassword = dynamicForm.get("confirm-password");
        String registerEmail = dynamicForm.get("email");
        String registerName = dynamicForm.get("name");

//        Logger.info("Username is: " + registerUsername);
//        Logger.info("Password is: " + registerPassword);
//        Logger.info("Confirm Password is: " + registerConfirmPassword);
//        Logger.info("Email is: " + registerEmail);
//        Logger.info("Name is: " + registerName);

        List<Users> userList = Ebean.find(Users.class).where().eq("username", registerName).findList();
        if(userList.size() < 1){
            Users newUser = new Users();
            newUser.username = registerUsername;
            newUser.password = registerPassword;
            newUser.email = registerEmail;
            newUser.name = registerName;

            newUser.save();
            sessionController.setSession("connected", newUser.id.toString());
            return redirect(routes.HomeController.me());
            //return ok(logged_in.render(newUser));
        }
        else{
            return ok(home.render());
        }


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
            book1.adder = ozgur;
            book1.isAvailable = true;
            book1.author = "Douglas Hofstadter";
            book1.ISBN = "9780140179972";
            book1.name = "Gödel, Escher, Bach";
            book1.additionDate = LocalDate.now();
            book1.save();

        });

        return ok(home.render());
    }

    public Result home() {
        Users loginUser = sessionController.findUserWithSession("connected");
        if(loginUser != null){
            return ok(logged_in.render(loginUser, getmyWishList()));
        }
        else{
            return ok(home.render());
        }
    }

    public Result getAllUsers() {
        Users loginUser = sessionController.findUserWithSession("connected");

        if(loginUser != null) {
            return ok(users.render(loginUser));
        } else {
            return unauthorized("Oops, you are not authorized!");
        }
    }

    public Result about() {
        Users loginUser = sessionController.findUserWithSession("connected");
        return ok(about.render(loginUser));

    }

    // TODO add a user profile screen to see other users profile and reputation

}
