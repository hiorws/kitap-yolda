package controllers;

import models.Books;
import models.Users;
import play.mvc.Controller;
import play.mvc.Result;
import services.Counter;
import views.html.books;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

public class BookController extends Controller {

    public void addBook(String bookName, Users bookAdder, String bookAuthor, Boolean bookAvailable) {
        Books newBook = new Books();
        newBook.name = bookName;
        newBook.adder = bookAdder;
        newBook.author = bookAuthor;
        newBook.isAvailable = bookAvailable;

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
        return ok(books.render(booksList));
    }

}
