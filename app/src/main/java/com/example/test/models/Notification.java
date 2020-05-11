package com.example.test.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.Objects;

public class Notification {


    public static class Types{
        public static final String comment = "comment";
        public static final String like = "like";
    }

    @NonNull
    private String _id;
    @NonNull
    private String _user_id; // to whom the notification connect to
    @NonNull
    private String _creator; // who done the notification
    @NonNull
    private String _type; // "like" "comment" "follow"(you can add)
    @Nullable
    private String _post_id; // (Optional) the post id that the notification connect to
    @NonNull
    private Date _date;

    private Notification() {
    }

    public Notification(@NonNull String _id, @NonNull String _user_id, @NonNull String _creator, @NonNull String _type, @Nullable String _post_id, @NonNull Date _date) {
        this._id = _id;
        this._user_id = _user_id;
        this._creator = _creator;
        this._type = _type;
        this._post_id = _post_id;
        this._date = _date;
    }

    @NonNull
    public String get_id() {
        return _id;
    }

    public void set_id(@NonNull String _id) {
        this._id = _id;
    }

    @NonNull
    public String get_user_id() {
        return _user_id;
    }

    public void set_user_id(@NonNull String _user_id) {
        this._user_id = _user_id;
    }

    @NonNull
    public String get_creator() {
        return _creator;
    }

    public void set_creator(@NonNull String _creator) {
        this._creator = _creator;
    }

    @NonNull
    public String get_type() {
        return _type;
    }

    public void set_type(@NonNull String _type) {
        this._type = _type;
    }

    @Nullable
    public String get_post_id() {
        return _post_id;
    }

    public void set_post_id(@Nullable String _post_id) {
        this._post_id = _post_id;
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
        Notification that = (Notification) o;
        return _id.equals(that._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }


    @Override
    public String toString() {
        return "Notification{" +
                "_id='" + _id + '\'' +
                ", _user_id='" + _user_id + '\'' +
                ", _creator='" + _creator + '\'' +
                ", _type='" + _type + '\'' +
                ", _post_id='" + _post_id + '\'' +
                ", _date=" + _date +
                '}';
    }
}
