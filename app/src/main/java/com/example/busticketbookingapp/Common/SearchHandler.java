package com.example.busticketbookingapp.Common;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchHandler {

    private DatabaseReference databaseReference;

    public SearchHandler() {
        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buses");
    }

    public void performSearch(final String place1, final String place2, final OnSearchCompleteListener listener) {
        // Retrieve all buses
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<BusSearchClass> busList = new ArrayList<>();

                // Iterate through all buses
                for (DataSnapshot busSnapshot : dataSnapshot.getChildren()) {
                    BusSearchClass bus = busSnapshot.getValue(BusSearchClass.class);

                    // Check if both places exist in selectedRoutes
                    if (bus != null && checkRouteExists(bus.getSelectedRoutes(), place1, place2)) {
                        busList.add(bus);
                    }
                }

                // Notify listener that search is complete and pass the list of buses
                listener.onSearchComplete(busList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.out.println("Database Error: " + databaseError.getMessage());
            }
        });
    }

    private boolean checkRouteExists(List<String> selectedRoutes, String place1, String place2) {
        return selectedRoutes.contains(place1) && selectedRoutes.contains(place2);
    }

    // Interface to listen for search completion
    public interface OnSearchCompleteListener {
        void onSearchComplete(List<BusSearchClass> busList);
    }
}
