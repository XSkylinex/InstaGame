package com.example.test.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.adapter.CustomPostInfoWindowGoogleMap;
import com.example.test.models.Coordinate;
import com.example.test.models.Post;
import com.example.test.viewmodel.PostsUsersChangesSharedViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(coordinate.getLatitude(),coordinate.getLongitude())));
        map.moveCamera(CameraUpdateFactory.zoomTo(15));


        PostsUsersChangesSharedViewModel mViewModel = new ViewModelProvider(requireActivity()).get(PostsUsersChangesSharedViewModel.class);
        CustomPostInfoWindowGoogleMap customInfoWindow = new CustomPostInfoWindowGoogleMap(requireContext());
        map.setInfoWindowAdapter(customInfoWindow);
        mViewModel.getPostsUsers().observe(this.getViewLifecycleOwner(),postUserMap -> {
            map.clear();
            Log.d("MapFragment","clear markers");


            postUserMap.forEach((post, user) -> {
                final Coordinate coordinate = post.get_coordinate();
                if (coordinate == null) return;
                Log.d("MapFragment",coordinate.toString());
                final MarkerOptions markerOptions;
                markerOptions = new MarkerOptions()
                        .position(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()))
                        .title(user.get_userName())
                        .snippet(post.get_content())
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_RED))
                        .draggable(false);
                final Marker marker = map.addMarker(markerOptions);

                marker.setTag(post);

//                marker.showInfoWindow();

            });
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                final Post markerPost = (Post) marker.getTag();
                assert markerPost != null;
                NavDirections navDirections = MapFragmentDirections.actionMapFragmentToPostFragment(markerPost.get_id());
                Navigation.findNavController(requireView()).navigate(navDirections);
//                Toast.makeText(getContext(), markerPost.toString(), Toast.LENGTH_SHORT).show();
            }
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
