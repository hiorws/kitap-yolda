package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

@Entity
public class Users extends Model {

    @Id
    @Column(name="users_id")
    public Long id;

    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String email;

    @ManyToMany(mappedBy = "wisher")
    public Transitions transition;

    @OneToMany(targetEntity = Books.class, cascade = CascadeType.ALL, mappedBy = "adder")
    public List<Books> booksAdded;

    @OneToMany(targetEntity = Transitions.class, cascade = CascadeType.ALL, mappedBy = "currentOwner")
    public List<Transitions> ownedTransitions;

    @OneToMany(targetEntity = Books.class, cascade = CascadeType.ALL, mappedBy = "owner")
    public List<Books> booksOwned;

    public static final Finder<Long, Users> find = new Finder<>(Users.class);

    @Override
    public boolean equals(Object object)
    {
        boolean isEqual= false;

        if (object != null && object instanceof Users)
        {
            isEqual = (this.id == ((Users) object).id);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        int hashCode = (int) (this.id * 17);
        return hashCode;
    }

}