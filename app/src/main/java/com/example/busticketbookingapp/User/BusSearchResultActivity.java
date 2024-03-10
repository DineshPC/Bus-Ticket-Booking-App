package com.example.busticketbookingapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class BusSearchResultActivity extends AppCompatActivity {

    private String busPlateNumber = null;
    private String busNumber = null;

    TextView plateNumberTextView, busNumberTextView, busSeatTextView;
    Button bookBusBtn;
    DatabaseReference busesRef;
    String busStartTime , busEndTime ;
    String lastTicketBookingTimeFromDatabase;
    String currentTimeWithAllData = getCurrentTime();
    int busStartTimeInInteger;
    int busEndTimeInInteger;
    int currentTimeWithHourAndMinuteInInteger;



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

            checkBusAvailability(busPlateNumber);


            plateNumberTextView.setText("Bus Plate Number: " + busPlateNumber);
            busNumberTextView.setText("Bus Number: " + busNumber);


        } else {
            // Handle the case where the intent does not contain the expected extra data
            Toast.makeText(this, "No bus plate number found", Toast.LENGTH_SHORT).show();
        }
    }






    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }




    private void checkBusAvailability(String plateNumber) {
        busesRef = FirebaseDatabase.getInstance().getReference("Buses");
        Query query = busesRef.orderByChild("busPlateNumber").equalTo(plateNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot timeBoxSnapshot : busSnapshot.child("timeBoxDataList").getChildren()) {
                            String endTime = timeBoxSnapshot.child("endTime").getValue(String.class);
                            String startTime = timeBoxSnapshot.child("startTime").getValue(String.class);

                            if (endTime != null && startTime != null && isTimeBetween(startTime, endTime)) {
                                busStartTime = startTime;
                                busEndTime = endTime;
                                String busNumber = busSnapshot.child("busNumber").getValue(String.class);
                                String busPlateNumber = busSnapshot.child("busPlateNumber").getValue(String.class);
                                Toast.makeText(BusSearchResultActivity.this, "Bus " + busNumber + " (" + busPlateNumber + ") is available.", Toast.LENGTH_SHORT).show();
                                getAvailableSeats(busPlateNumber);
                                return;
                            }
                        }
                    }
                    Toast.makeText(BusSearchResultActivity.this, "Bus " + plateNumber + " is currently not available at a time", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BusSearchResultActivity.this, "No bus found with plate number " + plateNumber, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BusSearchResultActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAvailableSeats(String busPlateNumber){
        busesRef = FirebaseDatabase.getInstance().getReference().child("Buses").child(busPlateNumber);;
        updateBusAvailableSeatFromLastTicketBookingTime();
        // Using searchBusPlateNumber instead of busPlateNumber inside the listener
        busesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Integer availableSeat = dataSnapshot.child("availableSeats").getValue(Integer.class);

                    busSeatTextView.setText("Available Seat: " + availableSeat);
                } else {
                    Toast.makeText(BusSearchResultActivity.this, "No data found for the bus plate number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BusSearchResultActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateBusAvailableSeatFromLastTicketBookingTime() {
        Date currentTime = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());

        String currentTimeWithHourAndMinute = sdf.format(currentTime);

        int busStartTimeInInteger = Integer.parseInt(busStartTime);
        int busEndTimeInInteger = Integer.parseInt(busEndTime);
        int currentTimeWithHourAndMinuteInInteger = Integer.parseInt(currentTimeWithHourAndMinute);

        // Initiate the retrieval of lastTicketBookingTime
        getLastTicketBookingTime(busPlateNumber, currentTimeWithAllData);
    }

    public void getLastTicketBookingTime(String plateNumber, String userCurrentTime) {
        busesRef = FirebaseDatabase.getInstance().getReference("Buses").child(plateNumber);

        // Fetch last ticket booking time from the database
        busesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastTicketBookingTimeFromDB = dataSnapshot.child("lastTicketBookingTime").getValue(String.class);
                if (lastTicketBookingTimeFromDB == null) {
                    // If last booking time doesn't exist, add it and update variable
                    busesRef.child("lastTicketBookingTime").setValue("0");
                } else {
                    // If last booking time exists, update variable
                    lastTicketBookingTimeFromDatabase = lastTicketBookingTimeFromDB;
                }
                // Now that the data retrieval is complete, continue with further operations
                processLastTicketBookingTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void processLastTicketBookingTime() {
        // This method is called after lastTicketBookingTime retrieval is complete
        Log.d("LastBookingTime", "Last Ticket Booking Time: " + lastTicketBookingTimeFromDatabase);

        // Further processing based on lastTicketBookingTime
        // Ensure to handle null or empty values appropriately

        if (lastTicketBookingTimeFromDatabase != null && !lastTicketBookingTimeFromDatabase.equals("0")) {
            // Process the retrieved lastTicketBookingTime
            String HoursAndMinutesFromLastTicketBookingTimeFromDatabase = lastTicketBookingTimeFromDatabase.split(" ")[1].replaceAll(":", "").substring(0, 4);
            int HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger = Integer.parseInt(HoursAndMinutesFromLastTicketBookingTimeFromDatabase);


            // Check if the dates from currentTimeWithAllData and lastTicketBookingTimeFromDatabase are different
            if (!isSameDate(currentTimeWithAllData, lastTicketBookingTimeFromDatabase)) {
                setAvailableSeatsFromTotalNumberOfSeats(busPlateNumber);
            } else if (busStartTimeInInteger>HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger || HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger>busEndTimeInInteger){
                setAvailableSeatsFromTotalNumberOfSeats(busPlateNumber);
            }
        }
    }

    private void setAvailableSeatsFromTotalNumberOfSeats(String plateNumber) {
        // Get a reference to the specific bus with the given plate number
        DatabaseReference busRef = FirebaseDatabase.getInstance().getReference("Buses").child(plateNumber);

        // Add a ValueEventListener to listen for changes in the data at the specified location
        busRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the bus exists in the database
                if (dataSnapshot.exists()) {
                    // Get the numberOfSeats for the bus
                    int numberOfSeats = dataSnapshot.child("numberOfSeats").getValue(Integer.class);
                    Log.d("Seats","new seats" + numberOfSeats);
                    // Update the availableSeats to match the numberOfSeats
                    busRef.child("availableSeats").setValue(numberOfSeats);
                } else {
                    makeToast("No bus found with plate number: " + plateNumber);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }






    private boolean isSameDate(String date1, String date2) {
        // Extract the dates from the strings
        String[] parts1 = date1.split(" ");
        String[] parts2 = date2.split(" ");
        // Compare only the dates (without time)
        return parts1[0].equals(parts2[0]);
    }

    private boolean isTimeBetween(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        return startTime.compareTo(currentTime) < 0 && endTime.compareTo(currentTime) > 0;
    }

    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
