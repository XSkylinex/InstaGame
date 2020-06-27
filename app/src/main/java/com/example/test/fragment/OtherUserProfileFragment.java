package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.PostImageAdapter;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.contollers.database.PostAPI;
import com.example.test.models.Post;
import com.example.test.models.listener.Listener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


public class OtherUserProfileFragment extends Fragment {

    private PostImageAdapter imageAdapter;
    private Listener listener;


    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;
    private ProgressBar _pbOtherUserProfile;
    RecyclerView gridview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        this.gridview = view.findViewById(R.id.usergridview);
        gridview.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        this.iv_user_pic = view.findViewById(R.id.iv_user_pic);
        this.tv_UserFullName = view.findViewById(R.id.tv_UserFullName);
        this.tv_userDescription = view.findViewById(R.id.tv_userDescription);
        this.tv_posts_count = view.findViewById(R.id.tv_posts_count);
        this._pbOtherUserProfile = view.findViewById(R.id.pb_other_user_profile);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String userId = OtherUserProfileFragmentArgs.fromBundle(requireArguments()).getUserId();


        Query query = FirebaseFirestore.getInstance()
                .collection(PostAPI.POSTS)
                .whereEqualTo("_userId", userId);

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

                final OtherUserProfileFragmentDirections.ActionOtherUserProfileFragmentToPostFragment action =
                        OtherUserProfileFragmentDirections.actionOtherUserProfileFragmentToPostFragment(post.get_id());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onItemLongClick(int position, View v) {
            }
        });

        listener = Database.Post.listenPostsFromUser(userId, posts -> tv_posts_count.setText(""+posts.size()), Throwable::printStackTrace);
        gridview.setAdapter(imageAdapter);
        imageAdapter.startListening();
        Database.User.getUser(userId, user -> {
            if (user.get_imageUrl() != null)
                Picasso.get().load(user.get_imageUrl()).into(iv_user_pic);
            tv_UserFullName.setText(user.get_userName());
            tv_userDescription.setText(user.get_userBio());
        }, e -> {
            Log.e("UserProfileFragment","Error: "+e.getMessage());
            e.printStackTrace();
        });
    }
}
