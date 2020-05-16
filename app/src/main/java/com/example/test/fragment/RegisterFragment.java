package com.example.test.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.activity.MainActivity;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.User;

public class RegisterFragment extends Fragment {

    private Button btnSignUp;
    private EditText et_username, et_email, et_password;
    private ProgressBar pbRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnSignUp = view.findViewById(R.id.btn_signup);
        et_username = view.findViewById(R.id.et_username);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);
        pbRegister = view.findViewById(R.id.pb_register);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnSignUp.setOnClickListener(v -> {
            pbRegister.setVisibility(View.VISIBLE);
            btnSignUp.setClickable(false);
            String username = et_username.getText()+"";
            String email = et_email.getText()+"";
            String password = et_password.getText()+"";
            Database.User.isUserNameExist(username, userNameExist -> {
                if (userNameExist.getKey()){
                    Toast.makeText(getContext(), "username is already taken!\nplease choose another one", Toast.LENGTH_SHORT).show();
                }else {
                    Auth.SignUp(email, password, userId -> {
                        User user = new User(userId,email,username,null,null);
                        Database.User.addUser(user, aVoid -> {
                            Toast.makeText(getContext(), "Welcome "+username+"!", Toast.LENGTH_SHORT).show();
                            pbRegister.setVisibility(View.GONE);
                            btnSignUp.setClickable(true);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }, e -> {
                            //not success add to database
                            pbRegister.setVisibility(View.GONE);
                            btnSignUp.setClickable(true);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }, e -> {
                        // problems with the registration like Mail exist
                        pbRegister.setVisibility(View.GONE);
                        btnSignUp.setClickable(true);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }, new Consumer<Exception>() {
                @Override
                public void accept(Exception e) {
                    //not success add to database
                    pbRegister.setVisibility(View.GONE);
                    btnSignUp.setClickable(true);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

}
