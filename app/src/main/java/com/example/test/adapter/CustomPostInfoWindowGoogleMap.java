package com.example.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.models.Post;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomPostInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomPostInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.call_custom_post_info_window_map,null,false);
//        View view = ((Activity)context).getLayoutInflater()
//                .inflate(R.layout.map_custom_infowindow, null);
//

        ImageView img = view.findViewById(R.id.pic);

        Post post = (Post) marker.getTag();
        assert post != null;
        Picasso.get().load(post.get_imageUrl()).into(img);

        return view;
    }
}