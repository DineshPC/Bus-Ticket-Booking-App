package com.example.busticketbookingapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.busticketbookingapp.R;

public class AdminBusActivity extends AppCompatActivity {

    Button addBusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bus);

        addBusBtn = findViewById(R.id.addBusButton);


        addBusBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminBusActivity.this, AdminAddBusActivity.class);
                startActivity(intent);
            }
        });
    }
}