package com.example.test.models;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String _id;
    private String _email;
    private String _userName;
    private boolean _isAdmin;

    private User(){}

    public User(String _id, String _email, String _userName) {
        this._id = _id;
        this._email = _email;
        this._userName = _userName;
        this._isAdmin = false;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_userName() {
        return _userName;
    }

    public void set_userName(String _userName) {
        this._userName = _userName;
    }

    public boolean is_isAdmin() {
        return _isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return _id.equals(user._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", _email='" + _email + '\'' +
                ", _userName='" + _userName + '\'' +
                ", _isAdmin=" + _isAdmin +
                '}';
    }
}
