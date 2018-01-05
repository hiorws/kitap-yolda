package models;

import com.typesafe.config.Optional;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Book extends Model {

    @Id
    public Long id;

    @Optional
    public String ISBN;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public boolean isAvailable;

    @Constraints.Required
    public String author;

    @Constraints.Required
    public Reader adder;

    @Constraints.Required
    public Date additionDate;

    public Book(@Constraints.Required String name, @Constraints.Required boolean isAvailable, @Constraints.Required String author, @Constraints.Required Reader adder, @Constraints.Required Date additionDate) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.author = author;
        this.adder = adder;
        this.additionDate = additionDate;
    }
}
