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
import views.html.users;
import views.html.logged_in;

import java.time.LocalDate;
import java.util.HashMap;
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
                    sessionController.setSession("connected", loginUser.id.toString());
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
                Query<Transitions> queryTransition = Ebean.createQuery(Transitions.class);
                Query<Books> queryBooks = Ebean.createQuery(Books.class);
                List<Books> bookList = null;
                if(Transitions.find.query().findCount() > 0){
                    Logger.info("Here");

                    List<Transitions> transitionList = queryTransition.where(Expr.and(Expr.eq("currentOwnerId", loginUser.id),
                            Expr.isNotEmpty("wisher"))).findList();
                    boolean b = transitionList.size() > 0;
                    Logger.info(Boolean.toString(b));
                    bookList = queryBooks.where(Expr.in("transition", transitionList)).findList();
                }
                return ok(logged_in.render(loginUser, getmyWishList(), bookList));
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
            book1.owner = ozgur;
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
            return redirect(routes.HomeController.me());
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

    public HashMap<Books, Users> wishedBooksHashMapByUserId(Long userId) {
        HashMap<Books, Users> bookAndUsersHM = new HashMap<Books, Users>();

        Users user = Users.find.byId(userId);

        Query<Books> queryBookList= Ebean.createQuery(Books.class);
        queryBookList.where(
                Expr.eq("adder", user));
        List<Books> usersBookList = queryBookList.findList();

        for(Books currentBook: usersBookList){
            // Logger.info(currentBook.name);
            Query<Transitions> queryTransition = Ebean.createQuery(Transitions.class);
            List<Transitions> transitionsList = queryTransition.where(
                    Expr.eq("book", currentBook)
            ).findList();

            for(Transitions t: transitionsList) {
                for(Users wisher: t.wisher){
                    Logger.info("BOOK NAME: " + currentBook.name +  " WISHER NAME: " + wisher.name);
                    bookAndUsersHM.put(currentBook, wisher);
                    // Logger.info(wisher.name);
                }
            }
        }
        return bookAndUsersHM;
    }

    // TODO add a user profile screen to see other users profile and reputation

}
