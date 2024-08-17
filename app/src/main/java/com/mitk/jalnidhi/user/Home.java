package com.mitk.jalnidhi.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    LinearLayout findWell, captureWell, rechargeWell, aboutJalNidhi, activity, shareApp;
    CircleImageView profileImage;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    static double latitude;
    static double longitude;


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
        setContentView(R.layout.activity_home);

        findWell = findViewById(R.id.findWell);

        rechargeWell = findViewById(R.id.rechargeWell);
        aboutJalNidhi = findViewById(R.id.aboutJalNidhi);
        activity = findViewById(R.id.activity);
        shareApp = findViewById(R.id.shareApp);
rechargeWell.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(Home.this, RechargeWell.class));
    }
});
        profileImage = findViewById(R.id.profileImage);
        ArrayList<SlideModel> imageList = new ArrayList<>(); // Create image list

      // imageList.add(new SlideModel("String Url" or R.drawable));
        // imageList.add(new SlideModel("String Url" or R.drawable, "title")); You can add title

        imageList.add(new SlideModel(R.drawable.groundwater1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.undergroundwatervalue, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.groundwater3, ScaleTypes.FIT));

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String AuthUserId = firebaseUser.getUid();
     shareApp.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             try {

                 String appUrl = "https://play.google.com/store/apps/details?id=com.mitk.jalnidhi";
                 Intent sharing = new Intent(Intent.ACTION_SEND);
                 sharing.setType("text/plain");
                 sharing.putExtra(Intent.EXTRA_SUBJECT, "Download Now");
                 sharing.putExtra(Intent.EXTRA_TEXT, appUrl);
                 startActivity(Intent.createChooser(sharing, "Share via"));


             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
     });

     aboutJalNidhi.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
         startActivity(new Intent(Home.this, AboutJalNidhi.class));
         }
     });
        ProgressDialog pd = new ProgressDialog(Home.this);
        pd.setMessage("Please wait...");
        pd.show();
        try {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    try{
                        String ProfileImage = snapshot.child("Users").child(AuthUserId).child("profileImage").getValue(String.class);
                        Uri profileImageUri = Uri.parse(ProfileImage);
                        if (profileImageUri != null) {
                            Picasso.get().load(profileImageUri).into(profileImage);
                        }
                        else {
                            profileImage.setImageResource(R.drawable.profilelogo);
                        }

                        pd.dismiss();
                    }
                    catch (Exception e){
                        profileImage.setImageResource(R.drawable.profilelogo);
                        pd.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pd.dismiss();
                    Toast.makeText(Home.this, "Error! please try again..", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception exception) {
            pd.dismiss();
            Toast.makeText(this, "" + exception, Toast.LENGTH_SHORT).show();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle live location updates here
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(Home.this, ""+latitude, Toast.LENGTH_SHORT).show();
                }
            }
        };

        requestLocationPermission();

        findWell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, FindWell.class);
                intent.putExtra("latitude",String.valueOf(latitude));
                intent.putExtra("longitude",String.valueOf(longitude));
               //Toast.makeText(Home.this, ""+String.valueOf(latitude), Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });



        activity.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(Home.this, Activity.class));
          }
      });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new
                        Intent(Home.this, ProfileSetting.class));
            }
        });
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
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
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
