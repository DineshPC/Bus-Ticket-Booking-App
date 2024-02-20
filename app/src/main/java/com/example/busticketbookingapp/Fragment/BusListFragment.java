package com.example.busticketbookingapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.busticketbookingapp.Common.BusListAdapter;
import com.example.busticketbookingapp.Common.BusSearchClass;
import com.example.busticketbookingapp.R;

import java.util.List;

public class BusListFragment extends Fragment {
    private List<BusSearchClass> busList;

    public BusListFragment(List<BusSearchClass> busList) {
        this.busList = busList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus_list, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        BusListAdapter adapter = new BusListAdapter(busList);
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
