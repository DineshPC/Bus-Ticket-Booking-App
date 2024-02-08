package com.example.busticketbookingapp.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.busticketbookingapp.Common.Unique_ID_Class;
import com.example.busticketbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.CompletableFuture;

public class AdminAddRouteActivity extends AppCompatActivity {

    Button addRouteBtn;
    EditText routeName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_route);

        addRouteBtn = findViewById(R.id.addBusStopButton);
        routeName = findViewById(R.id.routeNameEditText);

        addRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = routeName.getText().toString();
                Unique_ID_Class id = new Unique_ID_Class();
                CompletableFuture<String> future = id.getUniqueIDOfRoute();
                future.thenAccept(uniqueID -> {
                    addRoute(uniqueID, name);
                }).exceptionally(ex -> {
                    System.out.println("Error occurred: " + ex.getMessage());
                    return null;
                });

                Intent intent = new Intent(AdminAddRouteActivity.this, AdminRouteActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addRoute(String UID, String routeName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("routes");
        reference.child(UID).child("ID").setValue(UID); // Set UID under the route key
        reference.child(UID).child("name").setValue(routeName); // Set routeName under the route key
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // Finish the current activity when leaving
    }
}