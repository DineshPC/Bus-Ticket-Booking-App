package com.example.busticketbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signup_page_activity extends AppCompatActivity {

    EditText signUpRealName, signUpUsername, signUpEmail, signUpPassword, signUpConfirmPassword, signUpMobile_no;
    Button signUpButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        signUpRealName = findViewById(R.id.editTextName);
        signUpUsername = findViewById(R.id.editTextUsername);
        signUpEmail = findViewById(R.id.editTextEmail);
        signUpPassword = findViewById(R.id.editTextPasswordSignup);
        signUpConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        signUpMobile_no = findViewById(R.id.editTextMobile);
        signUpButton = findViewById(R.id.buttonSignUp);


        // Sign up button logic
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

                // Validation logic
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm_password) || TextUtils.isEmpty(mobile_number)) {
                    Toast.makeText(signup_page_activity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!email.endsWith("@gmail.com")) {
                    Toast.makeText(signup_page_activity.this, "Email should end with @gmail.com", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mobile_number.length() != 10) {
                    Toast.makeText(signup_page_activity.this, "Mobile number should be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirm_password)) {
                    Toast.makeText(signup_page_activity.this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show();
                    return;
                }



                reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Handle case when username already exists
                            Toast.makeText(signup_page_activity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            // All validations passed, proceed with registration
                            HelperClass helperClass = new HelperClass(name, email, username, password, confirm_password, mobile_number);
                            reference.child(username).setValue(helperClass);

                            Toast.makeText(signup_page_activity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(signup_page_activity.this, HomeActivity.class);
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
}