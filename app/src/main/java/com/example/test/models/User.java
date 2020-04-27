package com.example.test.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

    @NonNull
    private String _id;
    @NonNull
    private String _email;
    @NonNull
    private String _userName;
    @Nullable
    private String _imageUrl;
    private boolean _isAdmin;

    private User(){}


    private User(@NonNull final User user){
        this._id = user._id;
        this._email = user._email;
        this._userName = user._userName;
        this._imageUrl = user._imageUrl;
        this._isAdmin = user._isAdmin;
    }

    public User(@NonNull String _id, @NonNull String _email, @NonNull String _userName, @Nullable String _imageUrl) {
        this._id = _id;
        this._email = _email;
        this._userName = _userName;
        this._imageUrl = _imageUrl;
        this._isAdmin = false;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String get_email() {
        return _email;
    }

    public void set_email(@NonNull String _email) {
        this._email = _email;
    }

    @NonNull
    public String get_userName() {
        return _userName;
    }

    public void set_userName(@NonNull String _userName) {
        this._userName = _userName;
    }

    @Nullable
    public String get_imageUrl() {
        return _imageUrl;
    }

    public void set_imageUrl(@Nullable String _imageUrl) {
        this._imageUrl = _imageUrl;
    }

    public boolean is_isAdmin() {
        return _isAdmin;
    }

    public void set_isAdmin(boolean _isAdmin) {
        this._isAdmin = _isAdmin;
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
                ", _imageUrl='" + _imageUrl + '\'' +
                ", _isAdmin=" + _isAdmin +
                '}';
    }
}
