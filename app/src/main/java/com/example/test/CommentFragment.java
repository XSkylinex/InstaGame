package com.example.test;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapter.CommentAdapter;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Comment;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import com.example.test.ui.main.MainFragment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class CommentFragment extends Fragment {

    private EditText et_comment;
    private Button btn_comment_send;

    private RecyclerView rv_comments;
    private CommentAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Map<String,User> userMap; // user id to user
    private Map<String, CommentFragment.UserListenerCount> stringListenerMap; // user id to user listener

    private Listener postListener=null;

    private static class UserListenerCount {
        @NonNull
        public String userid;
        @NonNull
        public Listener userListener;
        public int count;

        public UserListenerCount(@NonNull String userid, @NonNull Listener userListener) {
            this.userid = userid;
            this.userListener = userListener;
            this.count = 0;
        }

        public void addListener(){
            this.count++;
        }
        public void removerListener(){
            this.count--;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CommentFragment.UserListenerCount that = (CommentFragment.UserListenerCount) o;
            return userid.equals(that.userid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid);
        }
    }

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_comments = view.findViewById(R.id.rv_comments);
        et_comment = view.findViewById(R.id.et_comment);
        btn_comment_send = view.findViewById(R.id.btn_comment_send);

        userMap = new HashMap<>();
        stringListenerMap = new HashMap<>();

        rv_comments.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        rv_comments.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapter(getContext(), new Consumer<User>() {
            @Override
            public void accept(User user) {
                Log.d("PostFragment","travel to user profile :"+user.get_id());
            }
        });
        rv_comments.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String postId = CommentFragmentArgs.fromBundle(getArguments()).getPostId();
        Log.d("CommentFragment",postId);

        Database.Comment.listenCommentsChangesFromPost(postId, new Consumer<Comment>() {
            @Override
            public void accept(Comment comment) {
                //added comment
                Log.d("CommentFragment","added comment : "+comment.get_id());

                final String commentUserId = comment.get_userId();
                if (!userMap.containsKey(commentUserId)) {
                    final Listener userListener = Database.User.listenUser(commentUserId, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            userMap.put(commentUserId,user); //get connect between user and post
                            mAdapter.updateData(comment,user); //display information
                            final UserListenerCount userListenerCount = stringListenerMap.get(commentUserId); // sync problem
                            assert userListenerCount != null;
                            userListenerCount.addListener();
                            Log.d("CommentFragment","user listener added for "+commentUserId+"\t count: "+ userListenerCount.count);
                        }
                    }, new Consumer<Exception>() {
                        @Override
                        public void accept(Exception e) {
                            Log.e("CommentFragment","Error: "+e.getMessage());
                            e.printStackTrace();
                        }
                    });
                    stringListenerMap.put(commentUserId,new UserListenerCount(commentUserId, userListener));
                }else{
                    mAdapter.updateData(comment,userMap.get(commentUserId)); //display information
                    final UserListenerCount userListenerCount = stringListenerMap.get(commentUserId);
                    assert userListenerCount != null;
                    userListenerCount.addListener();
                    Log.d("CommentFragment","user listener added for "+commentUserId+"\t count: "+ userListenerCount.count);
                }

            }
        }, new Consumer<Comment>() {
            @Override
            public void accept(Comment comment) {
                // modified comment
                Log.d("CommentFragment","modified comment : "+comment.get_id());
                mAdapter.updateData(comment, userMap.get(comment.get_userId()));
            }
        }, new Consumer<Comment>() {
            @Override
            public void accept(Comment comment) {
                // removed comment
                Log.d("CommentFragment","removed comment : "+comment.get_id());
                mAdapter.removeData(comment); //delete comment
                final String commentUserId = comment.get_userId();
                UserListenerCount userListenerCount = stringListenerMap.get(commentUserId);
                assert userListenerCount != null;
                userListenerCount.removerListener();//this post not anymore liston to user
                Log.d("CommentFragment","user listener removed for "+comment.get_id()+"\t count: "+ userListenerCount.count);
                if (userListenerCount.count <= 0){ // if no one left listen to this user
                    userListenerCount.userListener.remove(); // remove connection
                    stringListenerMap.remove(commentUserId); //
                    userMap.remove(commentUserId); //
                    Log.d("CommentFragment","user listener removed for "+ commentUserId +"\t removed");
                }
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                Log.e("CommentFragment","Error: "+e.getMessage());
                e.printStackTrace();
            }
        });

        btn_comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment_text = et_comment.getText() + "";

                if (comment_text.isEmpty()){
                    Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Auth.isSignIn()){
                    Toast.makeText(getContext(), "You must be login to comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                Date date = new Date(System.currentTimeMillis());
                Comment comment = new Comment(Database.Comment.generateCommentId(),postId, Auth.getUserId(), comment_text, date);
                Database.Comment.addComment(comment, new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        Toast.makeText(getContext(), "Comment send!", Toast.LENGTH_SHORT).show();
                    }
                }, new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        Log.e("CommentFragment","Error: "+e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CommentFragment","onDestroyView");

        if (this.postListener!=null){
            this.postListener.remove();
        }
        userMap.clear();
        stringListenerMap.forEach(new BiConsumer<String, UserListenerCount>() {
            @Override
            public void accept(String userId, UserListenerCount userListenerCount) {
                Log.d("CommentFragment","clear connection for "+userId+" had "+userListenerCount.count+" connection");
                userListenerCount.removerListener();
            }
        });
        stringListenerMap.clear();

    }
}
