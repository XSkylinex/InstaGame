package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.PostAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.listener.Listener;
import com.example.test.viewmodel.PostsSharedViewModel;

import java.util.List;
import java.util.Optional;


public class PostFragment extends Fragment {

    private PostAdapter mAdapter;

    private Listener userListener = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv_post = view.findViewById(R.id.rv_post);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv_post.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_post.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PostAdapter(getContext(), user -> {
            Log.d("PostFragment","travel to user profile :"+user.get_id());
            final PostFragmentDirections.ActionPostFragmentToOtherUserProfileFragment action = PostFragmentDirections.actionPostFragmentToOtherUserProfileFragment(user.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        }, post -> {
            Log.d("PostFragment","travel to comment of post :"+post.get_id());
            // safeArgs
            final PostFragmentDirections.ActionPostFragmentToCommentFragment action = PostFragmentDirections.actionPostFragmentToCommentFragment(post.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        });
        rv_post.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String postId = PostFragmentArgs.fromBundle(requireArguments()).getPostId();
        Log.d("PostFragment",postId);

        PostsSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsSharedViewModel.class);

        final LiveData<List<Post>> posts = mViewModel.getPosts();

        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            final Optional<Post> postOptional = posts1.stream().filter(post -> post.get_id().equals(postId)).findFirst();
            if (postOptional.isPresent()) {
                final Post post = postOptional.get();
                if (userListener !=null){
                    userListener.remove();
                }
                userListener = Database.User.listenUser(post.get_userId(), user -> {
                    mAdapter.updateData(post, user); //display information
                }, e -> {
                    Log.e("PostFragment", "Error: " + e.getMessage());
                    e.printStackTrace();
                });
            }else{
                Log.e("PostFragment","post not found! "+postId);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener!=null)
            userListener.remove();
    }
}
