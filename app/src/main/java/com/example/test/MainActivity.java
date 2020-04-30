package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.example.test.ui.camera.CameraFragment;
import com.example.test.ui.main.MainFragment;
import com.example.test.ui.search.SearchFragment;
import com.example.test.ui.userprofile.UserProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        getSupportActionBar().hide();

        navController = Navigation.findNavController(this,R.id.main_navhost_frag);
        NavigationUI.setupActionBarWithNavController(this,navController);
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
          @Override
          public void onDestinationChanged(@NonNull NavController
                                                   controller, @NonNull NavDestination destination, @Nullable Bundle
                                                   arguments) {
              getSupportActionBar().setTitle(destination.getLabel());
          }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            return navController.navigateUp();
        }else{
            return NavigationUI.onNavDestinationSelected(item,navController);
        }
    }
}
