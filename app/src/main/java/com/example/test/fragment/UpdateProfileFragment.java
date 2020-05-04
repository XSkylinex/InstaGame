package com.example.test.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.Storage;
import com.example.test.contollers.database.Database;
import com.example.test.view.SquareImageView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.function.Consumer;


public class UpdateProfileFragment extends Fragment {

    private static final int PICK_IMAGE = 12345;
    private static final int PERMISSION_ID = 6789;

    private SquareImageView _img_profile;
    private TextView _newUserName;
    private EditText _newPassword, _confirmNewPassword, _oldPassword;
    private Button _update_button, _cancel_button;

    private File file_image = null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this._img_profile = view.findViewById(R.id.im_profilePic);
        this._newUserName = view.findViewById(R.id.tv_newUserName);
        this._newPassword = view.findViewById(R.id.tv_input_new_password);
        this._confirmNewPassword = view.findViewById(R.id.tv_input_confirm_password);
        this._oldPassword = view.findViewById(R.id.tv_input_old_password);
        this._update_button = view.findViewById(R.id.btn_update);
        this._cancel_button = view.findViewById(R.id.btn_cancel);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Database.User.getCurrentUser(user -> {
            if(user.get_imageUrl() != null){
                Picasso.get().load(user.get_imageUrl()).into(_img_profile);
            }else{
                _img_profile.setImageResource(R.drawable.ic_profile);
            }

            _newUserName.setText(user.get_userName());
            _cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(user.get_imageUrl() != null){
                        Picasso.get().load(user.get_imageUrl()).into(_img_profile);
                    }else{
                        _img_profile.setImageResource(R.drawable.ic_profile);
                    }
                    _newUserName.setText(user.get_userName());
                    _oldPassword.setText("");
                    _newPassword.setText("");
                    _confirmNewPassword.setText("");
                }
            });
            _update_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userText = _newUserName.getText()+"";
                    String oldPassword = _oldPassword.getText()+"";
                    String password = _newPassword.getText()+"";
                    String confirmPassword = _confirmNewPassword.getText()+"";

                    // Prompt the user to re-provide their sign-in credentials

                    // username update
                    // image update
                    if (!userText.isEmpty()){
                        user.set_userName(userText);
                    }
                    if (file_image != null){
                        Storage.uploadProfileImage(file_image, user.get_id(), new Consumer<Uri>() {
                            @Override
                            public void accept(Uri uri) {
                                user.set_imageUrl(uri.toString());
                                Database.User.updateUser(user, new Consumer<Void>() {
                                    @Override
                                    public void accept(Void aVoid) {
                                        Toast.makeText(getContext(), "information update!", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Consumer<Exception>() {
                                    @Override
                                    public void accept(Exception e) {
                                        Toast.makeText(getContext(), "information failed to update!", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, new Consumer<Exception>() {
                            @Override
                            public void accept(Exception e) {

                            }
                        });

                    }else {
                        Database.User.updateUser(user, new Consumer<Void>() {
                            @Override
                            public void accept(Void aVoid) {
                                Toast.makeText(getContext(), "information update!", Toast.LENGTH_SHORT).show();
                            }
                        }, new Consumer<Exception>() {
                            @Override
                            public void accept(Exception e) {
                                Toast.makeText(getContext(), "information failed to update!", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    //password update
                    if(oldPassword.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                        // enter code here NEVER!!!
                    }
                    else if(!password.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "Password not Match", Toast.LENGTH_LONG).show();
                    }else {
                        Auth.updatePassword(user.get_email(), oldPassword, password, new Consumer<Void>() {
                            @Override
                            public void accept(Void aVoid) {
                                Toast.makeText(getContext(), "updated success", Toast.LENGTH_LONG).show();
                            }
                        }, new Consumer<Exception>() {
                            @Override
                            public void accept(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }


                }
            });

        }, e -> {
            Log.e("UserProfileFragment","Error: "+e.getMessage());
            e.printStackTrace();
        });

        Fragment fragment = this;
        _img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.Companion.with(fragment)
                    .cropSquare()                //Crop image(Optional), Check Customization for more option
                    .compress(2048)            //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(PICK_IMAGE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            assert data != null;
            Uri fileUri = data.getData();
            _img_profile.setImageURI(fileUri);
            //You can get File object from intent
            file_image = ImagePicker.Companion.getFile(data);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}



