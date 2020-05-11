package com.example.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Comment;
import com.example.test.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<Map.Entry<Comment, User>> mDataset;
    private Consumer<User> travelToUserProfile;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_user_comment;
        private TextView tv_comment_username;
        private TextView tv_comment_date;
        private ImageView iv_user_image;
        private Context context;
        MyViewHolder(View view, Context context){
            super(view);
            this.tv_user_comment = view.findViewById(R.id.tv_user_comment);
            this.tv_comment_username = view.findViewById(R.id.tv_comment_username);
            this.tv_comment_date = view.findViewById(R.id.tv_comment_date);
            this.iv_user_image = view.findViewById(R.id.iv_user_image);
            this.context = context;
        }

        void setData(final Comment comment, final User user, final Consumer<User> travelToUserProfile){
            this.tv_comment_username.setText(user.get_userName());
            final Date date = comment.get_date();
            final String dateTxt = date.getHours()+":"+date.getMinutes()+"\t  "+date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear() + 1900);
            this.tv_comment_date.setText(dateTxt);
            this.tv_user_comment.setText(comment.get_content());

            if (user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.iv_user_image);
            }else {
                this.iv_user_image.setImageResource(R.drawable.ic_profile);
            }

            this.tv_comment_username.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.iv_user_image.setOnClickListener(v -> travelToUserProfile.accept(user));
        }
    }

    public CommentAdapter(Context context,Consumer<User> travelToUserProfile) {
        this.context = context;
        this.mDataset = new ArrayList<>();
        this.travelToUserProfile = travelToUserProfile;
    }

    @NotNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_comment,parent,false);
        return new MyViewHolder(v, this.context);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.MyViewHolder holder, int position) {
        holder.setData(mDataset.get(position).getKey(),mDataset.get(position).getValue(),this.travelToUserProfile);
    }

    @Override
    public int getItemCount() {
        Log.d("CommentAdapter","getItemCount = " + mDataset.size());
        return mDataset.size();
    }

    public void setData(Map<Comment,User> posts){
        this.mDataset.clear();
        this.mDataset.addAll(posts.entrySet());
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date())); // sort the posts by the newest
        this.notifyDataSetChanged();
    }

    public void updateData(Comment comment, User user){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(comment.get_id()));
        this.mDataset.add(new AbstractMap.SimpleEntry<>(comment,user));
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date()));
        this.notifyDataSetChanged();
    }

    public void removeData(Comment comment){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(comment.get_id()));
        this.notifyDataSetChanged();
    }

}
