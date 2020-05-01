package com.example.test.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.User;

import java.util.function.Consumer;

public class LoginFragment extends Fragment {

    private Button btnToRegister;
    private Button btnSignIn;
    private EditText et_email, et_password;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnToRegister = (Button) view.findViewById(R.id.btn_login_to_register);
        btnSignIn = (Button) view.findViewById(R.id.btn_signin);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_password = (EditText) view.findViewById(R.id.et_password);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

//        final Fragment fragment= this;

        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText()+"";
                String password = et_password.getText()+"";

                Auth.SignIn(email, password, new Consumer<String>() {
                    @Override
                    public void accept(String userId) {
                        Database.User.getUser(userId, new Consumer<User>() {
                            @Override
                            public void accept(User user) {
                                Toast.makeText(getContext(), "Welcome back "+user.get_userName()+"!", Toast.LENGTH_SHORT).show();

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

        // TODO: Use the ViewModel
    }



}
