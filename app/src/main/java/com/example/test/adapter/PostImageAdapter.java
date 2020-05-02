package com.example.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.test.R;
import com.example.test.models.Post;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PostImageAdapter extends ArrayAdapter<Post> {



    public PostImageAdapter(Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        // Get the data item for this position
        Post post = getItem(position);
        ViewHolder holder;
        final View result;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_post_image, parent, false);
            // Lookup view for data population
            holder = new ViewHolder(convertView);
            result=convertView;
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(holder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            holder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(getContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        // Populate the data into the template view using the data object
        assert post != null;
        holder.setData(post);
        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        ImageView ivPostImage;

        ViewHolder(View convertView) {
            ivPostImage = convertView.findViewById(R.id.iv_post_image);
        }

        void setData(@NonNull Post post){
            Picasso.get().load(post.get_imageUrl()).into(ivPostImage);
        }
    }

}