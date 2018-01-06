package models;

import com.typesafe.config.Optional;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Books extends Model {

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
    public Users adder;

    @Constraints.Required
    public Date additionDate;

    public Books() {

    }

    public Books(@Constraints.Required String name, @Constraints.Required boolean isAvailable, @Constraints.Required String author, @Constraints.Required Users adder, @Constraints.Required Date additionDate) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.author = author;
        this.adder = adder;
        this.additionDate = additionDate;
    }

    public static final Finder<Long, Books> find = new Finder<>(Books.class);
}
