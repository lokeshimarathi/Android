package com.mitk.jalnidhi.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.admin.RequestsList;


import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.MyViewHolder>{
    private List<ActivityList> activitiesLists;
    private final Context context;
    public ActivityAdapter(List<ActivityList> activitiesLists, Context context) {
        this.activitiesLists = activitiesLists;
        this.context = context;

    }

    @NonNull
    @Override
    public ActivityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.MyViewHolder holder, int position) {

        ActivityList list2 = activitiesLists.get(position);
        holder.latitude.setText("Latitude: "+list2.getGetLatitude());
        holder.longitude.setText("Longitude: "+list2.getGetLongitude());
        holder.date.setText("Date: "+list2.getGetDate());
        holder.time.setText("Time: "+list2.getGetTime());
        holder.status.setText("Status: "+list2.getGetStatus());
        holder.placeName.setText("Place name: "+list2.getGetPlaceName());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Requests").child(list2.getActivityKey()).removeValue();
                Toast.makeText(context, "Your request has been deleted", Toast.LENGTH_SHORT).show();

            }
        });
        if ("Approved".equals(list2.getGetStatus())) {
            holder.processButton.setVisibility(View.VISIBLE);
            holder.processButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Process.class);
                    intent.putExtra("ActivityKey", list2.getActivityKey());
                    intent.putExtra("Longitude", list2.getGetLongitude());
                    intent.putExtra("Latitude", list2.getGetLatitude());
                    intent.putExtra("PlaceName", list2.getGetPlaceName());
                    context.startActivity(intent);
                }
            });
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<ActivityList> activitiesLists){

        this.activitiesLists = activitiesLists;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return activitiesLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        private final TextView latitude;
        private final TextView longitude;
        private final TextView date;
        private final TextView time;
        private final TextView status;
        private final Button deleteButton, processButton;
        private LinearLayout rootLayout;
        private final TextView placeName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            latitude = itemView.findViewById(R.id.latitude);
            longitude= itemView.findViewById(R.id.longitude);
            date = itemView.findViewById(R.id.date);
            time= itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            processButton = itemView.findViewById(R.id.processButton);
            placeName = itemView.findViewById(R.id.pointName);

        }
    }
}

