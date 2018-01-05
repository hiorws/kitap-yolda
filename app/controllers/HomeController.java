package controllers;

import com.google.inject.Inject;
import io.ebean.Ebean;
import models.Reader;
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
        Logger.info("Username is: " + dynamicForm.get("username"));
        Logger.info("Password is: " + dynamicForm.get("password"));

        return  ok(logged_in.render("hey"));}

    public Result home() {
        Ebean.execute(() -> {
            Reader user = new Reader();
            user.name = "oz";
            user.id = 2L;
            user.password = "123";
            // code running in "REQUIRED" transactional scope
            // ... as "REQUIRED" is the default TxType
            System.out.println(Ebean.currentTransaction());
            user.save();
        });


        return  ok(home.render());}

        public Result getAllUsers() {
            Reader oz = Reader.find.byId(2L);

            return ok(users.render(oz.name));
        }
}
