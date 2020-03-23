package com.example.test.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class Comment implements Serializable {

    private String _id;
    private String _postId;
    private String _userId;
    private String _content;

    public Comment() {
    }

    public Comment(@NonNull final String _id,@NonNull final String _postId,@NonNull final String _userId,@NonNull final String _content) {
        this._id = _id;
        this._postId = _postId;
        this._userId = _userId;
        this._content = _content;
    }

    public Comment(@NonNull final Comment comment){
        this._id = comment._id;
        this._postId = comment._postId;
        this._userId = comment._userId;
        this._content = comment._content;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull final String _id) {
        this._id = _id;
    }

    public String get_postId() {
        return _postId;
    }

    public void set_postId(@NonNull final String _postId) {
        this._postId = _postId;
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
                '}';
    }

}
