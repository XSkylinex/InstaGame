package com.example.test.ui.userprofile;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.ui.search.SearchFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserProfileFragment extends Fragment {

    private UserProfileViewModel mViewModel;

    private PostImageAdapter imageAdapter;

    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;



    public static UserProfileFragment newInstance() {
        return new UserProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment, container, false);
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
        mViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        // TODO: Use the ViewModel

        final LiveData<List<Post>> posts = mViewModel.getPosts();

        posts.observe(this.getViewLifecycleOwner(),posts1 -> {
            Log.d("posts",posts1.toString());
            imageAdapter.addAll(posts1);
        });


        Database.User.getCurrentUser(new Consumer<User>() {
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
