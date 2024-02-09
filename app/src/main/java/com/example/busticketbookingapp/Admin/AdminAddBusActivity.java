package com.example.busticketbookingapp.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.*;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;
import com.example.busticketbookingapp.R;
import com.google.firebase.database.DatabaseReference;

public class AdminAddBusActivity extends AppCompatActivity {

    EditText busNumberEditText, busPlateNumberEditText, sourceEditText, destinationEditText, numberOfSeatsInBus;
    Button addBusButton;
    Spinner routesSpinner;
    ListView routesListView;

    DatabaseReference routesReference;
    List<String> routeNames;
    ArrayAdapter<String> listViewAdapter;
    List<String> selectedRoutes;

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

        routeNames = new ArrayList<>();
        routeNames.add("None");
        selectedRoutes = new ArrayList<>();

        listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, selectedRoutes);
        routesListView.setAdapter(listViewAdapter);


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

        addBusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateBusNumber()){
                    if(validateBusPlateNumber()){
                        if(validateNumberOfSeatsOnBus()){
                            if(validateSourceAndDestination()){
                                if(validateSelectRoute()){

                                }
                            }
                        }
                    }
                }else{
                    makeToast("Input error");
                }
            }

        });

        readDataFromFirebase();


    }


    private boolean validateBusNumber() {
        String busNumber = busNumberEditText.getText().toString();
        if(busNumber.isEmpty()){
            makeToast("Please enter a bus number (eg. 100, A370)");
            return false;
        }else if (busNumber.length()>6){
            makeToast("Bus number length must be between 1-6 characters");
            return false;
        }
        return true;
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

    public void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
        finish();
    }
}
