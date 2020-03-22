package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.test.ui.login.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.login_container, LoginFragment.newInstance())
                    .commitNow();
        }
    }
}
