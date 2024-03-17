package com.example.busticketbookingapp.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.busticketbookingapp.Common.BusSearchClass;
import com.example.busticketbookingapp.Common.SearchHandler;
import com.example.busticketbookingapp.R;
import com.example.busticketbookingapp.User.BusSearchResultActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Booking_Fragment extends Fragment {

    private AutoCompleteTextView sourceAutoCompleteTextView, destinationAutoCompleteTextView;
    private Button searchButton;
    private List<String> placeNames;
    private ArrayAdapter<String> locationAdapter;
    FragmentManager fragmentManager;

    DatabaseReference placesRef;
    public String selectedSource, selectedDestination;



    
    
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    
    private String mParam1;
    private String mParam2;

    public Booking_Fragment() {
        
    }

    
    
    public static Booking_Fragment newInstance(String param1, String param2) {
        Booking_Fragment fragment = new Booking_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booking, container, false);

        
        sourceAutoCompleteTextView = rootView.findViewById(R.id.sourceAutoCompleteTextView);
        destinationAutoCompleteTextView = rootView.findViewById(R.id.destinationAutoCompleteTextView);
        searchButton = rootView.findViewById(R.id.searchButton);

        
        placesRef = FirebaseDatabase.getInstance().getReference("routes");

        
        placeNames = new ArrayList<>();
        locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, placeNames);
        sourceAutoCompleteTextView.setAdapter(locationAdapter);
        destinationAutoCompleteTextView.setAdapter(locationAdapter);

        
        fetchPlaceNames();

        sourceAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedSource = (String) parent.getItemAtPosition(position);
            checkSameSourceAndDestination();
        });

        destinationAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            selectedDestination = (String) parent.getItemAtPosition(position);
            checkSameSourceAndDestination();
        });

        searchButton.setOnClickListener(v -> {
            if (selectedSource != null && selectedDestination != null) {
                
                if (!selectedSource.equals(selectedDestination)) {

                    SharedPreferences preferences = requireActivity().getSharedPreferences("MY_PREFERENCES", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("SOURCE_NAME", selectedSource);
                    editor.putString("DESTINATION_NAME", selectedDestination);
                    editor.apply();


                    SearchHandler searchHandler = new SearchHandler();
                    searchHandler.performSearch(selectedSource, selectedDestination, new SearchHandler.OnSearchCompleteListener() {
                        @Override
                        public void onSearchComplete(List<BusSearchClass> busList) {
                            Toast.makeText(requireContext(), "Found " + busList.size() + " buses", Toast.LENGTH_SHORT).show();



                            
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                            
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            
                            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                            if (currentFragment != null) {
                                fragmentTransaction.remove(currentFragment);
                            }

                            
                            BusListFragment busListFragment = new BusListFragment(busList);
                            fragmentTransaction.replace(R.id.fragment_container, busListFragment)
                                    .commit();
                        }

                    });
                } else {
                    Toast.makeText(requireContext(), "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please select source and destination", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void fetchPlaceNames() {
        placesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String placeName = snapshot.child("name").getValue(String.class);
                    placeNames.add(placeName);
                }
                locationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                
            }
        });
    }

    private void checkSameSourceAndDestination() {
        if (selectedSource != null && selectedDestination != null && selectedSource.equals(selectedDestination)) {
            destinationAutoCompleteTextView.setText("");
            selectedDestination = null;
            Toast.makeText(requireContext(), "Source and destination cannot be the same", Toast.LENGTH_SHORT).show();
        }
    }
}