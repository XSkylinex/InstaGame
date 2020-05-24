package com.example.test.models.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.test.models.Post;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM Post")
    List<Post> getAll();

    @Query("SELECT * FROM Post WHERE _userId = :uId")
    List<Post> getPostsFromUserId(String uId);

    @Query("SELECT * from Post")
    LiveData<List<Post>> getAllLive();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Post> posts);

    @Delete
    void delete(Post post);

    @Query("DELETE FROM Post WHERE _id = :postId")
    void delete(String postId);
}
