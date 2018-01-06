package controllers;

import io.ebean.Ebean;
import models.Books;
import models.Users;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class BookController extends Controller {
    @Inject
    FormFactory formFactory;
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
        String user = session("connected");
        Users loginUser = Ebean.find(Users.class).where().eq("id", user).findOne();
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

        String user = session("connected");
        Users loginUser = Ebean.find(Users.class).where().eq("id", user).findOne();
        if(loginUser!= null){
            if(bookAvailable == null){
                addBook(bookName,loginUser,bookAuthor,false,bookIsbn);
            }
            else{
                addBook(bookName,loginUser,bookAuthor,true,bookIsbn);
            }
        }
        return ok(logged_in.render(loginUser));
    }

    public Result getBookPage(Long bookID){
        Books book = Books.find.byId(bookID);
        if(book != null){
            return ok(bookinfo.render(book));
        }
        else{
            return ok(home.render());
        }
    }
}
