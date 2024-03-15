package com.example.busticketbookingapp.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.busticketbookingapp.Admin.AdminAddRouteActivity;
import com.example.busticketbookingapp.Admin.AdminHomeActivity;
import com.example.busticketbookingapp.Admin.AdminRouteActivity;
import com.example.busticketbookingapp.Common.MainActivity;
import com.example.busticketbookingapp.Common.Unique_ID_Class;
import com.example.busticketbookingapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class BusSearchResultActivity extends AppCompatActivity {

    private String busPlateNumber = null;
    private String busNumber = "";

    TextView plateNumberTextView, busNumberTextView, busSeatTextView, busSourceAndDestinationTextView, userSourceAndDestinationTextView, busStopTextView;
    Button bookBusBtn;
    RadioGroup radioGroup;
    DatabaseReference busesRef;
    String busStartTime , busEndTime ;
    int busStartTimeInInteger , busEndTimeInInteger;
    String lastTicketBookingTimeFromDatabase;
    String currentTimeWithAllData = getCurrentTime();
    String noOfPassengersString = "1";
    String busTravelDirection = "";
    String userSourceName, userDestinationName, username;
    List<String> selectedRoutes = new ArrayList<>();
    int currentTimeWithHourAndMinuteInInteger;




    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_search_result);

        Intent intent = getIntent();
        plateNumberTextView = findViewById(R.id.textViewBusPlateNumber);
        busNumberTextView = findViewById(R.id.textViewBusNumber);
        busSeatTextView = findViewById(R.id.textViewAvailableSeat);
        userSourceAndDestinationTextView = findViewById(R.id.textViewUserSourceAndDestination);
        bookBusBtn = findViewById(R.id.buttonBookTicket);
        radioGroup = findViewById(R.id.radioGroupNumberOfPassengers);
        busSourceAndDestinationTextView = findViewById(R.id.textViewTravelDirection);
        busStopTextView = findViewById(R.id.textViewBusStops);
        bookBusBtn.setEnabled(false);

        SharedPreferences preferences = getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE);
        userSourceName = preferences.getString("SOURCE_NAME", "");
        userDestinationName = preferences.getString("DESTINATION_NAME", "");

        SharedPreferences prefs = getSharedPreferences("getUsernameFromPrefrence", Context.MODE_PRIVATE);
        username = prefs.getString("username", "");
        userSourceAndDestinationTextView.setText("User Source : " + userSourceName + "\nUser Destination : " + userDestinationName);


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
        bookBusBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int noOfPassengers = Integer.parseInt(noOfPassengersString);
                bookingTicket(noOfPassengers, busPlateNumber);
            }
        });

    }

    private void bookingTicket(int noOfPassengers, String busPlateNumber) {
        Unique_ID_Class id = new Unique_ID_Class();
        CompletableFuture<String> future = id.getUniqueIDOfTicket();
        future.thenAccept(uniqueID -> {
            addTicket(uniqueID, noOfPassengers, busPlateNumber, userSourceName, userDestinationName, username);
        }).exceptionally(ex -> {
            System.out.println("Error occurred: " + ex.getMessage());
            return null;
        });

        makeToast("Ticket is Booked");
        updateLastTicketBookingTimeOfBus(busPlateNumber);
    }

    private void updateLastTicketBookingTimeOfBus(String busPlateNumber) {
        DatabaseReference updateLastTicketBookingTimeReference = FirebaseDatabase.getInstance().getReference("Buses").child(busPlateNumber);
        updateLastTicketBookingTimeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateLastTicketBookingTimeReference.child("lastTicketBookingTime").setValue(currentTimeWithAllData);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addTicket(String UID, int noOfPassengers, String busPlateNumber ,String userSourceName, String userDestinationName, String username){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("tickets");
        reference.child(UID).child("ID").setValue(UID);
        reference.child(UID).child("noOfPassengers").setValue(noOfPassengers);
        reference.child(UID).child("userSourceName").setValue(userSourceName);
        reference.child(UID).child("userDestinationName").setValue(userDestinationName);
        reference.child(UID).child("bookedByUser").setValue(username);
        reference.child(UID).child("busPlateNumber").setValue(busPlateNumber);
        reference.child(UID).child("ticketIsCheckByDriver").setValue(false);
        removeNoOfPassengersFromBus(noOfPassengers,busPlateNumber);
        addTicketReceiptToUser(username, UID);
    }

    private void addTicketReceiptToUser(String username, String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(username);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (userSnapshot.hasChild("ticketsBookByUser")) {
                            // "ticketsBookByUser" field exists
                            DatabaseReference ticketsReference = userSnapshot.child("ticketsBookByUser").getRef();
                            addTicketToUser(ticketsReference, uid);
                        } else {
                            // "ticketsBookByUser" field doesn't exist, add it and then add the ticket
                            DatabaseReference ticketsReference = userSnapshot.getRef().child("ticketsBookByUser");
                            ticketsReference.setValue("");
                            addTicketToUser(ticketsReference, uid);
                        }
                    }
                } else {
                    // Handle the case where the user does not exist
                    makeToast("Error in adding ticket to user: User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }

    private void addTicketToUser(DatabaseReference ticketsReference, String uid) {
        // Find the highest ticket index
        ticketsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int highestTicketIndex = -1;
                for (DataSnapshot ticketSnapshot : dataSnapshot.getChildren()) {
                    String ticketKey = ticketSnapshot.getKey();
                    if (ticketKey != null && ticketKey.startsWith("ticket_")) {
                        int index = Integer.parseInt(ticketKey.substring(7)); // Extract index from key
                        if (index > highestTicketIndex) {
                            highestTicketIndex = index;
                        }
                    }
                }

                // Set the new ticket at the next available index
                int nextTicketIndex = highestTicketIndex + 1;
                ticketsReference.child("ticket_" + nextTicketIndex).setValue(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
            }
        });
    }


    private void removeNoOfPassengersFromBus(int noOfPassengers, String busPlateNumber) {
        DatabaseReference removeNoOfPassengerReference = FirebaseDatabase.getInstance().getReference("Buses").child(busPlateNumber);
        removeNoOfPassengerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer noOfAvailableTickets = snapshot.child("availableSeats").getValue(Integer.class);
                    if (noOfAvailableTickets != null) {

                        noOfAvailableTickets -= noOfPassengers;

                        removeNoOfPassengerReference.child("availableSeats").setValue(noOfAvailableTickets);
                        getAvailableSeats(busPlateNumber);
                    } else {

                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getBusSourceAndDestination() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Buses").child(busPlateNumber);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Assuming only one result, no need to iterate
                    String busSourceName = snapshot.child("source").getValue(String.class);
                    String busDestinationName = snapshot.child("destination").getValue(String.class);
                    if (busTravelDirection.isEmpty()){
                        busSourceName = "Null";
                        busDestinationName = "Null";
                    } else if (!busTravelDirection.equals("Source to Destination")){
                        String temp = busSourceName;
                        busSourceName = busDestinationName;
                        busDestinationName = temp;
                    }
                    busSourceAndDestinationTextView.setText("Bus Source : " + busSourceName + " \nBus Destination : " + busDestinationName);
                } else {
                    makeToast("Error: Bus not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                makeToast("Error: " + error.getMessage());
            }
        });
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
                            String travelMode = timeBoxSnapshot.child("direction").getValue(String.class);

                            if (endTime != null && startTime != null && isTimeBetween(startTime, endTime)) {
                                busStartTime = startTime;
                                busEndTime = endTime;
                                busTravelDirection = travelMode;
                                String busNumber = busSnapshot.child("busNumber").getValue(String.class);
                                String busPlateNumber = busSnapshot.child("busPlateNumber").getValue(String.class);
                                Toast.makeText(BusSearchResultActivity.this, "Bus " + busNumber + " (" + busPlateNumber + ") is available.", Toast.LENGTH_SHORT).show();
                                updateBusAvailableSeatFromLastTicketBookingTime();
//                                getAvailableSeats(busPlateNumber);
                                getBusSourceAndDestination();
                                getBusStatus(plateNumber);
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

    private void getBusStatus(String plateNumber) {
        DatabaseReference busesStatusRef = FirebaseDatabase.getInstance().getReference("Buses");
        Query query = busesStatusRef.orderByChild("busPlateNumber").equalTo(plateNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for (DataSnapshot routeSnapshot : snapshot.child("selectedRoutes").getChildren()) {
                        String route = routeSnapshot.getValue(String.class);
                        selectedRoutes.add(route);
                        Log.d("selectedRoutes", "Routes : " + selectedRoutes.size() + route);
                    }
                    if (busTravelDirection.equals("Destination to Source")){
                        Collections.reverse(selectedRoutes);
                    }
                    String temp = "Bus Stops (Travel in Up-to-Down Direction)";
                    String temp2 = "";
                    for (String route : selectedRoutes) {
                        temp2 = temp2 + "\n-> " + route;
                    }
                    busStopTextView.setText(temp + temp2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkBusDirectionMatchWithUserDestination(List<String> selectedRoutes, String source, String destination) {
        int sourceIndex = selectedRoutes.indexOf(source);
        int destinationIndex = selectedRoutes.indexOf(destination);
        int indexDifference = destinationIndex - sourceIndex;

        // Check if the index difference is negative
        if (indexDifference > 0) {
            makeToast("Bus Travel in same direction as User");
            Log.d("test" , "user travel in same direction as bus");
        } else {
            makeToast("Bus Travel in opposite direction from User");
            Log.d("test" , "user travel in different direction as bus");
            bookBusBtn.setEnabled(false);
        }
    }

    public void getAvailableSeats(String busPlateNumber){
        busesRef = FirebaseDatabase.getInstance().getReference().child("Buses").child(busPlateNumber);
//        updateBusAvailableSeatFromLastTicketBookingTime();

        busesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Integer availableSeat = dataSnapshot.child("availableSeats").getValue(Integer.class);
                    busSeatTextView.setText("Available Seat: " + availableSeat);
                    if (availableSeat>0){
                        bookBusBtn.setEnabled(true);
                    }
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            // checkedId is the ID of the selected radio button
                            // You can handle the click event here

                            RadioButton selectedRadioButton = findViewById(checkedId);
                            String selectedText = selectedRadioButton.getText().toString();
                            noOfPassengersString = selectedText;
                            int selectedTextInt = Integer.parseInt(selectedText);

                            // Perform actions based on the selected radio button
                            // For example, enable/disable a button based on the selection
                            if (availableSeat >= selectedTextInt) {
                                bookBusBtn.setEnabled(true);
                                checkBusDirectionMatchWithUserDestination(selectedRoutes, userSourceName, userDestinationName);

                            } else {
                                bookBusBtn.setEnabled(false);
                                checkBusDirectionMatchWithUserDestination(selectedRoutes, userSourceName, userDestinationName);
                            }
                        }
                    });
                   checkBusDirectionMatchWithUserDestination(selectedRoutes, userSourceName, userDestinationName);
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

        busStartTimeInInteger = Integer.parseInt(busStartTime);
        busEndTimeInInteger = Integer.parseInt(busEndTime);
        currentTimeWithHourAndMinuteInInteger = Integer.parseInt(currentTimeWithHourAndMinute);

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
                if (lastTicketBookingTimeFromDB == null || lastTicketBookingTimeFromDB.isEmpty()) {
                    // If last booking time doesn't exist, add it and update variable
                    busesRef.child("lastTicketBookingTime").setValue("0000-00-00 00:00:00");
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

        if (lastTicketBookingTimeFromDatabase != null && !lastTicketBookingTimeFromDatabase.equals("0000-00-00 00:00:00")) {
            // Process the retrieved lastTicketBookingTime
            String HoursAndMinutesFromLastTicketBookingTimeFromDatabase = lastTicketBookingTimeFromDatabase.split(" ")[1].replaceAll(":", "").substring(0, 4);
            int HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger = Integer.parseInt(HoursAndMinutesFromLastTicketBookingTimeFromDatabase);


            // Check if the dates from currentTimeWithAllData and lastTicketBookingTimeFromDatabase are different
            if (!isSameDate(currentTimeWithAllData, lastTicketBookingTimeFromDatabase)) {
                setAvailableSeatsFromTotalNumberOfSeats(busPlateNumber);
            } else if (busStartTimeInInteger>HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger || HoursAndMinutesFromLastTicketBookingTimeFromDatabaseInInteger>busEndTimeInInteger){
                setAvailableSeatsFromTotalNumberOfSeats(busPlateNumber);
            }else{
                getAvailableSeats(busPlateNumber);
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
                    getAvailableSeats(busPlateNumber);
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

    protected void onStop() {
        super.onStop();
        finish(); // Finish the current activity when leaving
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
