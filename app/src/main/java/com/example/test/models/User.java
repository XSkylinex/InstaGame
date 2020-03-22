package com.example.test.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    private String _id;
    private String _email;
    private String _userName;
    private boolean _isAdmin;

    private User(){}

    public User(@NonNull final String _id, @NonNull final String _email, @NonNull final String _userName) {
        this._id = _id;
        this._email = _email;
        this._userName = _userName;
        this._isAdmin = false;
    }

    private User(@NonNull final User user){
        this._id = user._id;
        this._email = user._email;
        this._userName = user._userName;
        this._isAdmin = user._isAdmin;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull final String _id) {
        this._id = _id;
    }

    @NonNull
    public String get_email() {
        return _email;
    }

    public void set_email(@NonNull final String _email) {
        this._email = _email;
    }

    @NonNull
    public String get_userName() {
        return _userName;
    }

    public void set_userName(@NonNull final String _userName) {
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

    @NonNull
    @Override
    protected User clone() {
        return new User(this);
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
