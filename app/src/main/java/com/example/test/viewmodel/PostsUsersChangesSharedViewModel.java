package com.example.test.viewmodel;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PostsUsersChangesSharedViewModel extends ViewModel {


    private MutableLiveData<Map<Post, User>> postsusers;
    private Map<Post, User> entries;
    public LiveData<Map<Post, User>> getPostsUsers() {
        if (postsusers == null) {
            entries = new HashMap<>();
            postsusers = new MutableLiveData<>();
            loadPosts(postUserEntry -> {
                entries.put(postUserEntry.getKey(), postUserEntry.getValue());
                postsusers.setValue(entries);
            }, post -> {
                final User user = entries.get(post);
                entries.remove(post);
                entries.put(post,user);
                postsusers.setValue(entries);
            }, post -> {
                entries.remove(post);
                postsusers.setValue(entries);
            });
        }
        return postsusers;
    }

    private Map<String, User> userMap; // user id to user
    private Map<String, UserListenerCount> stringListenerMap; // user id to user listener
    private Listener postsListener = null;

    private static class UserListenerCount {
        @NonNull
        public String userid;
        @Nullable
        Listener userListener;
        int count;

        UserListenerCount(@NonNull String userid, @Nullable Listener userListener) {
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

    private void loadPosts(Consumer<Map.Entry<Post,User>> onAdded,Consumer<Post> onUpdated,Consumer<Post> onRemoved) {
        // Do an asynchronous operation to fetch users.
        userMap = new HashMap<>();
        stringListenerMap = new HashMap<>();
        this.postsListener = Database.Post.listenPostsChanges(() -> {
            postsusers.setValue(new HashMap<>());
        },post -> {
            // added post
            Log.d("PostsUsersChangesSharedViewModel","on post added: "+post.get_id());
            final String postUserId = post.get_userId();
            if (!userMap.containsKey(postUserId)) {

                final UserListenerCount userListenerCount = new UserListenerCount(postUserId, null);
                userListenerCount.addListener();
                stringListenerMap.put(postUserId,userListenerCount);
                userListenerCount.userListener = Database.User.listenUser(postUserId, user -> {

                    Set<Map.Entry<String, User>> entries1 = userMap.entrySet();
                    entries1 = entries1.stream().filter(stringUserEntry -> stringUserEntry.getValue().equals(user)).collect(Collectors.toSet());
                    for (Map.Entry<String, User> userEntry : entries1) {
                        userMap.put(userEntry.getKey(),user);
                    }
                    userMap.put(postUserId,user); //get connect between user and post
                    onAdded.accept(new AbstractMap.SimpleEntry<>(post,user)); //display information
                    postsusers.setValue(this.entries);

                    Log.d("PostsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
                }, e -> {
                    Log.e("PostsUsersChangesSharedViewModel","Error: "+e.getMessage());
                    e.printStackTrace();
                });
            }else{
                onAdded.accept(new AbstractMap.SimpleEntry<>(post,userMap.get(postUserId))); //display information
                postsusers.setValue(entries);
                final UserListenerCount userListenerCount = stringListenerMap.get(postUserId);
                assert userListenerCount != null;
                userListenerCount.addListener();
                Log.d("PostsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
            }
        }, post -> {
            // modified post
//            Log.d("PostsUsersChangesSharedViewModel","on post modified: "+post.get_id());
            Log.d("PostsUsersChangesSharedViewModel","on post modified: "+post.toString());
            onUpdated.accept(post);
            postsusers.setValue(entries);
        }, post -> { // remove connection from user when deleted post
            // remove post
            Log.d("PostsUsersChangesSharedViewModel","on post removed: "+post.get_id());
            onRemoved.accept(post);
            postsusers.setValue(entries);
            UserListenerCount userListenerCount = stringListenerMap.get(post.get_userId());
            if (userListenerCount == null) return;

            Log.d("PostsUsersChangesSharedViewModel","on post removed: "+post.get_userId()+"\t"+userListenerCount.count);
            userListenerCount.removerListener();//this post not anymore liston to user
            Log.d("PostsUsersChangesSharedViewModel","user listener removed for "+post.get_id()+"\t count: "+ userListenerCount.count);
            if (userListenerCount.count <= 0){ // if no one left listen to this user
                userListenerCount.userListener.remove(); // remove connection
                stringListenerMap.remove(post.get_userId()); //
                userMap.remove(post.get_userId()); //
                Log.d("PostsUsersChangesSharedViewModel","user listener removed for "+post.get_id()+"\t removed");
            }


        }, () -> {

        },e -> {
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
