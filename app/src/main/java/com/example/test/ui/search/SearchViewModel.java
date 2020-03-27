package com.example.test.ui.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SearchViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<Post>> posts;
    public LiveData<List<Post>> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<List<Post>>();
            loadPosts();
        }
        return posts;
    }

    private void loadPosts() {
        // Do an asynchronous operation to fetch users.

        Database.Post.getPosts(new Consumer<List<Post>>() {
            @Override
            public void accept(List<Post> _posts) {
                Log.d("Posts","get data from server");
                posts.setValue(_posts);
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                Log.e("Posts",e.getMessage().toString());
                e.printStackTrace();
            }
        });


//                .getCurrentUser(new Consumer<User>() {
//            @Override
//            public void accept(User user) {
//
//            }
//        }, new Consumer<Exception>() {
//            @Override
//            public void accept(Exception e) {
//                e.printStackTrace();
//            }
//        });
    }
}
