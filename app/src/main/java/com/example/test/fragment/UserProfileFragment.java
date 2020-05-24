package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activity.LoginActivity;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.viewmodel.PostsSharedViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserProfileFragment extends Fragment {

    private PostImageAdapter imageAdapter;

    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;
    private ProgressBar pb_userProfile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        GridView gridview = view.findViewById(R.id.usergridview);
        this.iv_user_pic = view.findViewById(R.id.iv_user_pic);
        this.tv_UserFullName = view.findViewById(R.id.tv_UserFullName);
        this.tv_userDescription = view.findViewById(R.id.tv_userDescription);
        this.tv_posts_count = view.findViewById(R.id.tv_posts_count);
        this.pb_userProfile = view.findViewById(R.id.pb_user_profile);

        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener((parent, view1, position, id) -> {
            Post post = imageAdapter.getItem(position);
            assert post != null;
            Log.d("UserProfileFragment","NavDirections: "+post);
            // bug with NavDirections idk why
            final NavDirections action = UserProfileFragmentDirections.actionUserProfileFragmentToPostFragment(post.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        });
        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageAdapter.notifyDataSetChanged();
        final PostsSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsSharedViewModel.class);
        final LiveData<List<Post>> posts = mViewModel.getPosts();
        tv_posts_count.setText("0");
        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            this.pb_userProfile.setVisibility(View.GONE);
            final String userId = Auth.getUserId();
            posts1 = posts1.stream().filter(post -> post.get_userId().equals(userId)).sorted((o1, o2) -> o2.get_date().compareTo(o1.get_date())).collect(Collectors.toList());
            final int size = posts1.size();
            Log.d("posts", size +"\t"+posts1.toString());
            tv_posts_count.setText(size+"");
            imageAdapter.clear();
            imageAdapter.addAll(posts1);
        });


        Database.User.getCurrentUser(user -> {
            if (user.get_imageUrl() != null)
                Picasso.get().load(user.get_imageUrl()).into(iv_user_pic);
            tv_UserFullName.setText(user.get_userName());
            tv_userDescription.setText(user.get_userBio());
            if(getActivity()!=null)
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(user.get_userName());
        }, e -> {
            Log.e("UserProfileFragment","Error: "+e.getMessage());
            e.printStackTrace();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        setHasOptionsMenu(false);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.user_profile_menu, menu);
        //hide item (sort)
//        menu.findItem(R.id.action_sort).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks
        switch (item.getItemId()){
            case R.id.item_to_user_prifile:{
                Toast.makeText(getActivity(), "Settings", Toast.LENGTH_SHORT).show();
                final NavDirections navDirections = UserProfileFragmentDirections.actionUserProfileFragmentToUpdateProfileFragment();
                Navigation.findNavController(requireView()).navigate(navDirections);
                break;
            }
            case R.id.item_logout:{
                Auth.signOut();
                Intent intent =new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
