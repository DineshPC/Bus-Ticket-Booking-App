package com.example.busticketbookingapp.Common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.busticketbookingapp.Common.BusSearchClass;
import com.example.busticketbookingapp.R;

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
        String ansBus = "No. : " + bus.getBusNumber()+"\n" +
                        "Plate No. : "+ bus.getBusPlateNumber() ;
        holder.busNumberTextView.setText(ansBus);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView busNumberTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            busNumberTextView = itemView.findViewById(R.id.busNumberTextView);
        }
    }
}
