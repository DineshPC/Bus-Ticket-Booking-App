package com.example.busticketbookingapp.Fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment simple {@link Fragment} subclass.
 * Use the {@link Booking_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Booking_Fragment extends Fragment {

    private AutoCompleteTextView sourceAutoCompleteTextView, destinationAutoCompleteTextView;
    private Button searchButton;
    private List<String> placeNames;
    private ArrayAdapter<String> locationAdapter;
    FragmentManager fragmentManager;

    DatabaseReference placesRef;
    private String selectedSource, selectedDestination;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Booking_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return Fragment new instance of fragment Dashbord_Fragment.
     */
    // TODO: Rename and change types and number of parameters
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

        // Initialize views
        sourceAutoCompleteTextView = rootView.findViewById(R.id.sourceAutoCompleteTextView);
        destinationAutoCompleteTextView = rootView.findViewById(R.id.destinationAutoCompleteTextView);
        searchButton = rootView.findViewById(R.id.searchButton);

        // Initialize Firebase reference
        placesRef = FirebaseDatabase.getInstance().getReference("routes");

        // Initialize place names list and adapter
        placeNames = new ArrayList<>();
        locationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, placeNames);
        sourceAutoCompleteTextView.setAdapter(locationAdapter);
        destinationAutoCompleteTextView.setAdapter(locationAdapter);

        // Fetch places name from Firebase
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
                // Perform search
                if (!selectedSource.equals(selectedDestination)) {
                    SearchHandler searchHandler = new SearchHandler();
                    searchHandler.performSearch(selectedSource, selectedDestination, new SearchHandler.OnSearchCompleteListener() {
                        @Override
                        public void onSearchComplete(List<BusSearchClass> busList) {
                            Toast.makeText(requireContext(), "Found " + busList.size() + " buses", Toast.LENGTH_SHORT).show();

                            // Get the FragmentManager from the parent activity
                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                            // Begin a fragment transaction
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            // Remove the current fragment from the container, if any
                            Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
                            if (currentFragment != null) {
                                fragmentTransaction.remove(currentFragment);
                            }

                            // Replace it with the new fragment (BusListFragment)
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
                // Handle database error
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