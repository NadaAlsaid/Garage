package com.example.garage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.garage.Logs;
import com.example.garage.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.FeatureViewHolder> {

    private static Context cont;
    private static List<Logs> myLogs;
    private static OnItemClickListener clickListener;

    public LogAdapter(Context cont,List<Logs>myLogs) {
        this.cont=cont;
        this.myLogs=myLogs;

    }

    @NonNull
    @Override
    public LogAdapter.FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_view, parent, false);
        return new LogAdapter.FeatureViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder holder, int position) {
        holder.Activity.setText(myLogs.get(position).getAction());
        holder.Timestamp.setText(myLogs.get(position).getTimeStamp());
    }


    @Override
    public int getItemCount() {
        return myLogs.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Logs clickedItem);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }
    public static void setFilteredList(List<Logs> filteredList) {
//        this.filteredList = filteredList;
//        notifyDataSetChanged();
    }

    public static class FeatureViewHolder extends RecyclerView.ViewHolder {
        TextView Activity;
        TextView Timestamp;

        public FeatureViewHolder(View itemView) {
            super(itemView);
            Activity = itemView.findViewById(R.id.Activity);
            Timestamp = itemView.findViewById(R.id.Timestamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(myLogs.get(position));
                        }
                    }
                }
            });
        }

    }
}
