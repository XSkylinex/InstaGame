package com.example.test.ui.camera;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.Storage;
import com.example.test.contollers.database.Database;
import com.example.test.models.Coordinate;
import com.example.test.models.Post;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class CameraFragment extends Fragment {

    public static final int PICK_IMAGE = 123;
    public static final int PERMISSION_ID = 44;


//    private CameraViewModel mViewModel;

    private ImageButton imagePost;
    private EditText postText;
    private Button btnUpload;
    private Switch gpsSwitch;

    private File file_image = null;
    private Location lastLocation = null;

    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.camera_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imagePost = (ImageButton) view.findViewById(R.id.ib_image_post);
        postText = (EditText) view.findViewById(R.id.et_post_text);
        btnUpload = (Button) view.findViewById(R.id.btn_upload_post);
        gpsSwitch = (Switch) view.findViewById(R.id.switch_use_gps);

        postText.setText("");

        gpsSwitch.setChecked(false);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(CameraViewModel.class);
        // TODO: Use the ViewModel
        final Fragment fragment = this;

        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(fragment)
                        .cropSquare()                //Crop image(Optional), Check Customization for more option
                        .compress(2048)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(PICK_IMAGE);
            }
        });
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("Post", "isChecked " + isChecked);



                if (isChecked) {
                    if (!checkPermissions()) {
                        requestPermissions();
                    }
                    if (isLocationEnabled()) {
                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        final LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                        final LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                Log.d("Location", location.toString());
                                lastLocation = location;
                                assert locationManager != null;
                                locationManager.removeUpdates(this);
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                Log.d("Latitude", "disable");
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                Log.d("Latitude", "enable");
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Log.d("Latitude", "status");
                            }
                        };
                        assert locationManager != null;
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Log.d("Post","task location");
                    }else{
                        lastLocation = null;
                    }
                }
                else {
                    lastLocation = null;
                }
            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_image != null && Auth.isSignIn()) {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Posting...");
                    progressDialog.show();
                    final String postId = Database.Post.generatePostId();
                    Storage.uploadImage(file_image, "posts/"+postId+".jpg", new Consumer<Uri>() {
                        @Override
                        public void accept(final Uri uri) {
                            uploadPost(postId,lastLocation,uri,progressDialog);
                        }
                    }, new Consumer<Exception>() {
                        @Override
                        public void accept(Exception e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed to upload image\ntry again later", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void uploadPost(String postId,@Nullable Location location,Uri imageUri,ProgressDialog progressDialog){
        Date date = new Date(System.currentTimeMillis());
        Coordinate coordinate = null;
        if(location != null){
            coordinate = new Coordinate(location.getLatitude(),location.getLongitude());
        }

        Post post=new Post(postId,imageUri.toString(), Auth.getUserId(),postText.getText().toString(),coordinate, date);
        final Fragment fragment = this;
        Database.Post.addPost(post, new Consumer<Void>() {
            @Override
            public void accept(Void aVoid) {

                Database.User.attachPostToUser(Auth.getUserId(), post.get_id(), new Consumer<Void>() {
                    @Override
                    public void accept(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();

                        resetView();

                        getFragmentManager().beginTransaction()
                                .detach(fragment)
                                .attach(fragment)
                                .commit();
                    }
                }, new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                });


            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        });
    }

    private void resetView() {
        postText.setText("");
        gpsSwitch.setChecked(false);

        file_image = null;
        lastLocation = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            assert data != null;
            Uri fileUri = data.getData();
            imagePost.setImageURI(fileUri);
            //You can get File object from intent
            file_image = ImagePicker.Companion.getFile(data);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Granted. Start getting the location information
//            }
//        }
//    }
}
