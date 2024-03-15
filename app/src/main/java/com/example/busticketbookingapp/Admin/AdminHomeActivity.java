package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

import com.example.busticketbookingapp.Common.MainActivity;
import com.example.busticketbookingapp.R;
import com.google.zxing.integration.android.IntentResult;

public class AdminHomeActivity extends AppCompatActivity {

    Button busBtn;
    Button routeBtn;
    Button logOutBtn;
    Button addAdminUserBtn;
    Button checkTicket;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        busBtn = findViewById(R.id.button);
        logOutBtn = findViewById(R.id.button6);
        routeBtn = findViewById(R.id.button4);
        addAdminUserBtn = findViewById(R.id.button5);
        checkTicket = findViewById(R.id.button7);

        checkTicket.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Start QR code scanner
                IntentIntegrator integrator = new IntentIntegrator(AdminHomeActivity.this);
                integrator.setOrientationLocked(true);
                integrator.setPrompt("Scan a QR code");
                integrator.initiateScan();
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // QR code scanned successfully, result is stored in result.getContents()
                String qrCodeValue = result.getContents();
                Log.d("QR Code", qrCodeValue);
                checkTicket(qrCodeValue);
                // Now you can use qrCodeValue as needed, for example, display in a Toast
                Toast.makeText(AdminHomeActivity.this, "Scanned QR Code: " + qrCodeValue, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AdminHomeActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkTicket(String qrCodeValue) {
        DatabaseReference ticketReference = FirebaseDatabase.getInstance().getReference("tickets").child(qrCodeValue);
        ticketReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean ticketIsCheckByDriver = dataSnapshot.child("ticketIsCheckByDriver").getValue(Boolean.class);
                if (!ticketIsCheckByDriver){
                    String busPlateNumber = dataSnapshot.child("busPlateNumber").getValue(String.class);
                    String noOfPassenger = dataSnapshot.child("noOfPassengers").getValue(Long.class).toString();
                    String userSourceName = dataSnapshot.child("userSourceName").getValue(String.class);
                    String userDestinationName = dataSnapshot.child("userDestinationName").getValue(String.class);
                    String ticket_id = dataSnapshot.child("ID").getValue(String.class);
                    aleartDialogForNotCheckTicket(busPlateNumber, noOfPassenger, userSourceName, userDestinationName, ticket_id);
                }else{
                    alertDialogForAlreadyCheckTicket();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void aleartDialogForNotCheckTicket(String busPlateNumber, String noOfPassenger, String userSourceName, String userDestinationName, String ticketId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ticket Information")
                .setMessage("Bus Plate Number: " + busPlateNumber + "\n" +
                        "Number of Passengers: " + noOfPassenger + "\n" +
                        "Source: " + userSourceName + "\n" +
                        "Destination: " + userDestinationName + "\n" +
                        "Ticket ID: " + ticketId)
                .setPositiveButton("Check", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform action when the "Check" button is clicked
                        // For example, you can call a method to mark the ticket as checked
                        markTicketAsChecked(ticketId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing when the "Cancel" button is clicked
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void markTicketAsChecked(String ticketId) {
        DatabaseReference ticketReference = FirebaseDatabase.getInstance().getReference("tickets").child(ticketId);
        ticketReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean ticketIsCheckByDriver = dataSnapshot.child("ticketIsCheckByDriver").getValue(Boolean.class);
                if (!ticketIsCheckByDriver){
                    ticketReference.child("ticketIsCheckByDriver").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


    private void alertDialogForAlreadyCheckTicket() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ticket Information")
                .setMessage("Ticket id is not match or already checked")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing when the "Cancel" button is clicked
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
