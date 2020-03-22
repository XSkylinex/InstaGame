package com.example.test.models;

import android.location.Location;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Post implements Serializable {

    private String _id;
    private String _imageUrl;
    private String _userId;
    private String _content;
    private Location _location;
    private Date _date;


    public Post(@NonNull final String _id,@NonNull final String _imageUrl,@NonNull final String _userId,@NonNull final  String _content,@NonNull final  Location _location,@NonNull final  Date _date) {
        this._id = _id;
        this._imageUrl = _imageUrl;
        this._userId = _userId;
        this._content = _content;
        this._location = _location;
        this._date = _date;
    }

    public Post(@NonNull final Post post){
        this._id = post._id;
        this._imageUrl = post._imageUrl;
        this._userId = post._userId;
        this._content = post._content;
        this._location = post._location;
        this._date = post._date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull final String _id) {
         this._id = _id;
    }

    public String get_imageUrl() {
        return _imageUrl;
    }

    public void set_imageUrl(@NonNull final String _imageUrl) {
        this._imageUrl = _imageUrl;
    }

    public String get_userId() {
        return _userId;
    }

    public void set_userId(@NonNull final String _userId) {
        this._userId = _userId;
    }

    public String get_content() {
        return _content;
    }

    public void set_content(@NonNull final String _content) {
        this._content = _content;
    }

    public Location get_location() {
        return _location;
    }

    public void set_location(@NonNull final Location _location) {
        this._location = _location;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(@NonNull final Date _date) {
        this._date = _date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return _id.equals(post._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @NonNull
    @Override
    protected Post clone() {
        return new Post(this);
    }


    @Override
    public String toString() {
        return "Post{" +
                "_id='" + _id + '\'' +
                ", _imageUrl='" + _imageUrl + '\'' +
                ", _userId='" + _userId + '\'' +
                ", _content='" + _content + '\'' +
                ", _location=" + _location +
                ", _date=" + _date +
                '}';
    }
}
