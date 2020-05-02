package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.viewmodel.SharedViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class OtherUserProfileFragment extends Fragment {

    private SharedViewModel mViewModel;

    private PostImageAdapter imageAdapter;

    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        GridView gridview = view.findViewById(R.id.usergridview);
        this.iv_user_pic = view.findViewById(R.id.iv_user_pic);
        this.tv_UserFullName = view.findViewById(R.id.tv_UserFullName);
        this.tv_userDescription = view.findViewById(R.id.tv_userDescription);
        this.tv_posts_count = view.findViewById(R.id.tv_posts_count);

        imageAdapter = new PostImageAdapter(getContext(),new ArrayList<>());
        gridview.setOnItemClickListener((parent, view1, position, id) -> {
            Post post = imageAdapter.getItem(position);
            assert post != null;
            final OtherUserProfileFragmentDirections.ActionOtherUserProfileFragmentToPostFragment action =
                    OtherUserProfileFragmentDirections.actionOtherUserProfileFragmentToPostFragment(post.get_id());
            Navigation.findNavController(requireView()).navigate(action);

        });
        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        final String userId = OtherUserProfileFragmentArgs.fromBundle(requireArguments()).getUserId();

        mViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // TODO: Use the ViewModel

        final LiveData<List<Post>> posts = mViewModel.getPosts();

        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            posts1 = posts1.stream().filter(post -> post.get_userId().equals(userId)).collect(Collectors.toList());
            Log.d("posts",posts1.toString());
            imageAdapter.addAll(posts1);
        });


        Database.User.getUser(userId, user -> {
            if (user.get_imageUrl() != null)
                Picasso.get().load(user.get_imageUrl()).into(iv_user_pic);
            tv_UserFullName.setText(user.get_userName());
            tv_userDescription.setText("WIP");
            tv_posts_count.setText("0");
        }, e -> {
            Log.e("UserProfileFragment","Error: "+e.getMessage());
            e.printStackTrace();
        });
    }
}