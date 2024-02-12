package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.busticketbookingapp.R;
import com.google.firebase.database.DatabaseReference;

public class AdminAddBusActivity extends AppCompatActivity {

    EditText busNumberEditText, busPlateNumberEditText, sourceEditText, destinationEditText, numberOfSeatsInBus;
    EditText minimumBusFare, intermediateBusFare, maximumBusFare;
    Button addBusButton;
    Spinner routesSpinner;
    ListView routesListView;

    DatabaseReference routesReference;
    List<String> routeNames;
    ArrayAdapter<String> listViewAdapter;
    List<LinearLayout> timeBoxContainers;
    List<String> selectedRoutes;
    LinearLayout timeBoxContainer;
    LinearLayout parentLayout;
    Button addTimeBoxButton;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_bus);

        routesReference = FirebaseDatabase.getInstance().getReference("routes");

        busNumberEditText = findViewById(R.id.busNumberEditText);
        busPlateNumberEditText = findViewById(R.id.busPlateNumberEditText);
        numberOfSeatsInBus = findViewById(R.id.numberOfSeatsEditText);
        sourceEditText = findViewById(R.id.sourceEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        addBusButton = findViewById(R.id.addBusButton);
        routesSpinner = findViewById(R.id.routesSpinner);
        routesListView = findViewById(R.id.routesListView);
        timeBoxContainer = findViewById(R.id.timeBoxContainer);
        addTimeBoxButton = findViewById(R.id.addTimeBoxButton);
        minimumBusFare = findViewById(R.id.minimumFareEditText);
        intermediateBusFare = findViewById(R.id.intermediateFareEditText);
        maximumBusFare = findViewById(R.id.maxFareEditText);
        parentLayout = findViewById(R.id.parentLayout);
        routeNames = new ArrayList<>();
        routeNames.add("None");
        selectedRoutes = new ArrayList<>();
        timeBoxContainers = new ArrayList<>();

        listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedRoutes);
        routesListView.setAdapter(listViewAdapter);

        timeBoxContainers.add(timeBoxContainer);

        // Set onItemSelectedListener for Routes Spinner
        routesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRoute = parent.getItemAtPosition(position).toString();
                if (!selectedRoute.equals("None") && !selectedRoutes.contains(selectedRoute)) {
                    selectedRoutes.add(selectedRoute); // Add selected route to the list
                    listViewAdapter.notifyDataSetChanged(); // Update ListView
                }
                ListAdapter adapter = routesListView.getAdapter();
                int totalHeight = 0;
                for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, routesListView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = routesListView.getLayoutParams();
                params.height = totalHeight + (routesListView.getDividerHeight() * (adapter.getCount() - 1));
                routesListView.setLayoutParams(params);

                routesListView.setVerticalScrollBarEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected
            }
        });

        addBusButton.setOnClickListener(v -> {
            if(validateBusNumber()){
                if(validateBusPlateNumber()){
                    if(validateNumberOfSeatsOnBus()){
                        if(validateSourceAndDestination()){
                            if(validateSelectRoute()){
                                if(validateFare()){
                                    String busPlateNumber = busPlateNumberEditText.getText().toString().toUpperCase();
                                    checkIfBusExists(busPlateNumber);
                                }
                            }
                        }
                    }
                }
            }else{
                // makeToast("Input error");
            }
        });
        readDataFromFirebase();

        addTimeBoxButton.setOnClickListener(v ->
                addNewTimeBox()
        );
    }

    private void uploadBusData() {
        String busNumber = busNumberEditText.getText().toString();
        String busPlateNumber = busPlateNumberEditText.getText().toString().toUpperCase();
        int numberOfSeats = Integer.parseInt(numberOfSeatsInBus.getText().toString());
        String source = sourceEditText.getText().toString();
        String destination = destinationEditText.getText().toString();

        double minimumFare = Double.parseDouble(minimumBusFare.getText().toString());
        double intermediateFare = Double.parseDouble(intermediateBusFare.getText().toString());
        double maximumFare = Double.parseDouble(maximumBusFare.getText().toString());

        // Create a list to store TimeBoxData objects
        List<TimeBoxData> timeBoxDataList = new ArrayList<>();

        for (LinearLayout timeBoxContainer : timeBoxContainers) {
            // Retrieve data from timeBoxContainer
            EditText startTimeEditText = timeBoxContainer.findViewById(R.id.startTimeEditText);
            EditText endTimeEditText = timeBoxContainer.findViewById(R.id.endTimeEditText);
            RadioGroup directionRadioGroup = timeBoxContainer.findViewById(R.id.directionRadioGroup);
            String startTime = startTimeEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();
            String direction = (directionRadioGroup.getCheckedRadioButtonId() == R.id.sourceToDestinationRadioButton) ? "Source to Destination" : "Destination to Source";

            // Add the retrieved data to the list
            timeBoxDataList.add(new TimeBoxData(startTime, endTime, direction));
        }

        // Create a new Bus object with the retrieved values
        Bus bus = new Bus(busNumber, busPlateNumber, numberOfSeats, source, destination, selectedRoutes, minimumFare, intermediateFare, maximumFare, timeBoxDataList);


        // Upload the Bus object to the database
        uploadBusToDatabase(bus);
    }

    private void uploadBusToDatabase(Bus bus) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference busesReference = databaseReference.child("Buses");
        String busId = busPlateNumberEditText.getText().toString().toUpperCase();

        // Convert timeBoxDataList to a HashMap to store it in Firebase
        HashMap<String, TimeBoxData> timeBoxDataMap = new HashMap<>();
        for (int i = 0; i < bus.getTimeBoxDataList().size(); i++) {
            timeBoxDataMap.put("timeBox_" + i, bus.getTimeBoxDataList().get(i));
        }

        // Set the bus data including timeBoxDataList
        bus.setTimeBoxDataList(null); // Exclude timeBoxDataList from Bus object
        Map<String, Object> busValues = bus.toMap();
        busValues.put("timeBoxDataList", timeBoxDataMap);

        // Upload the bus data to Firebase
        busesReference.child(busId).setValue(busValues)
                .addOnSuccessListener(aVoid -> {
                    // Data uploaded successfully
                    makeToast("Bus data uploaded successfully");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while uploading data
                    makeToast("Failed to upload bus data: " + e.getMessage());
                });
    }

    private void checkIfBusExists(String busId) {

        DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("Buses");

        busesRef.orderByChild("busPlateNumber").equalTo(busId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Bus with the same ID already exists
                    makeToast("Bus with plate number " + busId + " already exists.");
                } else {
                    // Bus does not exist, proceed with adding the bus
                    uploadBusData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred, handle accordingly
                makeToast("Error checking bus existence: " + databaseError.getMessage());
            }
        });
    }



    private boolean validateFare() {
        String minimumFareString = minimumBusFare.getText().toString();
        String intermediateFareString = intermediateBusFare.getText().toString();
        String maximumFareString = maximumBusFare.getText().toString();

        if (!minimumFareString.isEmpty() && !intermediateFareString.isEmpty() && !maximumFareString.isEmpty()) {
            try {
                int minimumFare = Integer.parseInt(minimumFareString);
                int intermediateFare = Integer.parseInt(intermediateFareString);
                int maximumFare = Integer.parseInt(maximumFareString);

                if (minimumFare >= 0 && minimumFare<1000 && intermediateFare >= 0 && intermediateFare<1000 && maximumFare >= 0 && maximumFare<1000) {
                    return true;
                } else {
                    minimumBusFare.setError("fare cannot be negative and greater than 1000");
                }
            } catch (NumberFormatException e) {
                makeToast(e.getMessage());
            }
        } else {
            minimumBusFare.setError("Fare cannot be empty");
        }
        // Validation failed
        return false;
    }



    private boolean validateBusNumber() {
        String busNumber = busNumberEditText.getText().toString();
        if(!busNumber.isEmpty()) {
            if (busNumber.length()<7){
                return true;
            }else{
                makeToast("Bus number length must be between 1-6 characters");
            }
        }else{
            makeToast("Please enter a bus number (eg. 100, A370)");
        }
        return false;
    }

    private boolean validateBusPlateNumber() {
        String busPlateNumber = busPlateNumberEditText.getText().toString();
        if(busPlateNumber.isEmpty()){
            makeToast("Please enter a bus plate number (eg. MH04AB1001)");
            return false;
        }else if (busPlateNumber.length()!=10){
            makeToast("Bus plate number must be 10 characters");
            return false;
        }
        return true;
    }

    private boolean validateNumberOfSeatsOnBus() {
        String numberOfSeats = numberOfSeatsInBus.getText().toString();
        if(numberOfSeats.isEmpty()){
            makeToast("Please enter a number of seats on the bus");
            return false;
        }
        int seats = Integer.parseInt(numberOfSeats);
        if (seats < 10 || seats > 100){
            makeToast("Number of seats must be between 10-100");
            return false;
        }
        return true;
    }

    private boolean validateSourceAndDestination() {
        String source = sourceEditText.getText().toString();
        String destination = destinationEditText.getText().toString();

        if(source.isEmpty()){
            makeToast("Source cannot be empty");
            return false;
        }else if (destination.isEmpty()){
            makeToast("Destination cannot be empty");
            return false;
        }else if (source.equals(destination)){
            makeToast("Source and destination cannot be the same");
            return false;
        }
        return true;
    }

    private Boolean validateSelectRoute(){
        if (selectedRoutes.isEmpty() || selectedRoutes.size() <2) {
            makeToast("Please select at least two route.");
            return false;
        }
        return true;
    }

    private void readDataFromFirebase() {
        routesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String routeName = snapshot.child("name").getValue(String.class);
                    if (routeName != null) {
                        routeNames.add(routeName);
                    }
                }
                // Populate Spinner with routeNames
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminAddBusActivity.this,
                        android.R.layout.simple_spinner_item, routeNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                routesSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addNewTimeBox() {
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View timeBoxView = inflater.inflate(R.layout.activity_admin_add_bus_time, null); // Assuming time_box_layout is the XML layout file for the time box

        // Add the inflated layout to the parent layout below the existing timeBoxContainer
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        parentLayout.addView(timeBoxView);

        // Add the timeBoxContainer to the list
        timeBoxContainers.add((LinearLayout) timeBoxView.findViewById(R.id.timeBoxContainer));
    }




    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
        finish();
    }
}
