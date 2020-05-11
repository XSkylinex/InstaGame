package com.example.test.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.test.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().hide();

        navController = Navigation.findNavController(this,R.id.main_navhost_frag);
        NavigationUI.setupActionBarWithNavController(this,navController);
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> getSupportActionBar().setTitle(destination.getLabel()));
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
