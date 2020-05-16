package com.example.test.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.viewmodel.PostsUsersChangesSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class SearchFragment extends Fragment {

    ListView lv_user_names;
    ProgressBar pb_search;
    ArrayAdapter<String> arrayAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        lv_user_names = view.findViewById(R.id.lv_user_names);
        pb_search = view.findViewById(R.id.pb_search);

        arrayAdapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_1);

        lv_user_names.setAdapter(arrayAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        PostsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsUsersChangesSharedViewModel.class);


        final LiveData<Map<Post, User>> postsUsers = mViewModel.getPostsUsers();

        postsUsers.observe(this.getViewLifecycleOwner(),postUserMap -> {
            this.pb_search.setVisibility(View.GONE);
            final ArrayList<User> users = new ArrayList<>(new HashSet<>(postUserMap.values()));

            arrayAdapter.clear();
            arrayAdapter.addAll(users.stream().map(User::get_userName).collect(Collectors.toList()));
            lv_user_names.setOnItemClickListener((parent, view, position, id) -> {
                final String userText = ((TextView) view).getText().toString();
                Log.d("SearchFragment",userText);
                final Optional<User> first = users.stream().filter(user1 -> user1.get_userName().equals(userText)).findFirst();
                first.ifPresent(user -> {
                    NavDirections action = SearchFragmentDirections.actionSearchFragmentToOtherUserProfileFragment(user.get_id());
                    Navigation.findNavController(requireView()).navigate(action);
                });

            });
        });
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_icon);
        final SearchView searchView = (SearchView)menuItem.getActionView();

        searchView.setQueryHint("Search Here!");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
