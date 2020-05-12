package com.example.test.fragment;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.test.models.Notification;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.viewmodel.CommentsUsersChangesSharedViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;


public class CommentFragment extends Fragment {

    private EditText et_comment;
    private Button btn_comment_send;

    private CommentAdapter mAdapter;

    private Post post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        RecyclerView rv_comments = view.findViewById(R.id.rv_comments);
        et_comment = view.findViewById(R.id.et_comment);
        btn_comment_send = view.findViewById(R.id.btn_comment_send);


        rv_comments.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_comments.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new CommentAdapter(user -> {
            Log.d("PostFragment","travel to user profile :"+user.get_id());
            final CommentFragmentDirections.ActionCommentFragmentToOtherUserProfileFragment action =
                    CommentFragmentDirections.actionCommentFragmentToOtherUserProfileFragment(user.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        });

        mAdapter.setOnItemClickListener(new CommentAdapter.onClickListner() {
            @Override
            public void onItemClick(int position, View v) {
//                final Map.Entry<Comment, User> dataAt = mAdapter.getDataAt(position);
//                final Comment comment = dataAt.getKey();
//                final User user = dataAt.getValue();
//                Log.d("onItemClickListener","onItemClick:"+dataAt.getKey().toString()+"\t"+dataAt.getValue().toString());
//                if (!user.get_id().equals(Auth.getUserId()))
//                    return;
            }

            @Override
            public void onItemLongClick(int position, View v) {
                final Map.Entry<Comment, User> dataAt = mAdapter.getDataAt(position);
                final Comment comment = dataAt.getKey();
                final User user = dataAt.getValue();
                Log.d("onItemClickListener","onItemLongClick:"+ comment.toString()+"\t"+ user.toString());
                if (!user.get_id().equals(Auth.getUserId()))
                    return;
                if (getContext()==null)
                    return;
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Delete Comment?")
                        .setMessage("Are you sure you want to delete this comment?")
                        .setNegativeButton("Cancel",(dialog, which) -> dialog.cancel())
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Database.Comment.deleteComment(comment.get_postId(), comment.get_id(), aVoid -> {
                                Database.Notification.deleteNotifications(post.get_userId(), Notification.Types.comment, user.get_id(), post.get_id(),
                                        Void1 -> {
                                            Toast.makeText(getContext(), "Comment deleted!", Toast.LENGTH_SHORT).show();
                                            dialog.cancel();
                                        },
                                        e -> {
                                            Log.e("CommentFragment", "Error: " + e.getMessage());
                                            e.printStackTrace();
                                            dialog.cancel();
                                        });
                            }, e -> {
                                dialog.cancel();
                                Log.e("CommentFragment", "Error: " + e.getMessage());
                                e.printStackTrace();
                            });
                        }).create().show();
            }
        });

        rv_comments.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        post = CommentFragmentArgs.fromBundle(requireArguments()).getPost();
        Log.d("CommentFragment",post.get_id());

        CommentsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(CommentsUsersChangesSharedViewModel.class);



        final LiveData<Map<Comment, User>> commentsUsers = mViewModel.getCommentsUsers(post.get_id());

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
            final String userId = Auth.getUserId();
            Comment comment = new Comment(Database.Comment.generateCommentId(post.get_id()),post.get_id(), userId, comment_text, date);
            Database.Comment.addComment(comment, aVoid -> {
                        Notification notification = new Notification(Database.Notification.generateNotificationId(userId),
                                post.get_userId() ,userId,Notification.Types.comment,post.get_id(),new Date(System.currentTimeMillis()));
                        Database.Notification.addNotification
                                (notification,aVoid1 -> {
                                    Toast.makeText(getContext(), "Comment send!", Toast.LENGTH_SHORT).show();
                                }, e -> {
                            Log.e("CommentFragment","Error: "+e.getMessage());
                            e.printStackTrace();
                        });
                    },
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
