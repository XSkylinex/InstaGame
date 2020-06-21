package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class PostImageAdapter extends FirestoreRecyclerAdapter<Post, PostImageAdapter.PostHolder> {

    private static ClickListener clickListener;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PostImageAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }

    class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView ivPostImage;
        Post post;
        public PostHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);
        }
        void setData(@NonNull Post post){
            this.post = post;
            Picasso.get().load(post.get_imageUrl()).into(ivPostImage);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }

        public Post getData() {
            return post;
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull PostHolder holder, int position, @NonNull Post model) {
        // Bind the Chat object to the ChatHolder
        // ...
//        Log.d("MyFirestoreRecyclerAdapter","onBindViewHolder "+model);
        holder.setData(model);
    }

    @Override
    public void onViewRecycled(@NonNull PostHolder holder) {
        super.onViewRecycled(holder);
//        Log.d("MyFirestoreRecyclerAdapter","onViewRecycled "+holder.getData());
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.call_post_image, group, false);

        return new PostHolder(view);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        PostImageAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
}
