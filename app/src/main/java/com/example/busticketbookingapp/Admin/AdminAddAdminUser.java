package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.busticketbookingapp.Common.HelperClass;
import com.example.busticketbookingapp.Common.signup_page_activity;
import com.example.busticketbookingapp.R;
import com.example.busticketbookingapp.User.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminAddAdminUser extends AppCompatActivity {
    EditText signUpRealName, signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword, signUpMobile_no;
    Button signUpButton;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_admin_user);

        signUpRealName = findViewById(R.id.editTextText);
        signUpUsername = findViewById(R.id.editText2Text);
        signUpEmail = findViewById(R.id.editText3Text);
        signUpPassword = findViewById(R.id.editText5Text);
        signUpConfirmPassword = findViewById(R.id.editText6Text);
        signUpMobile_no = findViewById(R.id.editText4Text);
        signUpButton = findViewById(R.id.button7);

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = signUpRealName.getText().toString();
                String email = signUpEmail.getText().toString();
                String username = signUpUsername.getText().toString();
                String password = signUpPassword.getText().toString();
                String confirm_password = signUpConfirmPassword.getText().toString();
                String mobile_number = signUpMobile_no.getText().toString();
                String role = "admin";

                
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password) || TextUtils.isEmpty(mobile_number)) {
                    Toast.makeText(AdminAddAdminUser.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.endsWith("@gmail.com")) {
                    Toast.makeText(AdminAddAdminUser.this, "Email should end with @gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mobile_number.length() != 10) {
                    Toast.makeText(AdminAddAdminUser.this, "Mobile number should be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirm_password)) {
                    Toast.makeText(AdminAddAdminUser.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            
                            Toast.makeText(AdminAddAdminUser.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            
                            AdminUserClass adminUserClass = new AdminUserClass(name, email, username, password, confirm_password, mobile_number, role);
                            reference.child(username).setValue(adminUserClass);

                            Toast.makeText(AdminAddAdminUser.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminAddAdminUser.this, AdminHomeActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
            }
        });


    }

    protected void onStop() {
        super.onStop();
        finish(); 
    }
}