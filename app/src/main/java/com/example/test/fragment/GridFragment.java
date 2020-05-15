package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.models.Post;
import com.example.test.viewmodel.PostsSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GridFragment extends Fragment {

    private PostsSharedViewModel mViewModel;

    private PostImageAdapter imageAdapter;

    private ProgressBar pbGrid;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        this.pbGrid = view.findViewById(R.id.pb_grid);

        GridView gridview = view.findViewById(R.id.usergridview);
        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener((parent, view1, position, id) -> {
            Post post = imageAdapter.getItem(position);
            assert post != null;
            final NavDirections action = GridFragmentDirections.actionSearchFragmentToPostFragment(post.get_id());
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
            this.pbGrid.setVisibility(View.GONE);
            Log.d("posts",posts1.toString());
            imageAdapter.addAll(posts1);
        });


    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_icon);
        menuItem.setOnMenuItemClickListener(item -> {
            NavDirections action = GridFragmentDirections.actionGridFragmentToSearchFragment();
            Navigation.findNavController(requireView()).navigate(action);
            return false;
        });
        menuItem.setActionView(null);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
