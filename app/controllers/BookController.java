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

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class BookController extends Controller {
    private SessionController sessionController;
    @Inject
    FormFactory formFactory;

    public BookController(){
        sessionController = SessionController.getInstance();
    }
    public void addBook(String bookName, Users bookAdder, String bookAuthor, Boolean bookAvailable, String bookIsbn) {
        Books newBook = new Books();
        newBook.name = bookName;
        newBook.adder = bookAdder;
        newBook.author = bookAuthor;
        newBook.isAvailable = bookAvailable;
        newBook.adder = bookAdder;
        newBook.ISBN = bookIsbn != "" ? bookIsbn : "";
        newBook.additionDate = LocalDate.now();
        Logger.info(newBook.ISBN);
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
        Logger.info(bookIsbn);
        Logger.info(bookAvailable);

        Users loginUser = sessionController.findUserWithSession("connected");
        if(loginUser!= null){
            if(bookAvailable == null){
                addBook(bookName,loginUser,bookAuthor,false,bookIsbn);
            }
            else{
                addBook(bookName,loginUser,bookAuthor,true,bookIsbn);
            }
        }
        return redirect(routes.HomeController.me());
    }

    private boolean checkIfAlreadyWished(Books book){

        Users currentUser = sessionController.findUserWithSession("connected");
        Query<Transitions> query = Ebean.createQuery(Transitions.class);
        List<Transitions> transitions = query.where(
                Expr.and(Expr.eq("wisher", currentUser),
                        Expr.eq("book", book))).findList();
        return transitions.size() < 1;
    }


    public Result getBookPage(Long bookID){
        Books book = Books.find.byId(bookID);

        if(book != null){
            return ok(bookinfo.render(book, sessionController.findUserWithSession("connected"), checkIfAlreadyWished(book)));
        }
        else{
            return ok(home.render());
        }
    }
    public Result searchBook(){
        DynamicForm dynamicForm = formFactory.form().bindFromRequest();
        String searchBook = dynamicForm.get("search_parameter");

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
        transition.book.add(Books.find.byId(Long.parseLong(bookId)));
        transition.wisher.add(Users.find.byId(currentUser.id));
        transition.wishDate = LocalDate.now();
        transition.save();
        return redirect(routes.HomeController.me());

    }



}
