package com.example.garage;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.NotNull;
import android.util.Log;
import androidx.annotation.Nullable;

import static android.content.Intent.getIntent;
import static android.content.Intent.makeMainActivity;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Spot_adapter extends RecyclerView.Adapter<Spot_adapter.SpotViewHolder>{
    ArrayList<Spot> spots ;
    private Context context;
    private int userID;
    Boolean connection;
    public Spot_adapter(Context context, ArrayList<Spot> spots,int userID, Boolean connection) {
        this.context = context;
        this.spots = spots;
        this.userID=userID;
        this.connection=connection;
    }    @NonNull
    // layout manager 'll call these override functions
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate for layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_car_spot, null,false);
        SpotViewHolder spot_view_holder= new SpotViewHolder(v);
        return spot_view_holder;
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot s = spots.get(position);
        if(s.isSpot_availablility()){
            holder.tv_spot_id.setText("Spot "+String.valueOf(s.getSpot_id()));
        }
        else{
            holder.tv_spot_id.setText("Busy");
            holder.btn_select_spot.setBackgroundColor(R.color.red);
            holder.btn_select_spot.setEnabled(false);
        }
//        s.refreshActivity();
    }

    @Override
    public int getItemCount() {
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
                    SpotViewHolder holder = (SpotViewHolder) v.getTag();  // Assuming you set a tag on the ViewHolder
                    if (spot.isSpot_availablility()) {

                        if(connection == true){
                            updateSpotAvailabilityInFirebase(spot,holder);
                            Intent intent = new Intent(context, Spot_Availability.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                            Toast.makeText(itemView.getContext(), "Spot is booked", Toast.LENGTH_SHORT).show();
                        }if(connection ==false){
//                            Intent intent = new Intent(context, Spot_Availability.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            context.startActivity(intent);
                            Toast.makeText(itemView.getContext(), "Please check connection before selecting spot ", Toast.LENGTH_SHORT).show();

                        }
                    }
                    else {
                        Toast.makeText(itemView.getContext(), "Spot is not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        private void updateSpotAvailabilityInFirebase(Spot spot,SpotViewHolder holder) {
            // Get the spot data (assuming you have the spot object or its id)
            String spotId = String.valueOf(spot.getSpot_id());
            boolean newAvailability = spot.isSpot_availablility();
            String currentUserId =String.valueOf(userID); // Replace with your method to get user ID

            // Get a reference to the specific spot in Firebase
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference spotRef = database.getReference("Spots").child("spot"+spotId);

            // Update both is_available and user_id
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("is_available", "false");
            updates.put("user_id", currentUserId);
            spotRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    // Handle update completion (same as before)
                }
            });
        }
    }
}
