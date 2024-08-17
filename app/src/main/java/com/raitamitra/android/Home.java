package com.raitamitra.android;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.color.utilities.Scheme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends AppCompatActivity {
 
    ImageView helpline, notification;
    CircleImageView profileImage;
    LinearLayout schemes, equipments, labourRequirements, procedure, connect, share, services;
    private Handler handler;
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        requestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        //for full screen activity and transparent status bar
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        helpline = findViewById(R.id.helpline);
        notification = findViewById(R.id.notification);
        profileImage= findViewById(R.id.profileImage);
        schemes = findViewById(R.id.schemes);
        equipments = findViewById(R.id.equipments);
        labourRequirements = findViewById(R.id.labourRequirements);
        procedure = findViewById(R.id.procedure);
        connect = findViewById(R.id.connect);
        share = findViewById(R.id.share);
        services = findViewById(R.id.services);
        services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://pradhanmantriyojana.co.in/agriculture-farmers-welfare-schemes/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String AuthUserId = user.getUid();
        ProgressDialog pd = new ProgressDialog(Home.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();
        handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Perform your refresh logic here

                // Restart the handler after 2 seconds
                handler.postDelayed(this, 2000);
            }
        };handler = new Handler();
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                // Perform your refresh logic here
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String profileImageUri = snapshot.child("Users").child(AuthUserId).child("profileImage").getValue(String.class);
                        final String phone = snapshot.child("Users").child(AuthUserId).child("phone").getValue(String.class);
                        if(profileImageUri!= null){
                            Picasso.get().load(profileImageUri).into(profileImage);
                            pd.dismiss();
                        }
                        else {
                            profileImage.setImageResource(R.drawable.face);
                            Toast.makeText(Home.this, "Please complete the profile", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Home.this, ProfileSetting.class);
                            intent.putExtra("PhoneNumber",phone);
                            startActivity(intent);
                            finish();
                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        pd.dismiss();
                    }
                });
                // Restart the handler after 2 seconds
                handler.postDelayed(this, 2000);
            }
        };
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String profileImageUri = snapshot.child("Users").child(AuthUserId).child("profileImage").getValue(String.class);
                final String phone = snapshot.child("Users").child(AuthUserId).child("phone").getValue(String.class);
                if(profileImageUri!= null){
                    Picasso.get().load(profileImageUri).into(profileImage);
                    pd.dismiss();
                }
                else {
                    profileImage.setImageResource(R.drawable.face);
                    Toast.makeText(Home.this, "Please complete the profile", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, ProfileSetting.class);
                    intent.putExtra("PhoneNumber",phone);
                    startActivity(intent);
                    finish();
                    pd.dismiss();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                pd.dismiss();
            }
        });

        schemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Schemes.class));
            }
        });
        equipments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Equipments.class));
            }
        });
        labourRequirements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LabourRequirements.class));
            }
        });
        procedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProcedureAndGuidelines.class));
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Connect.class));
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String appUrl = "com.raitamitra.android";
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

        helpline.setOnClickListener(view -> gotoUrl());
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });
        ArrayList<SlideModel> imageList = new ArrayList<>(); // Create image list

// imageList.add(new SlideModel("String Url" or R.drawable));
// imageList.add(new SlideModel("String Url" or R.drawable, "title")); You can add title

        imageList.add(new SlideModel(R.drawable.farmer1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.farmer2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.farmer3, ScaleTypes.FIT));

        ImageSlider imageSlider = findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList);



        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://farmer.gov.in/";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void gotoUrl() {
        Uri uri = Uri.parse("tel:+919686356444");
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }
    @Override
    protected void onResume() {
        super.onResume();

        // Start the handler when the activity is resumed
        handler.postDelayed(refreshRunnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop the handler when the activity is paused
        handler.removeCallbacks(refreshRunnable);
    }
}