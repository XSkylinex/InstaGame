package com.example.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Comment;
import com.example.test.models.Notification;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private List<Map.Entry<Notification, Map.Entry<Post,User>>> mDataset;
    private Consumer<User> travelToUserProfile;
    private Consumer<Post> travelToPost;
    private Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_notification_user_image;
        private TextView tv_notification_text;
        private ImageView iv_notification_post_image;
        MyViewHolder(View view){
            super(view);
            this.iv_notification_user_image = view.findViewById(R.id.iv_notification_user_image);
            this.tv_notification_text = view.findViewById(R.id.tv_notification_text);
            this.iv_notification_post_image = view.findViewById(R.id.iv_notification_post_image);
        }

        void setData(final Notification notification,final Post post, final User user, final Consumer<User> travelToUserProfile,Consumer<Post> travelToPost){
            if (user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.iv_notification_user_image);
            }else {
                this.iv_notification_user_image.setImageResource(R.drawable.ic_profile);
            }
            String text = "";
            switch (notification.get_type()){
                case Notification.Types.like:{
                    text = user.get_userName() +" has like your post";
                    break;
                }
                case Notification.Types.comment:{
                    text = user.get_userName() +" has comment your post";
                    break;
                }
                default:break;
            }
            tv_notification_text.setText(text);

            Picasso.get().load(post.get_imageUrl()).into(this.iv_notification_post_image);

            this.iv_notification_user_image.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.iv_notification_post_image.setOnClickListener(v -> travelToPost.accept(post));
        }
    }

    public NotificationAdapter(Context context, Consumer<User> travelToUserProfile, Consumer<Post> travelToPost) {
        this.context = context;
        this.mDataset = new ArrayList<>();
        this.travelToUserProfile = travelToUserProfile;
        this.travelToPost = travelToPost;

    }

    @NotNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_call,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {
        holder.setData(mDataset.get(position).getKey(),mDataset.get(position).getValue().getKey(),
                mDataset.get(position).getValue().getValue(),this.travelToUserProfile,this.travelToPost);
    }

    @Override
    public int getItemCount() {
        Log.d("NotificationAdapter","getItemCount = " + mDataset.size());
        return mDataset.size();
    }

    public void setData(Map<Notification, Map.Entry<Post,User>> notificationEntryMap){
        this.mDataset.clear();
        this.mDataset.addAll(notificationEntryMap.entrySet());
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date())); // sort the posts by the newest
        this.notifyDataSetChanged();
    }

    public void updateData(Notification notification,Post post,User user){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.mDataset.add(new AbstractMap.SimpleEntry<>(notification,new AbstractMap.SimpleEntry<>(post,user)));
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date()));
        this.notifyDataSetChanged();
    }

    public void removeData(Notification notification){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.notifyDataSetChanged();
    }

}
