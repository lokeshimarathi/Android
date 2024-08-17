package com.mitk.jalnidhi.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.admin.AdminReportGenerator;

import java.util.Random;

public class Process extends AppCompatActivity {
TextView placeName, taluk, district, village, Longitude, Latitude,aqOneDepth, aqTwoDepth, aqThreeDepth, aqFourDepth, aqOneWidth, aqTwoWidth, aqThreeWidth, aqFourWidth, aqOneQuality, aqTwoQuality, aqThreeQuality, aqFourQuality, aqOneDischarge, aqTwoDischarge, aqThreeDischarge, aqFourDischarge, dateOne, dateTwo, dateThree, dateFour,waterdepthOne, waterdepthTwo, waterdepthThree, waterdepthFour,expectedWaterDischarge,waterDepth,expectedRock,overallWater,suitableDrillingMethod,waterQuality;
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
        setContentView(R.layout.activity_process);
        Intent intent = getIntent();
        String ActivityKey = intent.getStringExtra("ActivityKey");
        String longitude = intent.getStringExtra("Longitude");
        String latitude = intent.getStringExtra("Latitude");
        String PlaceName = intent.getStringExtra("PlaceName");

        taluk = findViewById(R.id.taluk);
        district = findViewById(R.id.district);
        village = findViewById(R.id.village);
        placeName = findViewById(R.id.placeName);
        Longitude = findViewById(R.id.Longitude);
        Latitude = findViewById(R.id.Latitude);


        aqOneDepth = findViewById(R.id.aqOnedepth);
        aqTwoDepth = findViewById(R.id.aqTwodepth);
        aqThreeDepth = findViewById(R.id.aqThreedepth);
        aqFourDepth = findViewById(R.id.aqFourdepth);


        aqOneWidth = findViewById(R.id.aqOneWidth);
        aqTwoWidth = findViewById(R.id.aqTwoWidth);
        aqThreeWidth = findViewById(R.id.aqThreeWidth);
        aqFourWidth = findViewById(R.id.aqFourWidth);
        aqOneQuality = findViewById(R.id.aqOneQuality);
        aqTwoQuality = findViewById(R.id.aqTwoQuality);
        aqThreeQuality = findViewById(R.id.aqThreeQuality);
        aqFourQuality = findViewById(R.id.aqFourQuality);
        aqOneDischarge = findViewById(R.id.aqOneDischarge);
        aqTwoDischarge = findViewById(R.id.aqTwoDischarge);
        aqThreeDischarge = findViewById(R.id.aqThreeDischarge);
        aqFourDischarge = findViewById(R.id.aqFourDischarge);


        dateOne = findViewById(R.id.dateOne);
        dateTwo = findViewById(R.id.dateTwo);
        dateThree = findViewById(R.id.dateThree);
        dateFour = findViewById(R.id.dateFour);

        waterdepthOne = findViewById(R.id.waterdepthOne);
        waterdepthTwo = findViewById(R.id.waterdepthTwo);
        waterdepthThree = findViewById(R.id.waterdepthThree);
        waterdepthFour = findViewById(R.id.waterdepthFour);


        expectedWaterDischarge = findViewById(R.id.expectedWaterDischarge);
        waterDepth = findViewById(R.id.waterDepth);
        expectedRock = findViewById(R.id.expectedRock);

        suitableDrillingMethod = findViewById(R.id.suitableDrillingMethod);
        waterQuality = findViewById(R.id.waterQuality);


        Longitude.setText(longitude);
        Latitude.setText(latitude);
        placeName.setText(PlaceName);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String AuthUserId = firebaseUser.getUid();
        assert AuthUserId != null;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        ProgressDialog pd = new ProgressDialog(Process.this);
        pd.setMessage("Please wait..");
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Taluk = snapshot.child(AuthUserId).child("taluk").getValue(String.class);
                String District = snapshot.child(AuthUserId).child("district").getValue(String.class);
                String Village = snapshot.child(AuthUserId).child("village").getValue(String.class);
                taluk.setText(Taluk);
                district.setText(District);
                village.setText(Village);
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(Process.this, ""+error, Toast.LENGTH_SHORT).show();

            }
        });

        databaseReference.child("Reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{

                }
                catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}