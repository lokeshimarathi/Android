package com.raitamitra.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raitamitra.android.R;

public class Notification extends AppCompatActivity {

    private SwipeRefreshLayout refreshLayout;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For full-screen activity
        requestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        // For full-screen activity and transparent status bar
        // For full-screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_notification);

        refreshLayout = findViewById(R.id.refresh_layout);
        dataTextView = findViewById(R.id.data_text_view);

        // Set colors
        refreshLayout.setColorSchemeColors(Color.BLACK);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh data from Firebase
                refreshData();
            }
        });
    }

    private void refreshData() {
        // Read data from Firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Handle retrieved data here
                        // You can update the dataTextView or perform any other necessary operations


                        // Display a toast message
                        String message = "Refreshed!";
                        Toast.makeText(Notification.this, message, Toast.LENGTH_SHORT).show();

                        // Stop the refresh animation
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error if data retrieval is unsuccessful
                        String errorMessage = "Data Retrieval Error: " + databaseError.getMessage();
                        Toast.makeText(Notification.this, errorMessage, Toast.LENGTH_SHORT).show();

                        // Stop the refresh animation
                        refreshLayout.setRefreshing(false);
                    }
                });
    }
}
