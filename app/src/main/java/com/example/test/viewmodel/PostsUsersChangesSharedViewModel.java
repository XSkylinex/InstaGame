package com.example.test.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostsUsersChangesSharedViewModel extends ViewModel {


    private MutableLiveData<Map<Post, User>> postsusers;
    private Map<Post, User> entries;
    public LiveData<Map<Post, User>> getPostsUsers() {
        if (postsusers == null) {
            postsusers = new MutableLiveData<>();
            loadPosts();
        }
        return postsusers;
    }

    private Map<String, User> userMap; // user id to user
    private Map<String, UserListenerCount> stringListenerMap; // user id to user listener
    private Listener postsListener = null;

    private static class UserListenerCount {
        @NonNull
        public String userid;
        @NonNull
        Listener userListener;
        int count;

        UserListenerCount(@NonNull String userid, @NonNull Listener userListener) {
            this.userid = userid;
            this.userListener = userListener;
            this.count = 0;
        }

        void addListener(){
            this.count++;
        }
        void removerListener(){
            this.count--;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserListenerCount that = (UserListenerCount) o;
            return userid.equals(that.userid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid);
        }
    }

    private void loadPosts() {
        // Do an asynchronous operation to fetch users.
        userMap = new HashMap<>();
        stringListenerMap = new HashMap<>();
        entries = new HashMap<>();
        this.postsListener = Database.Post.listenPostsChanges(post -> {
            // added post
            Log.d("PostsUsersChangesSharedViewModel","on post added: "+post.get_id());
            final String postUserId = post.get_userId();
            if (!userMap.containsKey(postUserId)) {
                final Listener userListener = Database.User.listenUser(postUserId, user -> {
                    userMap.put(postUserId,user); //get connect between user and post
                    entries.put(post,user); //display information
                    postsusers.setValue(entries);
                    final UserListenerCount userListenerCount = stringListenerMap.get(postUserId); // sync problem
                    assert userListenerCount != null;
                    userListenerCount.addListener();
                    Log.d("PostsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
                }, e -> {
                    Log.e("PostsUsersChangesSharedViewModel","Error: "+e.getMessage());
                    e.printStackTrace();
                });
                stringListenerMap.put(postUserId,new UserListenerCount(postUserId, userListener));
            }else{
                entries.put(post,userMap.get(postUserId)); //display information
                postsusers.setValue(entries);
                final UserListenerCount userListenerCount = stringListenerMap.get(postUserId);
                assert userListenerCount != null;
                userListenerCount.addListener();
                Log.d("PostsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
            }
        }, post -> {
            // modified post
            Log.d("PostsUsersChangesSharedViewModel","on post modified: "+post.get_id());
            entries.put(post, userMap.get(post.get_userId()));
            postsusers.setValue(entries);
        }, post -> { // remove connection from user when deleted post
            // remove post
            Log.d("PostsUsersChangesSharedViewModel","on post removed: "+post.get_id());
            entries.remove(post);
            postsusers.setValue(entries);
            UserListenerCount userListenerCount = stringListenerMap.get(post.get_userId());
            assert userListenerCount != null;
            userListenerCount.removerListener();//this post not anymore liston to user
            Log.d("PostsUsersChangesSharedViewModel","user listener removed for "+post.get_id()+"\t count: "+ userListenerCount.count);
            if (userListenerCount.count <= 0){ // if no one left listen to this user
                userListenerCount.userListener.remove(); // remove connection
                stringListenerMap.remove(post.get_userId()); //
                userMap.remove(post.get_userId()); //
                Log.d("PostsUsersChangesSharedViewModel","user listener removed for "+post.get_id()+"\t removed");
            }


        }, e -> {
            // on error
            Log.e("PostsUsersChangesSharedViewModel","Error: "+e.getMessage());
            e.printStackTrace();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.postsListener!=null){
            this.postsListener.remove();
        }
        userMap.clear();
        stringListenerMap.forEach((userId, userListenerCount) -> {
            Log.d("PostsUsersChangesSharedViewModel","clear connection for "+userId+" had "+userListenerCount.count+" connection");
            userListenerCount.removerListener();
        });
        stringListenerMap.clear();
    }
}