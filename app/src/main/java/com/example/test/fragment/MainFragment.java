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
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.viewmodel.PostsUsersChangesSharedViewModel;

import java.util.Map;

public class MainFragment extends Fragment {


    private PostAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("MainFragment","onCreateView");
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MainFragment","onViewCreated");

        RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PostAdapter(getContext(), user -> {
            Log.d("MainFragment","travel to user profile :"+user.get_id());
            final MainFragmentDirections.ActionMainFragmentToOtherUserProfileFragment action = MainFragmentDirections.actionMainFragmentToOtherUserProfileFragment(user.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        }, post -> {
            Log.d("MainFragment","travel to comment of post :"+post.get_id());
            final MainFragmentDirections.ActionMainFragmentToCommentFragment action = MainFragmentDirections.actionMainFragmentToCommentFragment(post);
            Navigation.findNavController(requireView()).navigate(action);
        });
        recyclerView.setAdapter(mAdapter);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MainFragment","onActivityCreated");

        PostsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsUsersChangesSharedViewModel.class);

        final LiveData<Map<Post,User>> posts = mViewModel.getPostsUsers();
        posts.observe(this.getViewLifecycleOwner(),postUserMap -> mAdapter.setData(postUserMap));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("MainFragment","onDestroyView");

    }


}
