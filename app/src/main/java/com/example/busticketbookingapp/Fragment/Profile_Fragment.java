package com.example.busticketbookingapp.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.busticketbookingapp.R;


public class Profile_Fragment extends Fragment {
    private TextView usernameTextView;
    private SharedPreferences prefs;

    
    
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    
    private String mParam1;
    private String mParam2;

    public Profile_Fragment() {
        
    }

    
    
    public static Profile_Fragment newInstance(String param1, String param2) {
        Profile_Fragment fragment = new Profile_Fragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_, container, false);
        usernameTextView = rootView.findViewById(R.id.userNameTextView);

        prefs = requireContext().getSharedPreferences("getUsernameFromPreference", requireContext().MODE_PRIVATE);
        String username = prefs.getString("username", "");

        usernameTextView.setText("Username : " + username);

        
        return rootView;
    }
}