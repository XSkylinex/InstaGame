package com.example.test.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.listener.Listener;

import java.util.List;

public class SharedViewModel extends ViewModel {

    private Listener Postslistener = null;
    private MutableLiveData<List<Post>> posts;
    public LiveData<List<Post>> getPosts() {
        if (posts == null) {
            posts = new MutableLiveData<>();
            loadPosts();
        }
        return posts;
    }

    private void loadPosts() {
        // Do an asynchronous operation to fetch users.

        Postslistener = Database.Post.listenPosts(_posts -> {
            Log.d("Posts", "get data from server");
            posts.setValue(_posts);
        }, e -> {
            Log.e("Posts", "Error: "+e.getMessage());
            e.printStackTrace();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (Postslistener !=null){
            Postslistener.remove();
        }
    }
}
