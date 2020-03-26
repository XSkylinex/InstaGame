package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.test.ui.camera.CameraFragment;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, CameraFragment.newInstance())
                    .commitNow();
        }
    }
}
