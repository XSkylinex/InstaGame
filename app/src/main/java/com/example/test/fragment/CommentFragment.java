package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.CommentAdapter;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Comment;
import com.example.test.models.User;
import com.example.test.viewmodel.CommentsUsersChangesSharedViewModel;

import java.util.Date;
import java.util.Map;


public class CommentFragment extends Fragment {

    private EditText et_comment;
    private Button btn_comment_send;

    private CommentAdapter mAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rv_comments = view.findViewById(R.id.rv_comments);
        et_comment = view.findViewById(R.id.et_comment);
        btn_comment_send = view.findViewById(R.id.btn_comment_send);


        rv_comments.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_comments.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapter(getContext(), user -> {
            Log.d("PostFragment","travel to user profile :"+user.get_id());
            final CommentFragmentDirections.ActionCommentFragmentToOtherUserProfileFragment action =
                    CommentFragmentDirections.actionCommentFragmentToOtherUserProfileFragment(user.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        });
        rv_comments.setAdapter(mAdapter);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final String postId = CommentFragmentArgs.fromBundle(requireArguments()).getPostId();
        Log.d("CommentFragment",postId);

        CommentsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(CommentsUsersChangesSharedViewModel.class);

        // TODO: Use the ViewModel


        final LiveData<Map<Comment, User>> commentsUsers = mViewModel.getCommentsUsers(postId);

        commentsUsers.observe(this.getViewLifecycleOwner(),commentUserMap -> {
            Log.d("comments",commentUserMap.toString());
            mAdapter.setData(commentUserMap);
        });

        btn_comment_send.setOnClickListener(v -> {
            final String comment_text = et_comment.getText() + "";

            if (comment_text.isEmpty()){
                Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Auth.isSignIn()){
                Toast.makeText(getContext(), "You must be login to comment", Toast.LENGTH_SHORT).show();
                return;
            }
            Date date = new Date(System.currentTimeMillis());
            Comment comment = new Comment(Database.Comment.generateCommentId(),postId, Auth.getUserId(), comment_text, date);
            Database.Comment.addComment(comment, aVoid -> Toast.makeText(getContext(), "Comment send!", Toast.LENGTH_SHORT).show(),
                    e -> {
                Log.e("CommentFragment","Error: "+e.getMessage());
                e.printStackTrace();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CommentFragment","onDestroyView");
    }
}
