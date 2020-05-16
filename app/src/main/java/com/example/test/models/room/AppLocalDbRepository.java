package com.example.test.models.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.test.models.Post;
import com.example.test.models.User;

@Database(entities = {User.class, Post.class}, version = 3)
@TypeConverters({DateConverters.class})
public abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract PostDao postDao();
}
