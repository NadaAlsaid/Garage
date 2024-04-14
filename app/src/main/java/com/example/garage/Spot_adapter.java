package com.example.garage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Spot_adapter extends RecyclerView.Adapter<Spot_adapter.SpotViewHolder>{
    ArrayList<Spot> spots , ava_spots;

    public Spot_adapter(ArrayList<Spot> spots) {
//        for (Spot spot : spots) {
//            if (spot.isSpot_availablility()) {
//                ava_spots.add(spot);
//            }
//        }
//        this.spots=ava_spots;
        this.spots = spots;
    }
    @NonNull
    // layout manager 'll call these override functions
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate for layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_car_spot, null,false);
        SpotViewHolder spot_view_holder= new SpotViewHolder(v);
        return spot_view_holder;
    }
    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot s = spots.get(position);
        if(s.isSpot_availablility()){
            holder.tv_spot_id.setText("Spot "+String.valueOf(s.getSpot_id()));
        }
        else{
            holder.tv_spot_id.setText("Busy");

            holder.btn_select_spot.setBackgroundColor(R.color.gray);
            holder.btn_select_spot.setEnabled(false);
        }
//            for (int i =position;i<spots.size()-1;i++) {
//                if (spots.get(i).isSpot_availablility()) {
//                    holder.tv_spot_id.setText("Spot "+String.valueOf(spots.get(i).getSpot_id()));
//                    break;
//                }
//            }
    }

    @Override
    public int getItemCount() {
//        int count = 0;
//        for (Spot spot : spots) {
//            if (spot.isSpot_availablility()) {
//                count++;
//            }
//        }
//        return count;
        return spots.size();
    }
    // holder class for recycler view
    class SpotViewHolder extends RecyclerView.ViewHolder{
        TextView tv_spot_id;
        Button btn_select_spot;
        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_spot_id = itemView.findViewById(R.id.spot_id);
            btn_select_spot = itemView.findViewById(R.id.select_spot_btn);

            btn_select_spot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Spot spot = spots.get(getAdapterPosition());  // Get the corresponding spot object

                    if (spot.isSpot_availablility()) {
                        // Spot is available, display "Spot is booked" Toast message
                        Toast.makeText(itemView.getContext(), "Spot is booked", Toast.LENGTH_SHORT).show();
                    } else {
                        // Spot is not available, display a different Toast message or perform any other action
                        // For example, you can display "Spot is not available" Toast message
                        Toast.makeText(itemView.getContext(), "Spot is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
