package com.example.test.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Post implements Serializable {

    private String _id;
    private String _imageUrl;
    private String _userId;
    private String _content;
    private Coordinate _coordinate;
    private Date _date;


    private Post() {
    }

    public Post(@NonNull final String _id, @NonNull final String _imageUrl, @NonNull final String _userId, @NonNull final  String _content, @Nullable final Coordinate _coordinate, @NonNull final  Date _date) {
        this._id = _id;
        this._imageUrl = _imageUrl;
        this._userId = _userId;
        this._content = _content;
        this._coordinate = _coordinate;
        this._date = _date;
    }

    public Post(@NonNull final Post post){
        this._id = post._id;
        this._imageUrl = post._imageUrl;
        this._userId = post._userId;
        this._content = post._content;
        this._coordinate = post._coordinate;
        this._date = post._date;
    }
    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull final String _id) {
         this._id = _id;
    }
    @NonNull
    public String get_imageUrl() {
        return _imageUrl;
    }

    public void set_imageUrl(@NonNull final String _imageUrl) {
        this._imageUrl = _imageUrl;
    }
    @NonNull
    public String get_userId() {
        return _userId;
    }

    public void set_userId(@NonNull final String _userId) {
        this._userId = _userId;
    }
    @NonNull
    public String get_content() {
        return _content;
    }

    public void set_content(@NonNull final String _content) {
        this._content = _content;
    }
    @Nullable
    public Coordinate get_coordinate() {
        return _coordinate;
    }

    public void set_coordinate(@Nullable final Coordinate _coordinate) {
        this._coordinate = _coordinate;
    }
    @NonNull
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


    @NotNull
    @Override
    public String toString() {
        return "Post{" +
                "_id='" + _id + '\'' +
                ", _imageUrl='" + _imageUrl + '\'' +
                ", _userId='" + _userId + '\'' +
                ", _content='" + _content + '\'' +
                ", _coordinate=" + _coordinate +
                ", _date=" + _date +
                '}';
    }
}
