package ch.trivadis.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Andy Moncsek on 18.02.16.
 */
@Document
public class Users {
    @Id
    private String _id;
    private String username;
    private String firstName;
    private String lastName;
    private String address;


    public Users(String _id, String username, String firstName, String lastName, String address) {
        this._id = _id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Users() {

    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
