package com.example.test.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.database.PostAPI;
import com.example.test.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

public class GridFragment extends Fragment {



    private ProgressBar pbGrid;
    PostImageAdapter imageAdapter;


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


        RecyclerView gridview = view.findViewById(R.id.usergridview);
        gridview.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        Query query = FirebaseFirestore.getInstance()
                .collection(PostAPI.POSTS);

        // Configure recycler adapter options:
        //  * query is the Query object defined above.
        //  * Chat.class instructs the adapter to convert each DocumentSnapshot to a Chat object
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();


        imageAdapter = new PostImageAdapter(options);

        imageAdapter.setOnItemClickListener(new PostImageAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                final Post post = imageAdapter.getItem(position);

                final NavDirections action = GridFragmentDirections.actionSearchFragmentToPostFragment(post.get_id());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageAdapter.startListening();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageAdapter.stopListening();
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
