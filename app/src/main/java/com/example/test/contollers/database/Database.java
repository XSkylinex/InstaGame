package com.example.test.contollers.database;

public class Database {


    private Database() {
    }

    public static UserApi           User       = new UserApi();
    public static PostAPI           Post       = new PostAPI();
    public static CommentAPI        Comment    = new CommentAPI();
    public static NotificationAPI   Notification    = new NotificationAPI();

}
