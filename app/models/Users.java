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

    @OneToOne(mappedBy = "wisher")
    public Transitions transition;

    @OneToMany(targetEntity = Books.class, cascade = CascadeType.ALL, mappedBy = "adder")
    public List<Books> books;

    public static final Finder<Long, Users> find = new Finder<>(Users.class);

}