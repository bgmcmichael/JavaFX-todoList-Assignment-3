package sample;

/**
 * Created by fenji on 9/12/2016.
 */
public class User {
    public String emailAddress = "";
    public String firstName = "";
    public String lastNAme = "";
    public int id = 0;

    public User(String emailAddress, String firstName, String lastNAme, int id) {
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastNAme = lastNAme;
        this.id = id;
    }
}
