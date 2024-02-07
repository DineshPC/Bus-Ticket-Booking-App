package com.example.busticketbookingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.busticketbookingapp.Fragments.sampledata.Booking_Fragment;
import com.example.busticketbookingapp.Fragments.sampledata.Profile_Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottonnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        loadFragment(new Booking_Fragment());
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.dashbord) {
            fragment = new Booking_Fragment();
        } else if (itemId == R.id.profile) {
            fragment = new Profile_Fragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }

    void loadFragment(Fragment fragment) {
        //to attach fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout, fragment).commit();
    }
}