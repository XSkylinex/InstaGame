package com.example.test.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Notification;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<Map.Entry<Post, User>> mDataset;
    private Consumer<User> travelToUserProfile;
    private Consumer<Post> travelToPostComment;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView useName;
        private final TextView picDescription;
        private final ImageView picUser;
        private final ImageView picPost;
        private final LikeButton ib_like;
        private final AppCompatImageButton btn_toComment;
        private final TextView likeNumbers;

        private Listener listenerDraw,listenerCount;

        MyViewHolder(View view){
            super(view);
            this.picDescription = view.findViewById(R.id.userPostDiscription);
            this.useName = view.findViewById(R.id.userNameProfile);
            this.picUser = view.findViewById(R.id.picUserPhotoCell);
            this.picPost = view.findViewById(R.id.userPic);
            this.ib_like = view.findViewById(R.id.ib_like);
            this.btn_toComment = view.findViewById(R.id.btn_toComment);
            this.likeNumbers = view.findViewById(R.id.likeNumbers);
        }

        @SuppressLint("DefaultLocale")
        void setData(final Post post, final User user, final Consumer<User> travelToUserProfile, final Consumer<Post> travelToPostComment){
            this.useName.setText(user.get_userName());
            this.picDescription.setText(post.get_content());

            Picasso.get().load(post.get_imageUrl()).into(this.picPost);
            if (user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.picUser);
            }else {
                this.picUser.setImageResource(R.drawable.ic_profile);
            }


            final String current_userId = Auth.getUserId();
            listenerDraw = Database.Post.listenIsLiked(post.get_id(), current_userId, isLiked -> {
                ib_like.setLiked(isLiked);
            }, e -> {
                Log.e("PostAdapter.MyViewHolder", "Error: " + e.getMessage());
                e.printStackTrace();
            });

            listenerCount = Database.Post.listenTotalLikesCount(post.get_id(),
                    count -> this.likeNumbers.setText(String.format("%d", count)),
                    Throwable::printStackTrace);
            Log.d("MyViewHolder","setData");
            ib_like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    Log.d("MyViewHolder","liked");
                    Database.Post.likePost(post.get_id(), current_userId, true, aVoid -> {
                        Log.d("MyViewHolder","liked async");
                        final String notificationId = Database.Notification.generateNotificationId(current_userId);
                        Notification notification = new Notification(notificationId,post.get_userId(),
                                current_userId,Notification.Types.like,post.get_id(),new Date(System.currentTimeMillis()));
                        Database.Notification.addNotification(notification,aVoid1 -> { },e -> { });
                    }, e -> { });
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    Log.d("MyViewHolder","unLiked");
                    Database.Post.likePost(post.get_id(), current_userId, false, aVoid -> {
                        Log.d("MyViewHolder","unLiked async");
                        Database.Notification.deleteNotifications(post.get_userId(),
                                Notification.Types.like,Auth.getUserId(),post.get_id(),aVoid1 -> {},e -> { });
                    }, e -> { });
                }
            });

            this.useName.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.picUser.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.btn_toComment.setOnClickListener(v -> travelToPostComment.accept(post));
        }


        public void remove() {
            if (listenerDraw!=null)
                listenerDraw.remove();
            if(listenerCount!=null)
                listenerCount.remove();
        }
        public void redraw(){
            this.picUser.setImageResource(R.drawable.ic_profile);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Context context,Consumer<User> travelToUserProfile, Consumer<Post> travelToPostComment) {
        this.mDataset = new ArrayList<>();
        this.travelToUserProfile = travelToUserProfile;
        this.travelToPostComment = travelToPostComment;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_post,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.remove();
        holder.redraw();
        super.onViewRecycled(holder);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setData(mDataset.get(position).getKey(),mDataset.get(position).getValue(),this.travelToUserProfile,this.travelToPostComment);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("PostAdapter","getItemCount = " + mDataset.size());
        return mDataset.size();
    }

    public void setData(Map<Post,User> posts){
        this.mDataset.clear();
        this.mDataset.addAll(posts.entrySet());
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date())); // sort the posts by the newest
//        Log.d("setData",mDataset.toString());
        this.notifyDataSetChanged();
    }

    public void updateData(Post post, User user){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(post.get_id()));
        this.mDataset.add(new AbstractMap.SimpleEntry<>(post,user));
        mDataset.sort((o1, o2) -> o2.getKey().get_date().compareTo(o1.getKey().get_date()));
        this.notifyDataSetChanged();
    }

    public void removeData(Post post){
        this.mDataset.removeIf(postUserEntry -> postUserEntry.getKey().get_id().equals(post.get_id()));
        this.notifyDataSetChanged();
    }
}