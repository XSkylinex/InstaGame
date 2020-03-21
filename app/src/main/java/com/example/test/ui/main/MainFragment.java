package com.example.test.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.test.R;
import com.example.test.contollers.Auth;
import com.example.test.contollers.database.Database;
import com.example.test.models.User;

import java.util.function.Consumer;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private TextView tvMessage;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvMessage = (TextView) view.findViewById(R.id.message1);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            // update UI
            tvMessage.setText(users.toString());
        });
//        String email = "alex@gmail.com";
//        Auth.SignUp(email, "Aa123456",
//                new Consumer<String>() {
//            @Override
//            public void accept(String userId) {
//                System.out.println(userId);
//                tvMessage.setText(userId);
//                User user = new User(userId,email, "alex");
//                Database.User.addUser(user, new Consumer<Void>() {
//                    @Override
//                    public void accept(Void aVoid) {
//
//                    }
//                }, new Consumer<Exception>() {
//                    @Override
//                    public void accept(Exception e) {
//
//                    }
//                });
//
//
//            }
//        }, new Consumer<Exception>() {
//            @Override
//            public void accept(Exception e) {
//                e.printStackTrace();
//                tvMessage.setText(e.getMessage());
//            }
//        });
    }

}
