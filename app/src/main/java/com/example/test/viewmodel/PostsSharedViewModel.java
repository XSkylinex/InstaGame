package com.example.test.viewmodel;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.listener.Listener;
import com.example.test.models.room.AppLocalDb;

import java.util.List;

public class PostsSharedViewModel extends ViewModel {

    private Listener Postslistener = null;
    private LiveData<List<Post>> posts;
    public LiveData<List<Post>> getPosts() {
        if (posts == null) {
            posts = AppLocalDb.db.postDao().getAllLive();
            loadPosts();
        }
        return posts;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadPosts() {
        // Do an asynchronous operation to fetch Posts.
        Postslistener = Database.Post.listenPostsChanges(
                () -> {},
                post -> {
                    new AsyncTask<Post, Void, Void>() {
                        @Override
                        protected Void doInBackground(Post... posts) {
                            Log.i("PostsSharedViewModel","add:"+post.toString());
                            AppLocalDb.db.postDao().insertAll(posts);
                            return null;
                        }
                    }.execute(post);
                },
                post -> {
                    new AsyncTask<Post, Void, Void>() {
                        @Override
                        protected Void doInBackground(Post... posts) {
                            Log.i("PostsSharedViewModel","mod:"+post.toString());

                            AppLocalDb.db.postDao().insertAll(posts);
                            return null;
                        }
                    }.execute(post);
                },
                post -> {
                    new AsyncTask<Post, Void, Void>() {
                        @Override
                        protected Void doInBackground(Post... posts) {
                            Log.i("PostsSharedViewModel","rev:"+post.toString());
                            for (Post post : posts) {
                                AppLocalDb.db.postDao().delete(post);
                            }
                            return null;
                        }
                    }.execute(post);
                },
                () -> {},
                e -> {
                    Log.e("Posts", "Error: "+e.getMessage());
                    e.printStackTrace();
                }
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (Postslistener !=null){
            Postslistener.remove();
        }
    }
}
