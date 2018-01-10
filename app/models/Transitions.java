package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Transitions extends Model {
/*


    @Constraints.Required
    public boolean isArrived;

    @Constraints.Required
    public boolean isAccepted;

    @Constraints.Required
    public LocalDate arrivalDate;


    @Constraints.Required
    public LocalDate shipDate;

    public static final Finder<Long, Transitions> find = new Finder<>(Transitions.class);
    */
    @Id
    @Column(name = "transition_id")
    public Long id;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Users.class)
    public Users sender;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Users.class)
    @Constraints.Required
    public Users receiver;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = Books.class)
    public Books book;

    @Constraints.Required
    public boolean isArrived;

    @ManyToMany(cascade = CascadeType.ALL)
    public List<Users> wisherList;

    @Constraints.Required
    public boolean isOwnerAccepted;

    @Constraints.Required
    public LocalDate shipDate;

    @Constraints.Required
    public LocalDate wishDate;

    public static final Finder<Long, Transitions> find = new Finder<>(Transitions.class);


}
