package com.mitk.jalnidhi.admin;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.user.Home;

import java.lang.reflect.Array;
import java.util.Random;

public class AdminReportGenerator extends AppCompatActivity {
TextView Longitude, Latitude, waterAvailability, expectedWaterDischarge, waterDepth,expectedRock, expectedWaterQuality;
Button finishButton;
    static String waterAvailabilityR;
    static String expectedWater;
    static String WaterDepth;
    static String ExpectedRock;
    static String expectedWaterQuality1;
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
        setContentView(R.layout.activity_admin_report_generator);
        Intent intent = getIntent();
        String longitude = intent.getStringExtra("Longitude");
        String latitude = intent.getStringExtra("Latitude");
        Longitude = findViewById(R.id.Longitude);
        Latitude = findViewById(R.id.Latitude);
        waterAvailability = findViewById(R.id.waterAvailability);
        expectedWaterDischarge = findViewById(R.id.expectedWaterDischarge);
        waterDepth = findViewById(R.id.waterDepth);
        expectedRock = findViewById(R.id.expectedRock);
        finishButton = findViewById(R.id.finishButton);
        expectedWaterQuality = findViewById(R.id.expectedWater);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        ProgressDialog pd = new ProgressDialog(AdminReportGenerator.this);
        pd.setMessage("Please wait...");
        pd.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                   String Key = String.valueOf(longitude + latitude);
                   Key = Key.replace(".", "");
                  try{

                      String getLongitude = snapshot.child("Reports").child(Key).child("longitude").getValue(String.class);

                      String getLatitude = snapshot.child("Reports").child(Key).child("latitude").getValue(String.class);
                      String getWaterAvailability = snapshot.child("Reports").child(Key).child("waterAvailability").getValue(String.class);
                      String getExpectedWaterDischarge = snapshot.child("Reports").child(Key).child("expectedWaterDischarge").getValue(String.class);
                      String getWaterDepth = snapshot.child("Reports").child(Key).child("waterDepth").getValue(String.class);
                      String getExpectedRock = snapshot.child("Reports").child(Key).child("expectedRock").getValue(String.class);
                      String getExpectedWaterQuality = snapshot.child("Reports").child(Key).child("expectedWaterQuality").getValue(String.class);
                      if (getLongitude != null && getLatitude != null && getWaterAvailability != null ){
                          Longitude.setText(getLongitude);
                          Latitude.setText(getLatitude);
                          waterAvailability.setText(getWaterAvailability);
                          expectedWaterDischarge.setText(getExpectedWaterDischarge);
                          waterDepth.setText(getWaterDepth);
                          expectedRock.setText(getExpectedRock);
                          expectedWaterQuality.setText(getExpectedWaterQuality);
                          pd.dismiss();

                      }
                      else if (getLongitude == null) {
                          pd.dismiss();
                          Longitude.setText(longitude);
                          Latitude.setText(latitude);
                          int min = 1;
                          int max = 100;

                          // Create an instance of Random class
                          Random random = new Random();

                          // Generate a random number within the specified range
                          int randomNumber = random.nextInt(max - min + 1) + min;

                          // Convert the random number to a String
                          waterAvailabilityR = String.valueOf(randomNumber);

                          waterAvailability.setText(waterAvailabilityR+" %");


                          int randomNumber2 = random.nextInt(5 - 1 + 1) + 1;

                          // Convert the random number to a String
                          expectedWater = String.valueOf(randomNumber2);

                          expectedWaterDischarge.setText(expectedWater+" inch");


                          int randomNumber3 = random.nextInt(100 - 50 + 1) + 50;

                          // Convert the random number to a String
                          WaterDepth = String.valueOf(randomNumber3);

                          waterDepth.setText(WaterDepth+" meter");


                          int randomNumber4 = random.nextInt(4 - 0 + 1) + 1;
                          String[] expectedRockList = {"x","XX", "XXX", "XXXX","XXXXX"};
                          // Convert the random number to a String
                          ExpectedRock = expectedRockList[randomNumber4];
                          expectedRock.setText(ExpectedRock);

                          int randomNumber5 = random.nextInt(4 - 0 + 1) + 1;
                          String[] expectedWaterQualityList = {"Impure","Pure", "Medium", "Bad","Too Bad"};
                          // Convert the random number to a String
                          expectedWaterQuality1 = expectedWaterQualityList[randomNumber5];
                          expectedWaterQuality.setText(expectedWaterQuality1);

                      }
                  }
                  catch (Exception exception){
                      Toast.makeText(AdminReportGenerator.this, ""+exception, Toast.LENGTH_SHORT).show();
                  }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             final String setLongitude = Longitude.getText().toString();
             final String setLatitude = Latitude.getText().toString();
             final String setWaterAvailability = waterAvailability.getText().toString();
             final String setExpectedWaterDischarge = expectedWaterDischarge.getText().toString();
             final String setWaterDepth = waterDepth.getText().toString();
             final String setExpectedRock = expectedRock.getText().toString();
             final String setExpectedWaterQuality = expectedWaterQuality.getText().toString();

              String Key = String.valueOf(longitude + latitude);
                    Key = Key.replace(".", "");
                   databaseReference.child("Reports").child(Key).child("longitude").setValue(setLongitude);
                   databaseReference.child("Reports").child(Key).child("latitude").setValue(setLatitude);
                   databaseReference.child("Reports").child(Key).child("waterAvailability").setValue(setWaterAvailability);
                   databaseReference.child("Reports").child(Key).child("expectedWaterDischarge").setValue(setExpectedWaterDischarge);
                   databaseReference.child("Reports").child(Key).child("waterDepth").setValue(setWaterDepth);
                   databaseReference.child("Reports").child(Key).child("expectedRock").setValue(setExpectedRock);
                   databaseReference.child("Reports").child(Key).child("expectedWaterQuality").setValue(setExpectedWaterQuality);
                   pd.dismiss();
                   Toast.makeText(AdminReportGenerator.this, "Report created successfully!", Toast.LENGTH_SHORT).show();

            }
        });

    }
}