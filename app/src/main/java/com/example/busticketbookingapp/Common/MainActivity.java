package com.example.busticketbookingapp.Common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.busticketbookingapp.Admin.AdminHomeActivity;
import com.example.busticketbookingapp.User.HomeActivity;
import com.example.busticketbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText loginUserName;
    EditText loginPassword;
    Button loginBtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginUserName = findViewById(R.id.editTextText2);
        loginPassword = findViewById(R.id.editTextPassword);
        loginBtn = findViewById(R.id.button2);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!validateUsername() || !validatePassword()){

                }else {
                    showLoadingScreen();
                    checkUser();
                }
            }
        });

    }

    public Boolean validateUsername(){
      String username = loginUserName.getText().toString();
      if (username.isEmpty()){
          makeToast("Username cannot be empty");
            return false;
      } else if (username.length() > 10){
          makeToast("Username length cannot be greater than 10");
          return false;
      }
      return true;
    };

    public Boolean validatePassword(){
        String password = loginPassword.getText().toString();
        if (password.isEmpty()){
            makeToast("Password cannot be empty");
            return false;
        } else if (password.length() > 20){
            makeToast("Password length cannot be greater than 20");
            return false;
        }
        return true;
    };

    public void checkUser(){
        String userUsername = loginUserName.getText().toString().trim();
        String userPassword = loginPassword.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                    String roleFromDB = userSnapshot.child("role").getValue(String.class);

                    if (roleFromDB == null || roleFromDB.isEmpty()) {
                        roleFromDB = "";
                    }

                    if (Objects.equals(passwordFromDB, userPassword)) {
                        SharedPreferences.Editor editor = getSharedPreferences("getUsernameFromPreference", MODE_PRIVATE).edit();
                        editor.putString("username", userUsername);
                        editor.apply();
                        if (Objects.equals(roleFromDB, "admin")) {
                            Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        hideLoadingScreen();
                        makeToast("Invalid Credentials");
                    }
                } else {
                    hideLoadingScreen();
                    makeToast("Username doesn't exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                
            }
        });
    }


    public void onSignUpClicked(View view) {
        Intent intent = new Intent(this, signup_page_activity.class);
        startActivity(intent);
    }

    public void onShowPasswordClicked(View view) {
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        CheckBox checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);

        int inputType = checkBoxShowPassword.isChecked() ?
                InputType.TYPE_CLASS_TEXT :
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

        editTextPassword.setInputType(inputType);
    }

    private void showLoadingScreen() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoadingScreen() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
        finish(); 
    }
}