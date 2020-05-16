package com.example.test.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.squareup.picasso.Picasso;


public class PostEditFragment extends Fragment {

    private ImageView _postPic;
    private EditText _etPostText;
    private Button _update,_refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();

        this._postPic = view.findViewById(R.id.img_postPic);
        this._etPostText = view.findViewById(R.id.et_post_text);
        this._update = view.findViewById(R.id.btn_update);
        this._refresh = view.findViewById(R.id.btn_refresh);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String postId = PostEditFragmentArgs.fromBundle(requireArguments()).getPostId();

        Database.Post.getPost(postId, post -> {

            if (post == null){
                Toast.makeText(getContext(), "Post not found!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
                return;
            }

            displayData(post);

            _update.setOnClickListener(v -> {
                post.set_content(_etPostText.getText()+"");
                Database.Post.updatePost(post, aVoid -> {
                    Toast.makeText(getContext(), "Post updated!", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).popBackStack();
                }, e -> Toast.makeText(getContext(), "Post failed to update\nplease try again", Toast.LENGTH_SHORT).show());
            });
            _refresh.setOnClickListener(v -> displayData(post));
        }, e -> {
            Toast.makeText(getContext(), "Post not found!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void displayData(Post post){
        Picasso.get().load(post.get_imageUrl()).into(this._postPic);
        this._etPostText.setText(post.get_content()+"");
    }
}
