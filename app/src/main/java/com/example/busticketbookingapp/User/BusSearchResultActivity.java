package com.example.busticketbookingapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class BusSearchResultActivity extends AppCompatActivity {

    private String busPlateNumber = null;
    private String busNumber = null;

    TextView plateNumberTextView, busNumberTextView, busSeatTextView;
    Button bookBusBtn;
    DatabaseReference busesRef;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search_result);

        Intent intent = getIntent();
        plateNumberTextView = findViewById(R.id.textViewBusPlateNumber);
        busNumberTextView = findViewById(R.id.textViewBusNumber);
        busSeatTextView = findViewById(R.id.textViewAvailableSeat);
        bookBusBtn = findViewById(R.id.buttonBookTicket);
        bookBusBtn.setEnabled(false);

        if (intent.hasExtra("BUS_PLATE_NUMBER") && intent.hasExtra("BUS_NUMBER")) {
            busPlateNumber = intent.getStringExtra("BUS_PLATE_NUMBER");
            busNumber = intent.getStringExtra("BUS_NUMBER");

            checkBusAvailableTime(busPlateNumber);
            getAvailableSeats(busPlateNumber);

            plateNumberTextView.setText("Bus Plate Number: " + busPlateNumber);
            busNumberTextView.setText("Bus Number: " + busNumber);


        } else {
            // Handle the case where the intent does not contain the expected extra data
            Toast.makeText(this, "No bus plate number found", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAvailableSeats(String busPlateNumber){
        busesRef = FirebaseDatabase.getInstance().getReference().child("Buses");
        String searchBusPlateNumber = busPlateNumber; // Using a local variable to store the bus plate number

        // Using searchBusPlateNumber instead of busPlateNumber inside the listener
        busesRef.child(searchBusPlateNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the snapshot exists and contains data
                if (dataSnapshot.exists()) {
                    // Get the availableSeat value from the dataSnapshot
                    Integer availableSeat = dataSnapshot.child("availableSeats").getValue(Integer.class);

                    // Now you have the availableSeats value, you can use it as needed
                    // For example, you can display it in a TextView
                    busSeatTextView.setText("Available Seat: " + availableSeat);
                } else {
                    // Handle the case where the snapshot does not exist or does not contain data
                    Toast.makeText(BusSearchResultActivity.this, "No data found for the bus plate number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BusSearchResultActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkBusAvailableTime(String plateNumber) {
        // Get the current time
        Date currentTime = new Date();

        // Format the current time to extract hour and minute
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());
        String formattedTime = sdf.format(currentTime);

        // Retrieve the specific bus from the database using its plate number
        DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("Buses");
        Query query = busesRef.orderByChild("busPlateNumber").equalTo(plateNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if any bus with the given plate number exists in the database
                if (dataSnapshot.exists()) {
                    // Loop through each bus with the matching plate number
                    for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                        // Get the time boxes of the bus
                        DataSnapshot timeBoxesSnapshot = busSnapshot.child("timeBoxDataList");
                        for (DataSnapshot timeBoxSnapshot : timeBoxesSnapshot.getChildren()) {
                            // Get the end time of each time box
                            String endTime = timeBoxSnapshot.child("endTime").getValue(String.class);
                            String startTime = timeBoxSnapshot.child("startTime").getValue(String.class);

                            // Compare end time with the current time
                            if (endTime != null && startTime!= null && endTime.compareTo(formattedTime) > 0 && startTime.compareTo(formattedTime)<0) {
                                // Bus is available
                                String busNumber = busSnapshot.child("busNumber").getValue(String.class);
                                String busPlateNumber = busSnapshot.child("busPlateNumber").getValue(String.class);
                                makeToast("Bus " + busNumber + " (" + busPlateNumber + ") is available.");
                                bookBusBtn.setEnabled(true);
                                return; // Exit the loop if bus is found available
                            }
                        }
                    }
                    // If no available bus found with the given plate number
                    makeToast("No available bus found with plate number " + plateNumber);
                } else {
                    // If no bus found with the given plate number
                    makeToast("No bus found with plate number " + plateNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }



    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
