package com.mitk.jalnidhi.user;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mitk.jalnidhi.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FindWell extends AppCompatActivity {
    SupportMapFragment smf;
    FusedLocationProviderClient client;
    EditText Longitude, Latitude,placeName;
    Button shareButton;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_find_well);
        Intent intent = getIntent();
        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");
        Longitude = findViewById(R.id.longitude);
        Latitude = findViewById(R.id.latitude);
        shareButton = findViewById(R.id.shareButton);
        placeName = findViewById(R.id.placeName);
        Longitude.setText(longitude);
        Latitude.setText(latitude);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String AuthUserId = firebaseUser.getUid();
        assert firebaseUser!=null;
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait...");

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                String currentDate = simpleDateFormat.format(new Date());
                String currentTime = simpleTimeFormat.format(new Date());
                final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                final String setLongitude= Longitude.getText().toString();
                final String setLatitude = Latitude.getText().toString();
                final String setPlaceName = placeName.getText().toString();
                if(TextUtils.isEmpty(setLongitude) || TextUtils.isEmpty(setLatitude) || TextUtils.isEmpty(setPlaceName)){
                    pd.dismiss();
                    Toast.makeText(FindWell.this, "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    databaseReference.child("Requests").child(currentTimestamp).child("authUserId").setValue(AuthUserId);
                    databaseReference.child("Requests").child(currentTimestamp).child("longitude").setValue(setLongitude);
                    databaseReference.child("Requests").child(currentTimestamp).child("latitude").setValue(setLatitude);
                    databaseReference.child("Requests").child(currentTimestamp).child("status").setValue("Approved");
                    databaseReference.child("Requests").child(currentTimestamp).child("date").setValue(currentDate);
                    databaseReference.child("Requests").child(currentTimestamp).child("time").setValue(currentTime);
                    databaseReference.child("Requests").child(currentTimestamp).child("placeName").setValue(setPlaceName);
                    pd.dismiss();
                    Intent intent = new Intent(FindWell.this, Broschure.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getmylocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public void getmylocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                smf.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("You are here...!!");

                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    }
                });
            }
        });
    }

}