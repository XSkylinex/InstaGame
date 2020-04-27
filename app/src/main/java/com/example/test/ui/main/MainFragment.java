package com.example.test.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.LoginActivity;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.adapter.PostAdapter;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class MainFragment extends Fragment {

//    private MainViewModel mViewModel;
//    private Button btn_logout;

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Map<String,User> postUserMap; // post id to user
    private Map<String,userListenerCount> stringListenerMap; // user id to user listener


    private static class userListenerCount{
        @NonNull
        public String userid;
        @NonNull
        public Listener userListener;
        public int count;

        public userListenerCount(@NonNull String userid, @NonNull Listener userListener) {
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
            userListenerCount that = (userListenerCount) o;
            return userid.equals(that.userid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid);
        }
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

//        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PostAdapter(getContext());
        recyclerView.setAdapter(mAdapter);

        postUserMap=new HashMap<>();
        stringListenerMap = new HashMap<>();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//         TODO: Use the ViewModel
//        btn_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Auth.signOut();
//
//                Intent intent = new Intent(getActivity(), LoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });

        Listener listener = Database.Post.listenPostsChanges(new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                // added post

                stringListenerMap.computeIfAbsent(post.get_userId(), new Function<String, userListenerCount>() {
                    @Override
                    public userListenerCount apply(String userId) {
                        Listener userListener = Database.User.listenUser(userId, new Consumer<User>() {
                            @Override
                            public void accept(User user) {
                                postUserMap.put(post.get_id(),user); //get connect between user and post
                                mAdapter.updateData(post,user); //display information
                            }
                        }, new Consumer<Exception>() {
                            @Override
                            public void accept(Exception e) {

                            }
                        });

                        return new userListenerCount(userId,userListener);
                    }
                }).addListener();

            }
        }, new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                // modified post
                mAdapter.updateData(post,postUserMap.get(post.get_id()));
            }
        }, new Consumer<Post>() {
            @Override
            public void accept(Post post) { // remove connection from user when deleted post
                // remove post
                mAdapter.removeData(post); //delete post
                postUserMap.remove(post.get_id()); //deleted the pointer from post to user
                userListenerCount userListenerCount = stringListenerMap.get(post.get_id());
                userListenerCount.removerListener();//this post not anymore liston to user
                if (userListenerCount.count <= 0){ // if no one left listen to this user
                    userListenerCount.userListener.remove(); // remove connection
                    stringListenerMap.remove(post.get_id()); //
                }


            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                // on error

            }
        });


    }


}
