package models;

import com.typesafe.config.Optional;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Books extends Model {

    @Id
    @Column(name = "book_index")
    public Long id;

    @Optional
    public String ISBN;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public boolean isAvailable;

    @Constraints.Required
    public String author;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Users.class)
    public Users adder;

    @ManyToMany(mappedBy = "book")
    public Transitions transition;

    @Constraints.Required
    public LocalDate additionDate;

    public Books() {
    }

    public Books(@Constraints.Required String name, @Constraints.Required boolean isAvailable, @Constraints.Required String author, @Constraints.Required Users adder, @Constraints.Required LocalDate additionDate) {
        this.name = name;
        this.isAvailable = isAvailable;
        this.author = author;
        this.adder = adder;
        this.additionDate = additionDate;
    }

    public static final Finder<Long, Books> find = new Finder<>(Books.class);
}
