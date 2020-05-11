package com.example.test.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Notification;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private List<Map.Entry<Notification, Post>> mDatasetP;
    private List<Map.Entry<Notification, User>> mDatasetU;
    private Consumer<User> travelToUserProfile;
    private Consumer<Post> travelToPost;

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

        void setData(@NonNull final Notification notification, @Nullable final Post post, @Nullable final User user, final Consumer<User> travelToUserProfile, Consumer<Post> travelToPost){
            String username="";
            if (user != null)
                username = user.get_userName();
            if (user != null && user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.iv_notification_user_image);
            }else {
                this.iv_notification_user_image.setImageResource(R.drawable.ic_profile);
            }
            String text = "";
            switch (notification.get_type()){
                case Notification.Types.like:{
                    text = username +" has like your post";
                    break;
                }
                case Notification.Types.comment:{
                    text = username +" has comment your post";
                    break;
                }
                default:break;
            }
            tv_notification_text.setText(text);
            if (post != null)
                Picasso.get().load(post.get_imageUrl()).into(this.iv_notification_post_image);

            this.iv_notification_user_image.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.iv_notification_post_image.setOnClickListener(v -> travelToPost.accept(post));
        }
    }

    public NotificationAdapter(Consumer<User> travelToUserProfile, Consumer<Post> travelToPost) {
        this.mDatasetP = new ArrayList<>();
        this.mDatasetU = new ArrayList<>();
        this.travelToUserProfile = travelToUserProfile;
        this.travelToPost = travelToPost;

    }

    @NotNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_notification,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, int position) {
        holder.setData(mDatasetU.get(position).getKey(),mDatasetP.get(position).getValue(),
                mDatasetU.get(position).getValue(),this.travelToUserProfile,this.travelToPost);
    }

    @Override
    public int getItemCount() {
        Log.d("NotificationAdapter","getItemCount = " + mDatasetU.size());
        return mDatasetU.size();
    }

    public void updateData(@NonNull Notification notification,@Nullable Post post,@Nullable User user){
        System.out.println(notification+"\t"+post+"\t"+user);
        this.mDatasetU.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.mDatasetP.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.mDatasetU.add(new AbstractMap.SimpleEntry<>(notification,user));
        this.mDatasetP.add(new AbstractMap.SimpleEntry<>(notification,post));
        mDatasetU.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date()));
        mDatasetP.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date()));
        this.notifyDataSetChanged();
    }

    public void removeData(Notification notification){
        this.mDatasetU.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.mDatasetP.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(notification.get_id()));
        this.notifyDataSetChanged();
    }

}
