package com.example.busticketbookingapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.busticketbookingapp.Common.MainActivity;
import com.example.busticketbookingapp.R;

public class AdminHomeActivity extends AppCompatActivity {

    Button busBtn;
    Button routeBtn;
    Button logOutBtn;
    Button addAdminUserBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        busBtn = findViewById(R.id.button);
        logOutBtn = findViewById(R.id.button6);
        routeBtn = findViewById(R.id.button4);
        addAdminUserBtn = findViewById(R.id.button5);

        busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminBusActivity.class);
                startActivity(intent);
            }
        });

        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminRouteActivity.class);
                startActivity(intent);
            }
        });

        addAdminUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminAdminUser.class);
                startActivity(intent);
            }
        });


        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    public void logoutUser() {
        Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}