package controllers;

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
import views.html.bookinfo;
import views.html.books;
import views.html.home;
import views.html.test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class BookController extends Controller {
    private SessionController sessionController;
    @Inject
    FormFactory formFactory;

    public BookController(){
        sessionController = SessionController.getInstance();
    }
    public void addBook(String bookName, Users bookAdder, String bookAuthor, Boolean bookAvailable, String bookIsbn, String bookCondition) {
        Books newBook = new Books();
        newBook.name = bookName;
        newBook.adder = bookAdder;
        newBook.owner = bookAdder;
        newBook.author = bookAuthor;
        newBook.isAvailable = bookAvailable;
        newBook.adder = bookAdder;
        newBook.ISBN = bookIsbn != "" ? bookIsbn : "";
        newBook.condition = bookCondition != "" ? bookCondition : "";
        newBook.additionDate = LocalDate.now();
        // Logger.info(newBook.ISBN);
        newBook.save();

    }

    public void deleteBook(Long bookId) {
        Books deletedBook = Books.find.byId(bookId);
        if(deletedBook != null){
            deletedBook.delete();
            deletedBook.save();
        }
    }


    public Result listBooks() {
        List<Books> booksList = Books.find.all();
        Users loginUser = sessionController.findUserWithSession("connected");
        if(loginUser != null){
            return ok(books.render(booksList, loginUser));
        }
        else{
            return ok(books.render(booksList, null));

        }
    }
    public Result addBookForm(){
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String bookAvailable = dynamicForm.get("book_available");
        String bookAuthor = dynamicForm.get("book_author");
        String bookName = dynamicForm.get("book_name");
        String bookIsbn = dynamicForm.get("book_isbn");
        String bookCondition = dynamicForm.get("book_condition");
        // Logger.info(bookIsbn);
        // Logger.info(bookAvailable);

        Users loginUser = sessionController.findUserWithSession("connected");
        if(loginUser!= null){
            if(bookAvailable == null){
                addBook(bookName,loginUser,bookAuthor,false,bookIsbn, bookCondition);
            }
            else{
                addBook(bookName,loginUser,bookAuthor,true,bookIsbn, bookCondition);
            }
        }
        return redirect(routes.HomeController.me());
    }

    private boolean checkIfAlreadyWished(Books book){

        Users currentUser = sessionController.findUserWithSession("connected");
        Query<Transitions> query = Ebean.createQuery(Transitions.class);
        List<Transitions> transitions = query.where(
                Expr.and(Expr.and(Expr.eq("wisherList", currentUser),
                        Expr.eq("book", book)),
                        Expr.eq("transitionIsActive", true))).findList();
        for(Transitions t : transitions){
            Logger.info(t.book.name);
        }
        return transitions.size() < 1;
    }


    public Result getBookPage(Long bookID){
        Books book = Books.find.byId(bookID);
        Users currentUser = sessionController.findUserWithSession("connected");
        Query<Transitions> query = Ebean.createQuery(Transitions.class);
        List<Transitions> transitionList = Transitions.find.query().where(Expr.and(Expr.and(
                Expr.eq("book", book), Expr.eq("transitionIsActive", true)),
                Expr.eq("state", 0))).findList();
        if (currentUser != null){
            if(book != null){
                return ok(bookinfo.render(book, currentUser, checkIfAlreadyWished(book), transitionList));
            }
            else{
                return ok(home.render());
            }
        }
        else{
            return ok(home.render());
        }

    }
    public Result searchBook(){
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String searchBook = dynamicForm.get("search_parameter");
        if(searchBook == null){
            Query<Books> query = Ebean.createQuery(Books.class);
            List<Books> bookList = query.findList();

            return ok(books.render(bookList, sessionController.findUserWithSession("connected")));
        }


        return redirect(routes.BookController.showFilter(searchBook));
    }

    public Result showFilter(String searchBook){
        Query<Books> query = Ebean.createQuery(Books.class);
        query.where(
                Expr.or(Expr.icontains("name", searchBook),
                        Expr.icontains("author", searchBook))
        );
        List<Books> bookList = query.findList();

        return ok(books.render(bookList, sessionController.findUserWithSession("connected")));
    }

    public Result getBooksByAuthor(String author) {
        Query<Books> query = Ebean.createQuery(Books.class);
        query.where(
                Expr.eq("author", author));
        List<Books> bookList = query.findList();
        return ok(books.render(bookList, sessionController.findUserWithSession("connected")));
    }

    public Result getBooksByAdder(Long id) {
        Users user = Users.find.byId(id);
        Query<Books> query = Ebean.createQuery(Books.class);
        query.where(
                Expr.eq("adder", user));
        List<Books> bookList = query.findList();
        return ok(books.render(bookList, sessionController.findUserWithSession("connected")));
    }

    public Result wishBook(){
        Users currentUser = sessionController.findUserWithSession("connected");
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String bookId = dynamicForm.get("book_id");
        Transitions transition = new Transitions();
        Books wishedBook = Books.find.byId(Long.parseLong(bookId));
        if(!currentUser.booksOwned.contains(wishedBook) && transition.state == 0){
            transition.book = wishedBook;
            transition.sender = wishedBook.owner;
            transition.receiver = currentUser;
            transition.wisherList.add(currentUser);
            transition.transitionIsActive = true;
            transition.wishDate = LocalDate.now();
            wishedBook.transition.add(transition);
            transition.save();
            wishedBook.save();
        }
        return redirect(routes.HomeController.me());
    }
    public Result bookArrived(){
        Users currentUser = sessionController.findUserWithSession("connected");
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String bookId = dynamicForm.get("book_id");
        String transitionId = dynamicForm.get("transition_id");
        Transitions transition = Transitions.find.byId(Long.parseLong(transitionId));
        Books wishedBook = Books.find.byId(Long.parseLong(bookId));
        if(transition.state == 1){
            transition.state = 0;
            wishedBook.owner = transition.receiver;
            wishedBook.save();
            transition.save();
        }
        return redirect(routes.HomeController.me());
    }

    public Result bookShipped(){
        Users currentUser = sessionController.findUserWithSession("connected");
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String transitionId = dynamicForm.get("transition_id");
        Transitions transition = Transitions.find.byId(Long.parseLong(transitionId));
        transition.state = 1;
        transition.save();
        return redirect(routes.HomeController.me());
    }

    public Result takeBackWish() {
        Users currentUser = sessionController.findUserWithSession("connected");
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String transition_id = dynamicForm.get("transition_id");
        String book_id = dynamicForm.get("book_id");
        if(transition_id != null){
            Transitions transitions = Transitions.find.byId(Long.parseLong(transition_id));
            transitions.transitionIsActive = false;
            transitions.save();
        }
        else{
            Books book = Books.find.byId(Long.parseLong(book_id));
            Transitions tr = Transitions.find.query().where(Expr.and(Expr.and(Expr.eq("book", book),
                    Expr.eq("wisherList", currentUser)), Expr.eq("transitionIsActive", true))).findOne();
            tr.transitionIsActive = false;
            tr.save();
        }

        return redirect(routes.HomeController.me());
    }

    public Result test() {
        Logger.info("TEST FUNCTION CALLED");
        Users currentUser = sessionController.findUserWithSession("connected");

        HashMap<Books, Users> bookAndUsersHM = new HashMap<Books, Users>();

        // List<Transitions> tl = wishedBooksById(Long.parseLong("1"));

        Query<Books> queryBookList= Ebean.createQuery(Books.class);
        queryBookList.where(
                Expr.eq("adder", currentUser));
        List<Books> usersBookList = queryBookList.findList();


        Books book = Books.find.byId(2L);
/*
        if(book.transition != null){
            Logger.info(book.transition.get(0).wisher.get(0).name);
        }
*/
//        Logger.info("Context of the Books and Wishers");
//        Set<Map.Entry<Books, Users>> hashSet = bookAndUsersHM.entrySet();
//        for(Map.Entry entry:hashSet ) {
//
//            System.out.println("Book="+entry.getKey().+", Wisher="+entry.getValue());
//        }

        return ok(test.render(currentUser));
    }

}
