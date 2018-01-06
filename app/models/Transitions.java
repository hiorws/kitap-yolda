package models;

import com.typesafe.config.Optional;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.awt.print.Book;
import java.time.LocalDate;

@Entity
public class Transitions extends Model {

    @Id
    @Column(name = "transition_id")
    public Long id;

    @OneToOne
    @JoinColumn(name = "book_index")
    public Books book;

    @OneToOne
    @JoinColumn(name = "user_id")
    @Constraints.Required
    public Users wisher;

    @Constraints.Required
    public boolean isArrived;

    @Constraints.Required
    public boolean isAccepted;

    @Constraints.Required
    public LocalDate arrivalDate;

    @Constraints.Required
    public LocalDate wishDate;

    @Constraints.Required
    public LocalDate shipDate;

    public static final Finder<Long, Transitions> find = new Finder<>(Transitions.class);
}
