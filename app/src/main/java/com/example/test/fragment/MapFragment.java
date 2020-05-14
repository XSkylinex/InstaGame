package com.example.test.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;
import com.example.test.models.Coordinate;
import com.example.test.models.Post;
import com.example.test.models.User;
import com.example.test.viewmodel.PostsUsersChangesSharedViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    private Coordinate coordinate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        // Gets the MapView from the XML layout and creates it
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        coordinate = MapFragmentArgs.fromBundle(requireArguments()).getCoordinate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(coordinate.getLatitude(),coordinate.getLongitude())));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));


        PostsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsUsersChangesSharedViewModel.class);

        mViewModel.getPostsUsers().observe(this.getViewLifecycleOwner(),postUserMap -> {
            map.clear();
            Log.d("MapFragment","clear markers");
            postUserMap.forEach((post, user) -> {
                final Coordinate coordinate = post.get_coordinate();
                if (coordinate == null) return;
                Log.d("MapFragment",coordinate.toString());
                final MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()))
                        .title(user.get_userName())
                        .snippet(post.get_content())
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_RED))
                        .draggable(false);
                map.addMarker(markerOptions);
            });
        });


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
