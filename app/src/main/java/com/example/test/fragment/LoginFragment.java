package com.example.test.fragment;

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

import com.example.test.R;
import com.example.test.activity.MainActivity;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;

public class LoginFragment extends Fragment {

    private Button btnToRegister;
    private Button btnSignIn;
    private EditText et_email, et_password;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnToRegister = view.findViewById(R.id.btn_login_to_register);
        btnSignIn = view.findViewById(R.id.btn_signin);
        et_email = view.findViewById(R.id.et_email);
        et_password = view.findViewById(R.id.et_password);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnToRegister.setOnClickListener(v -> Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment));

        btnSignIn.setOnClickListener(v -> {
            String email = et_email.getText()+"";
            String password = et_password.getText()+"";

            Auth.SignIn(email, password, userId -> Database.User.getUser(userId, user -> {
                Toast.makeText(getContext(), "Welcome back "+user.get_userName()+"!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }, e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()), e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // TODO: Use the ViewModel
    }



}
