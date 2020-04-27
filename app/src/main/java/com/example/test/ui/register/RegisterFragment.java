package com.example.test.ui.register;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.User;

import java.util.function.Consumer;

public class RegisterFragment extends Fragment {

//    private RegisterViewModel mViewModel;
    private Button btnSignUp;
    private EditText et_username, et_email, et_password;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnSignUp = (Button) view.findViewById(R.id.btn_signup);
        et_username = (EditText) view.findViewById(R.id.et_username);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_password = (EditText) view.findViewById(R.id.et_password);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        // TODO: Use the ViewModel

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText()+"";
                String email = et_email.getText()+"";
                String password = et_password.getText()+"";

                Auth.SignUp(email, password, new Consumer<String>() {
                    @Override
                    public void accept(String userId) {
                        User user = new User(userId,email,username,null);
                        Database.User.addUser(user, new Consumer<Void>() {
                            @Override
                            public void accept(Void aVoid) {
                                Toast.makeText(getContext(), "Welcome "+username+"!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }, new Consumer<Exception>() {
                            @Override
                            public void accept(Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, new Consumer<Exception>() {
                    @Override
                    public void accept(Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}
