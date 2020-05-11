package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.models.Post;
import com.example.test.viewmodel.PostsSharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private PostsSharedViewModel mViewModel;

    private PostImageAdapter imageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView gridview = view.findViewById(R.id.usergridview);
        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener((parent, view1, position, id) -> {
            Post post = imageAdapter.getItem(position);
            assert post != null;
            final SearchFragmentDirections.ActionSearchFragmentToPostFragment action = SearchFragmentDirections.actionSearchFragmentToPostFragment(post.get_id());
            Navigation.findNavController(requireView()).navigate(action);

        });
        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(PostsSharedViewModel.class);


        final LiveData<List<Post>> posts = mViewModel.getPosts();

        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            Log.d("posts",posts1.toString());
            imageAdapter.addAll(posts1);
        });


    }

}
