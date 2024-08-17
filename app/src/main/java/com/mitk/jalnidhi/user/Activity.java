package com.mitk.jalnidhi.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity extends AppCompatActivity {
    private RecyclerView activityRecyclerView;
    private String activityKey = "";
    private final List<ActivityList> activitiesList = new ArrayList<>();
    private ActivityAdapter activitiesAdapter;
    private boolean dataSet = false;
    private Handler handler;
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_);
        activityRecyclerView = findViewById(R.id.activityRecyclerView);
        activityRecyclerView.setHasFixedSize(true);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(Activity.this));

         //set Adapter to  recycler view
        activitiesAdapter = new ActivityAdapter(activitiesList, Activity.this);
        activityRecyclerView.setAdapter( activitiesAdapter);
        ProgressDialog pd = new ProgressDialog(Activity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Perform your refresh logic here

                // Restart the handler after 2 seconds
                handler.postDelayed(this, 2000);
            }
        };
        databaseReference.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                activitiesList.clear();

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    final String getKey = dataSnapshot1.getKey();
                    activityKey = getKey;
                    dataSet = false;
                    final String getAuthUserId = dataSnapshot1.child("authUserId").getValue(String.class);

                    if (Objects.equals(getAuthUserId, AuthUserId)) {

                        final String getLatitude = dataSnapshot1.child("latitude").getValue(String.class);
                        final String getLongitude = dataSnapshot1.child("longitude").getValue(String.class);
                        final String getDate = dataSnapshot1.child("date").getValue(String.class);
                        final String getTime = dataSnapshot1.child("time").getValue(String.class);
                        final String getStatus = dataSnapshot1.child("status").getValue(String.class);
                        final String getPlaceName = dataSnapshot1.child("placeName").getValue(String.class);
                        ActivityList activityList = new ActivityList(activityKey, getLatitude, getLongitude, getStatus, getDate, getTime, getPlaceName);
                        activitiesList.add(activityList);
                        dataSet = true; // Move this outside the if block if it's always true when inside the loop
                    }

                }

                // Move this line outside the loop
                if (dataSet) {
                    activitiesAdapter.updateData(activitiesList);
                    activityRecyclerView.setAdapter(activitiesAdapter);
                    pd.dismiss();
                }
                else {
                    pd.dismiss();
                    setContentView(R.layout.sad_layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });
    }
}