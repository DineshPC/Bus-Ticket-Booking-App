package com.example.busticketbookingapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.busticketbookingapp.R;

public class AdminRouteActivity extends AppCompatActivity {

    Button addRouteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_route);


        addRouteBtn = findViewById(R.id.addRouteButton);

        addRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminRouteActivity.this, AdminAddRouteActivity.class);
                startActivity(intent);
            }
        });
    }

}