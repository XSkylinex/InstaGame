package com.example.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.ui.userprofile.UserProfileFragmentDirections;
import com.example.test.ui.userprofile.UserProfileViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class otherUserProfileFragment extends Fragment {


    private PostImageAdapter imageAdapter;

    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;


    public static otherUserProfileFragment newInstance() {
        return new otherUserProfileFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView gridview = (GridView) view.findViewById(R.id.usergridview);
        this.iv_user_pic = view.findViewById(R.id.iv_user_pic);
        this.tv_UserFullName = view.findViewById(R.id.tv_UserFullName);
        this.tv_userDescription = view.findViewById(R.id.tv_userDescription);
        this.tv_posts_count = view.findViewById(R.id.tv_posts_count);

        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = imageAdapter.getItem(position);
                final UserProfileFragmentDirections.ActionUserProfileFragmentToPostFragment action = UserProfileFragmentDirections.actionUserProfileFragmentToPostFragment(post.get_id());
                Navigation.findNavController(getView()).navigate(action);

            }
        });
        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        final String userId = otherUserProfileFragmentArgs.fromBundle(getArguments()).getUserId();
        Database.Post.getPostsFromUser(userId, new Consumer<List<Post>>() {
            @Override
            public void accept(List<Post> posts) {
                Log.d("posts",posts.toString());
                imageAdapter.addAll(posts);
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                Log.e("UserProfileFragment","Error: "+e.getMessage());
                e.printStackTrace();
            }
        });


        Database.User.getUser(userId,new Consumer<User>() {
            @Override
            public void accept(User user) {
                if (user.get_imageUrl() != null)
                    Picasso.get().load(user.get_imageUrl()).into(iv_user_pic);
                tv_UserFullName.setText(user.get_userName());
                tv_userDescription.setText("WIP");
                tv_posts_count.setText("0");
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                Log.e("UserProfileFragment","Error: "+e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
