package com.example.busticketbookingapp.Common;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.busticketbookingapp.Common.BusSearchClass;
import com.example.busticketbookingapp.R;
import com.example.busticketbookingapp.User.BusSearchResultActivity;
import com.example.busticketbookingapp.User.HomeActivity;

import java.util.List;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {
    private List<BusSearchClass> busList;

    public BusListAdapter(List<BusSearchClass> busList) {
        this.busList = busList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BusSearchClass bus = busList.get(position);
        String busNumber = bus.getBusNumber();
        String busPlateNumber = bus.getBusPlateNumber();
        String ansBus = "No. : " + busNumber+"\n" +
                        "Plate No. : "+ busPlateNumber ;
        holder.busNumberTextView.setText(ansBus);
        holder.bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Book Button Clicked for Bus Number: " + busPlateNumber, Toast.LENGTH_LONG).show();
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, BusSearchResultActivity.class);
                intent.putExtra("BUS_PLATE_NUMBER", busPlateNumber);
                intent.putExtra("BUS_NUMBER", busNumber);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button bookButton;
        TextView busNumberTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumberTextView = itemView.findViewById(R.id.busNumberTextView);
            bookButton = itemView.findViewById(R.id.bookButton);
        }
    }
}
