package com.mitk.jalnidhi.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.user.Activity;
import com.mitk.jalnidhi.user.ActivityAdapter;
import com.mitk.jalnidhi.user.ActivityList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Home extends AppCompatActivity {
    private RecyclerView activityRecyclerView;
    private String activityKey = "";
    private final List<RequestsList> requestsLists = new ArrayList<>();
    private RequestAdapter requestAdapter;
    private static boolean dataSet = false;
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
        setContentView(R.layout.activity_home2);
        Intent i = getIntent();
        String Taluk = i.getStringExtra("Taluk");
        activityRecyclerView = findViewById(R.id.activityRecyclerView);

        activityRecyclerView.setHasFixedSize(true);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(Home.this));

        //set Adapter to  recycler view
        requestAdapter = new RequestAdapter(requestsLists, Home.this);
        activityRecyclerView.setAdapter( requestAdapter);

        ProgressDialog pd = new ProgressDialog(Home.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestsLists.clear();

                // Existing code ...

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    final String getKey = dataSnapshot1.getKey();
                    final String getAuthUserId = dataSnapshot1.child("authUserId").getValue(String.class);
                    final String getLatitude = dataSnapshot1.child("latitude").getValue(String.class);
                    final String getLongitude = dataSnapshot1.child("longitude").getValue(String.class);
                    final String getDate = dataSnapshot1.child("date").getValue(String.class);
                    final String getTime = dataSnapshot1.child("time").getValue(String.class);
                    final String getStatus = dataSnapshot1.child("status").getValue(String.class);

                    if ("Not Approved".equals(getStatus)) {
                        if (getAuthUserId != null) {
                            // Move this block outside of the loop
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                    String name = snapshot2.child("Users").child(getAuthUserId).child("name").getValue(String.class);
                                    String profileImage = snapshot2.child("Users").child(getAuthUserId).child("profileImage").getValue(String.class);
                                    String phoneNumber = snapshot2.child("Users").child(getAuthUserId).child("phoneNumber").getValue(String.class);
                                    String taluk = snapshot2.child("Users").child(getAuthUserId).child("taluk").getValue(String.class);
                                    String district = snapshot2.child("Users").child(getAuthUserId).child("district").getValue(String.class);
                                    String state = snapshot2.child("Users").child(getAuthUserId).child("state").getValue(String.class);

                                    if (Objects.equals(Taluk, taluk)) {
                                        // Move this block outside of the loop
                                        activityKey = getKey; // Update activityKey within the loop
                                        RequestsList requestList = new RequestsList(activityKey, getLatitude, getLongitude, getStatus, getDate, getTime, getAuthUserId, name, profileImage, phoneNumber, taluk, district, state);
                                        requestsLists.add(requestList);
                                        dataSet = true;
                                        requestAdapter.updateData(requestsLists);
                                        pd.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                }

// Handle dataSet and dismiss ProgressDialog if no "Not Approved" requests are found
                if (!dataSet) {
                    pd.dismiss();
                    setContentView(R.layout.sad_layout);
                }

// Existing code ...





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });




    }
}