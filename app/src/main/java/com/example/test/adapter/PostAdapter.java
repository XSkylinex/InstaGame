package com.example.test.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.R;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private List<Map.Entry<Post, User>> mDataset;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private Context context;
        private TextView useName;
        private TextView picDescription;
        private ImageView picUser;
        private ImageView picPost;

        public MyViewHolder(View view,Context context){
            super(view);
            this.context = context;
            this.picDescription = view.findViewById(R.id.userPostDiscription);
            this.useName = view.findViewById(R.id.userNameProfile);
            this.picUser = view.findViewById(R.id.picUserPhotoCell);
            this.picPost = view.findViewById(R.id.userPic);
        }

        public void setData(Post post,User user){
            useName.setText(user.get_userName());
            picDescription.setText(post.get_content());

            Picasso.get().load(post.get_imageUrl()).into(this.picPost);


            if (user.get_imageUrl() !=null) {
                Picasso.get().load(user.get_imageUrl()).into(this.picUser);
            }else {
                this.picUser.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_profile));
            }
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PostAdapter(Context context) {
        this.context = context;
        mDataset = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_call,parent,false);
        return new MyViewHolder(v,this.context);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.setData(mDataset.get(position).getKey(),mDataset.get(position).getValue());
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