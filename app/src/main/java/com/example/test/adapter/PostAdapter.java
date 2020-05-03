package com.example.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<Map.Entry<Post, User>> mDataset;
    private Consumer<User> travelToUserProfile;
    private Consumer<Post> travelToPostComment;
    private Context context;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final TextView useName;
        private final TextView picDescription;
        private final ImageView picUser;
        private final ImageView picPost;
        private final AppCompatImageButton ib_like;
        private final AppCompatImageButton btn_toComment;
        private final TextView likeNumbers;
        private final Context context;

        private Listener listenerDraw,listenerCount;

        MyViewHolder(View view, Context context){
            super(view);
            this.picDescription = view.findViewById(R.id.userPostDiscription);
            this.useName = view.findViewById(R.id.userNameProfile);
            this.picUser = view.findViewById(R.id.picUserPhotoCell);
            this.picPost = view.findViewById(R.id.userPic);
            this.ib_like = view.findViewById(R.id.ib_like);
            this.btn_toComment = view.findViewById(R.id.btn_toComment);
            this.likeNumbers = view.findViewById(R.id.likeNumbers);
            this.context = context;
        }

        void setData(final Post post, final User user, final Consumer<User> travelToUserProfile, final Consumer<Post> travelToPostComment){
            this.useName.setText(user.get_userName());
            this.picDescription.setText(post.get_content());

            Picasso.get().load(post.get_imageUrl()).into(this.picPost);
            if (user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.picUser);
            }else {
                this.picUser.setImageResource(R.drawable.ic_profile);
            }

            ib_like.setClickable(false);

            // TODO
            listenerDraw = Database.Post.listenIsLiked(post.get_id(), Auth.getUserId(),isLiked -> {
                ib_like.setClickable(true);
                if (isLiked)
                    ib_like.setImageResource(R.drawable.ic_liked);
                else
                    ib_like.setImageResource(R.drawable.ic_like);
            }, e -> {
                Log.e("PostAdapter.MyViewHolder", "Error: " + e.getMessage());
                e.printStackTrace();
                ib_like.setImageResource(R.drawable.ic_like);
            });

            // TODO
            listenerCount = Database.Post.listenTotalLikesCount(post.get_id(),
                    count -> this.likeNumbers.setText("" + count),
                    Throwable::printStackTrace);

            ib_like.setOnClickListener(v -> {
                ib_like.setClickable(false);
                Database.Post.runTransactionLike(post.get_id(),
                        Auth.getUserId(), aVoid -> ib_like.setClickable(true),
                        e -> ib_like.setClickable(true));
            });



            this.useName.setOnClickListener(v -> travelToUserProfile.accept(user));
            this.btn_toComment.setOnClickListener(v -> travelToPostComment.accept(post));
        }


        public void remove() {
            if (listenerDraw!=null)
                listenerDraw.remove();
            if(listenerCount!=null)
                listenerCount.remove();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Context context,Consumer<User> travelToUserProfile, Consumer<Post> travelToPostComment) {
        this.context = context;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_call,parent,false);
        return new MyViewHolder(v,this.context);
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        holder.remove();
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