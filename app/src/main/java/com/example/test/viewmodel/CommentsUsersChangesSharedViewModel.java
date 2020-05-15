package com.example.test.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.test.contollers.database.Database;
import com.example.test.models.Comment;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CommentsUsersChangesSharedViewModel extends ViewModel {


    private String curPostId=null;
    private MutableLiveData<Map<Comment, User>> commentsusers;
    private Map<Comment, User> entries;
    public LiveData<Map<Comment, User>> getCommentsUsers(String postId) {
        if(curPostId != null && !curPostId.equals(postId)){
            clear();
            curPostId=null;
        }
        if (commentsusers == null) {
            curPostId = postId;
            commentsusers = new MutableLiveData<>();
            loadComments(postId);
        }
        return commentsusers;
    }


    private Map<String, User> userMap; // user id to user
    private Map<String, UserListenerCount> stringListenerMap; // user id to user listener
    private Listener commentListener=null;

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


    private void loadComments(String postId) {
        // Do an asynchronous operation to fetch users.
        userMap = new HashMap<>();
        stringListenerMap = new HashMap<>();
        entries = new HashMap<>();
        this.commentListener = Database.Comment.listenCommentsChangesFromPost(postId,() -> {
            // on starting retrieve data
            Log.i("CommentsUsersChangesSharedViewModel","on start");
            commentsusers.setValue(new HashMap<>());
        },comment -> {
            // added comment
            Log.d("CommentsUsersChangesSharedViewModel","on comment added: "+comment.get_id());
            final String postUserId = comment.get_userId();
            if (!userMap.containsKey(postUserId)) {
                final Listener userListener = Database.User.listenUser(postUserId, user -> {
                    userMap.put(postUserId,user); //get connect between user and comment
                    entries.put(comment,user); //display information
                    commentsusers.setValue(entries);
                    final UserListenerCount userListenerCount = stringListenerMap.get(postUserId); // sync problem
                    assert userListenerCount != null;
                    userListenerCount.addListener();
                    Log.d("CommentsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
                }, e -> {
                    Log.e("CommentsUsersChangesSharedViewModel","Error: "+e.getMessage());
                    e.printStackTrace();
                });
                stringListenerMap.put(postUserId,new UserListenerCount(postUserId, userListener));
            }else{
                entries.put(comment,userMap.get(postUserId)); //display information
                commentsusers.setValue(entries);
                final UserListenerCount userListenerCount = stringListenerMap.get(postUserId);
                assert userListenerCount != null;
                userListenerCount.addListener();
                Log.d("PostsUsersChangesSharedViewModel","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
            }
        }, comment -> {
            // modified comment
            Log.d("CommentsUsersChangesSharedViewModel","on comment modified: "+comment.get_id());
            entries.put(comment, userMap.get(comment.get_userId()));
            commentsusers.setValue(entries);
        }, comment -> { // remove connection from user when deleted comment
            // remove comment
            Log.d("CommentsUsersChangesSharedViewModel","on comment removed: "+comment.get_id());
            entries.remove(comment);
            commentsusers.setValue(entries);
            UserListenerCount userListenerCount = stringListenerMap.get(comment.get_userId());
            assert userListenerCount != null;
            userListenerCount.removerListener();//this comment not anymore liston to user
            Log.d("CommentsUsersChangesSharedViewModel","user listener removed for "+comment.get_id()+"\t count: "+ userListenerCount.count);
            if (userListenerCount.count <= 0){ // if no one left listen to this user
                userListenerCount.userListener.remove(); // remove connection
                stringListenerMap.remove(comment.get_userId()); //
                userMap.remove(comment.get_userId()); //
                Log.d("CommentsUsersChangesSharedViewModel","user listener removed for "+comment.get_id()+"\t removed");
            }


        },() -> {
            // on finish retrieve data
            Log.i("CommentsUsersChangesSharedViewModel","on finish");

        }, e -> {
            // on error
            Log.e("CommentsUsersChangesSharedViewModel","Error: "+e.getMessage());
            e.printStackTrace();
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clear();
    }

    private void clear(){
        if (this.commentListener!=null){
            this.commentListener.remove();
        }
        userMap.clear();
        stringListenerMap.forEach((userId, userListenerCount) -> {
            Log.d("CommentsUsersChangesSharedViewModel","clear connection for "+userId+" had "+userListenerCount.count+" connection");
            userListenerCount.removerListener();
        });
        stringListenerMap.clear();

        commentsusers = null;
        commentListener = null;
        userMap = null;
        stringListenerMap = null;
    }
}
