package com.example.test.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.test.PostFragment;
import com.example.test.PostFragmentArgs;
import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.models.Post;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchViewModel mViewModel;

    private PostImageAdapter imageAdapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView gridview = (GridView) view.findViewById(R.id.usergridview);
        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = imageAdapter.getItem(position);
                final SearchFragmentDirections.ActionSearchFragmentToPostFragment action = SearchFragmentDirections.actionSearchFragmentToPostFragment(post.get_id());
                Navigation.findNavController(getView()).navigate(action);

            }
        });
        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        // TODO: Use the ViewModel


        final LiveData<List<Post>> posts = mViewModel.getPosts();

        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            Log.d("posts",posts1.toString());
            imageAdapter.addAll(posts1);
        });


    }

}
