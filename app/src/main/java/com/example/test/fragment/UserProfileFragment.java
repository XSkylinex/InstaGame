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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.activity.LoginActivity;
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

public class UserProfileFragment extends Fragment {

    private PostImageAdapter imageAdapter;
    private Listener listener;

    private ImageView iv_user_pic;
    private TextView tv_UserFullName;
    private TextView tv_userDescription;
    private TextView tv_posts_count;


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

        RecyclerView gridview = view.findViewById(R.id.usergridview);
        gridview.setLayoutManager(new GridLayoutManager(requireContext(), 3));


        this.iv_user_pic = view.findViewById(R.id.iv_user_pic);
        this.tv_UserFullName = view.findViewById(R.id.tv_UserFullName);
        this.tv_userDescription = view.findViewById(R.id.tv_userDescription);
        this.tv_posts_count = view.findViewById(R.id.tv_posts_count);
        ProgressBar pb_userProfile = view.findViewById(R.id.pb_user_profile);

        Query query = FirebaseFirestore.getInstance()
                .collection(PostAPI.POSTS)
                .whereEqualTo("_userId",Auth.getUserId());

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
                Log.d("UserProfileFragment","onItemClick");
                final Post post = imageAdapter.getItem(position);
                Log.d("UserProfileFragment","post="+post);

                final NavDirections action = UserProfileFragmentDirections.actionUserProfileFragmentToPostFragment(post.get_id());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Log.d("UserProfileFragment","onItemLongClick");
                final Post post = imageAdapter.getItem(position);
                Log.d("UserProfileFragment","post="+post);


            }
        });


        gridview.setAdapter(imageAdapter);
        super.onViewCreated(view, savedInstanceState);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageAdapter.startListening();
        tv_posts_count.setText("0");

        listener = Database.Post.listenPostsFromUser(Auth.getUserId(), posts -> tv_posts_count.setText(""+posts.size()), Throwable::printStackTrace);
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
        imageAdapter.stopListening();
        listener.remove();
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
