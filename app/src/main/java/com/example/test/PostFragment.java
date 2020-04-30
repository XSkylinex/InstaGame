package com.example.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.test.adapter.PostAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import com.example.test.ui.main.MainFragment;

import java.util.function.Consumer;


public class PostFragment extends Fragment {

    private RecyclerView rv_post;
    private PostAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Listener userListener = null;
    private Listener postListener = null;
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance() {
        return new PostFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_post = view.findViewById(R.id.rv_post);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv_post.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        rv_post.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PostAdapter(getContext(), new Consumer<User>() {
            @Override
            public void accept(User user) {
                Log.d("PostFragment","travel to user profile :"+user.get_id());
            }
        }, new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                Log.d("PostFragment","travel to comment of post :"+post.get_id());
                // safeArgs
                final PostFragmentDirections.ActionPostFragmentToCommentFragment action = PostFragmentDirections.actionPostFragmentToCommentFragment(post.get_id());
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        rv_post.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String postId = PostFragmentArgs.fromBundle(getArguments()).getPostId();
        Log.d("PostFragment",postId);


        postListener = Database.Post.listenPost(postId, new Consumer<Post>() {
            @Override
            public void accept(Post post) {
                if (userListener !=null){
                    userListener.remove();
                }
                userListener = Database.User.listenUser(post.get_userId(), new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        mAdapter.updateData(post, user); //display information
                    }
                }, new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        Log.e("PostFragment", "Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                Log.e("PostFragment", "Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener!=null)
            userListener.remove();
        if (postListener!=null)
            postListener.remove();
    }
}
