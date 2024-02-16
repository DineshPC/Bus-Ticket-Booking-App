package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busticketbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminAdminUser extends AppCompatActivity {
    Button addAdminUserBtn;
    DatabaseReference routesReference;
    LinearLayout linearLayoutContainer;
    ValueEventListener valueEventListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_admin_user);

        addAdminUserBtn = findViewById(R.id.addAdminUserButton);

        routesReference = FirebaseDatabase.getInstance().getReference("users");
        linearLayoutContainer = findViewById(R.id.linear_layout_container);


        addAdminUserBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminAdminUser.this, AdminAddAdminUser.class);
            startActivity(intent);
        });

        readDataFromFirebase();
    }

    private void readDataFromFirebase() {

        routesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linearLayoutContainer.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if (role != null && role.equals("admin")) {
                        // If the role is 'admin', proceed with processing the snapshot
                        String realName = snapshot.child("name").getValue(String.class);
                        String userName = snapshot.child("username").getValue(String.class);
                        String mobileNumber = snapshot.child("mobile_no").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String finalName = "Name : " + realName + "\nUsername : " + userName + "\nEmail : " + email + "\nMobile Number : " + mobileNumber;
                        addTextViewWithBorder(finalName);
                    }
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
                String username = extractUsernameFromText(text); // Extract username from text
                showDeleteDialog(username); // Show delete dialog when long click
                return true;
            }
        });
        linearLayoutContainer.addView(textView);
    }

    private String extractUsernameFromText(String text) {
        String[] parts = text.split("\n"); // the username is on the second line
        if (parts.length > 1) {
            return parts[1].split(" : ")[1]; // the format is "Username : username"
        }
        return ""; // Return empty string if username extraction fails
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
        Query query = routesReference.orderByChild("username").equalTo(text);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(); // Remove the node from Firebase
                }
                makeToast("Removed");
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

    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); // Finish the current activity when leaving
    }


}