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
import java.util.function.Consumer;

public class MainFragment extends Fragment {

//    private MainViewModel mViewModel;
    private Button btn_logout;

    private RecyclerView recyclerView;
    private PostAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Map<String,Listener> stringListenerMap; // post id to user listener


    class userListenerCount{
        public User user;
        public Listener userListener;
        public int count;

        public userListenerCount(User user, Listener userListener) {
            this.user = user;
            this.userListener = userListener;
            this.count = 0;
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

        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        PostAdapter mAdapter = new PostAdapter(getContext());
        recyclerView.setAdapter(mAdapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//         TODO: Use the ViewModel
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Auth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        Listener listener = Database.Post.listenPostsChanges(new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                // added post

                Listener userListener = Database.User.listenUser(post.get_userId(), new Consumer<User>() {
                    @Override
                    public void accept(User user) {

                    }
                }, new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {

                    }
                });
            }
        }, new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                // modified post

            }
        }, new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                // remove post

            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                // on error

            }
        });


    }


}
