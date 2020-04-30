package com.example.test.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Comment implements Serializable {

    @NonNull
    private String _id;
    @NonNull
    private String _postId;
    @NonNull
    private String _userId;
    @NonNull
    private String _content;
    @NonNull
    private Date _date;

    private Comment() {
    }

    public Comment(@NonNull String _id, @NonNull String _postId, @NonNull String _userId, @NonNull String _content, @NonNull Date _date) {
        this._id = _id;
        this._postId = _postId;
        this._userId = _userId;
        this._content = _content;
        this._date = _date;
    }

    public Comment(@NonNull final Comment comment){
        this._id = comment._id;
        this._postId = comment._postId;
        this._userId = comment._userId;
        this._content = comment._content;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String get_postId() {
        return _postId;
    }

    public void set_postId(@NonNull String _postId) {
        this._postId = _postId;
    }

    @NonNull
    public String get_userId() {
        return _userId;
    }

    public void set_userId(@NonNull String _userId) {
        this._userId = _userId;
    }

    @NonNull
    public String get_content() {
        return _content;
    }

    public void set_content(@NonNull String _content) {
        this._content = _content;
    }

    @NonNull
    public Date get_date() {
        return _date;
    }

    public void set_date(@NonNull Date _date) {
        this._date = _date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return _id.equals(comment._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Comment(this);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "_id='" + _id + '\'' +
                ", _postId='" + _postId + '\'' +
                ", _userId='" + _userId + '\'' +
                ", _content='" + _content + '\'' +
                ", _date=" + _date +
                '}';
    }
}
