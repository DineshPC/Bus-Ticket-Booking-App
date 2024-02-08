package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.*;

import com.example.busticketbookingapp.R;

public class AdminRouteActivity extends AppCompatActivity {

    Button addRouteBtn;
    DatabaseReference routesReference;
    LinearLayout linearLayoutContainer;
    ValueEventListener valueEventListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_route);

        addRouteBtn = findViewById(R.id.addRouteButton);
        routesReference = FirebaseDatabase.getInstance().getReference("routes");

        linearLayoutContainer = findViewById(R.id.linear_layout_container);

        addRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminRouteActivity.this, AdminAddRouteActivity.class);
                startActivity(intent);
            }
        });

        readDataFromFirebase();

    }

    private void readDataFromFirebase() {

        routesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayoutContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String routeName = snapshot.child("name").getValue(String.class);
                    addTextViewWithBorder(routeName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void addTextViewWithBorder(String text) {

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(20);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setBackgroundResource(R.drawable.textview_border);
        textView.setPadding(20, 10, 20, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 8, 16, 0);
        textView.setLayoutParams(layoutParams);

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(text); // Show delete dialog when long click
                return true;
            }
        });
        linearLayoutContainer.addView(textView);
    }

    private void showDeleteDialog(final String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + text + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the TextView from the layout
                        deleteTextView(text);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                });
        // Create and show the dialog
        builder.create().show();
    }

    private void deleteTextView(final String text) {
        Query query = routesReference.orderByChild("name").equalTo(text);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through the results and delete the node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(); // Remove the node from Firebase
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Database Error: " + databaseError.getMessage());
            }
        });

        // Remove the TextView from the layout
        for (int i = 0; i < linearLayoutContainer.getChildCount(); i++) {
            View child = linearLayoutContainer.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                if (textView.getText().toString().equals(text)) {
                    linearLayoutContainer.removeViewAt(i);
                    break;
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove ValueEventListener to prevent memory leaks
        if (valueEventListener != null) {
            routesReference.removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // Finish the current activity when leaving
    }


}