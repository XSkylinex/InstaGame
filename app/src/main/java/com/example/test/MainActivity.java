package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.test.ui.camera.CameraFragment;
import com.example.test.ui.main.MainFragment;
import com.example.test.ui.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment=null;
                switch (item.getItemId()){
                    case R.id.nav_home:{
                        fragment = MainFragment.newInstance();
                        break;
                    }
                    case R.id.nav_search:{
                        fragment = SearchFragment.newInstance();
                        break;
                    }
                    case R.id.nav_add:{
                        fragment = CameraFragment.newInstance();
                        break;
                    }
                }

                if (fragment!=null){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment)
                            .commitNow();
                }

                return true;
            }
        });
    }
}
