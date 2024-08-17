package com.mitk.jalnidhi.admin;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.user.Activity;
import com.mitk.jalnidhi.user.ActivityAdapter;
import com.mitk.jalnidhi.user.ActivityList;
import com.mitk.jalnidhi.user.Process;
import com.squareup.picasso.Picasso;


import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder>{

    private List<RequestsList> requestsLists;
    private final Context context;
    public RequestAdapter(List<RequestsList> requestsLists, Context context) {
        this.requestsLists = requestsLists;
        this.context = context;

    }
    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.requests_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.MyViewHolder holder, int position) {

        RequestsList list2 = requestsLists.get(position);
        if (list2.getGetProfileImage() !=null){
            Uri ProfileImageUri = Uri.parse(list2.getGetProfileImage());
            Picasso.get().load(ProfileImageUri).into(holder.profileImage);
        }
        holder.name.setText("Name: "+list2.getGetName());
        holder.phoneNumber.setText("Phone No: "+list2.getGetPhoneNumber());
        holder.taluk.setText("Taluk: "+list2.getGettaluk());
        holder.district.setText("District: "+list2.getGetDistrict());
        holder.state.setText("State: "+list2.getGetState());
        holder.latitude.setText("Latitude: "+list2.getGetLatitude());
        holder.longitude.setText("Longitude: "+list2.getGetLongitude());
        holder.date.setText("Date: "+list2.getGetDate());
        holder.time.setText("Time: "+list2.getGetTime());
        holder.status.setText("Status: "+list2.getGetStatus());

        try{
            int min = 0;
            int max = 6;

            // Create an instance of Random class
            Random random = new Random();

            // Generate a random number within the specified range
            int randomNumber = random.nextInt(max - min + 1) + min;
            if (randomNumber == 0) {
                holder.indicator.setColorFilter(Color.LTGRAY);
            } else if (randomNumber == 1) {
                holder.indicator.setColorFilter(Color.BLUE);
            } else if (randomNumber == 2) {
                holder.indicator.setColorFilter(Color.YELLOW);
            } else if (randomNumber == 3) {
                holder.indicator.setColorFilter(Color.RED);
            } else if (randomNumber == 4) {
                holder.indicator.setColorFilter(Color.GREEN);
            } else if (randomNumber == 5) {
                holder.indicator.setColorFilter(Color.WHITE);
            } else if (randomNumber == 6) {
                holder.indicator.setColorFilter(Color.GRAY);
            } else {
                holder.indicator.setColorFilter(Color.RED);
            }
        }catch (Exception e){
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }
        // Convert the random number to a String

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child("Requests").child(list2.getActivityKey()).removeValue();
                Toast.makeText(context, "User request has been deleted", Toast.LENGTH_SHORT).show();

            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent  = new Intent(context, AdminProcessGenerator.class);
                intent.putExtra("Longitude", list2.getGetLongitude());
                intent.putExtra("Latitude", list2.getGetLatitude());
                intent.putExtra("RequestKey", list2.getActivityKey());
                context.startActivity(intent);
                ((Home) context).finish();
            }
        });
        holder.dontApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Requests").child(list2.getActivityKey()).child("status").setValue("Not Approved");

            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("Requests").child(list2.getActivityKey()).removeValue();
                Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<RequestsList> requestsLists){

        this.requestsLists = requestsLists;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return requestsLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

       private final CircleImageView profileImage;
        private final TextView name;
        private final TextView phoneNumber;
        private final TextView taluk;
        private final TextView district;
        private final TextView state;
        private final TextView latitude;
        private final TextView longitude;
        private final TextView date;
        private final TextView time;
        private final TextView status;
        private final Button deleteButton, acceptButton, dontApproveButton;
        private LinearLayout rootLayout;
        private ImageView indicator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            taluk = itemView.findViewById(R.id.taluk);
            district = itemView.findViewById(R.id.district);
            state = itemView.findViewById(R.id.state);
            latitude = itemView.findViewById(R.id.latitude);
            longitude= itemView.findViewById(R.id.longitude);
            date = itemView.findViewById(R.id.date);
            time= itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            dontApproveButton = itemView.findViewById(R.id.dontApproveButton);
            indicator = itemView.findViewById(R.id.indicator);

        }
    }
}
