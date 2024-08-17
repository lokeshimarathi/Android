package com.mitk.jalnidhi.user;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.signup.ProfileCreating;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CaptureWell extends AppCompatActivity {
    ActivityResultLauncher<String> mGetContent;
TextView longitude, latitude, runningOrNot,knowRockType;
ImageView imageSaved;
EditText waterAvailabilityDistance,wellDepth,waterDischarge, rockName;

ImageView camera;
Button submitButton;
static Uri imageUri;
static String RunningOrNot = "No", RockName, RockTypeSelection= "No";
FirebaseStorage storage;

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
        setContentView(R.layout.activity_capture_well);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        runningOrNot = findViewById(R.id.runningOrNot);
        knowRockType = findViewById(R.id.knowRockType);
        waterAvailabilityDistance = findViewById(R.id.waterAvailabilityDistance);
        wellDepth = findViewById(R.id.wellDepth);
        waterDischarge = findViewById(R.id.waterDischarge);
        imageSaved = findViewById(R.id.imageSaved);
        rockName = findViewById(R.id.rockName);
        camera = findViewById(R.id.camera);
        submitButton= findViewById(R.id.submitButton);
       Intent i = getIntent();
       String getLongitude = i.getStringExtra("longitude");
       String getLatitude = i.getStringExtra("latitude");

       longitude.setText(getLongitude);
       latitude.setText(getLatitude);
       FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String AuthUserId = firebaseUser.getUid();
        assert AuthUserId !=null;


        runningOrNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRunningOrNotSelectionDialog();
            }
        });
        knowRockType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRockTypeSelectionDialog();
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });
        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                imageUri = result;
                imageSaved.setVisibility(View.VISIBLE);
                Picasso.get().load(result).into(imageSaved);

            }
        });
        ProgressDialog pd = new ProgressDialog(CaptureWell.this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                final String setLongitude = longitude.getText().toString();
                final String setLatitude = latitude.getText().toString();
                final String setCurrentlyRunningOrNot = runningOrNot.getText().toString();
                final String setWaterAvailabilityDistance = waterAvailabilityDistance.getText().toString() + " meter";
                final String setKnowRockType = knowRockType.getText().toString();
                final String setRockName = rockName.getText().toString();
                final String setWellDepth = wellDepth.getText().toString()+ " meter";
                final String setWaterDischarge = waterDischarge.getText().toString()+ " inch";

                if(TextUtils.isEmpty(setLongitude) || TextUtils.isEmpty(setLatitude) || TextUtils.isEmpty(setCurrentlyRunningOrNot) || TextUtils.isEmpty(setWaterAvailabilityDistance) || TextUtils.isEmpty(setKnowRockType) || TextUtils.isEmpty(setRockName)  || TextUtils.isEmpty(setWellDepth) || TextUtils.isEmpty(setWaterDischarge) || imageUri == null){
                    pd.dismiss();
                    Toast.makeText(CaptureWell.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();

                }
                else {

                        storage = FirebaseStorage.getInstance();
                        StorageReference profileImagePath = storage.getReference().child(AuthUserId + "/CaptureWellRockImage").child(imageUri.getLastPathSegment());
                        profileImagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri profileImageDownloadUri) {
                                        String Key = String.valueOf(setLongitude + setLatitude);
                                        Key = Key.replace(".", "");
                                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = firebaseDatabase.getReference();
                                        databaseReference.child("CaptureWell").child(Key).child("authUserId").setValue(AuthUserId);
                                        databaseReference.child("CaptureWell").child(Key).child("longitude").setValue(setLongitude);
                                        databaseReference.child("CaptureWell").child(Key).child("latitude").setValue(setLatitude);
                                        databaseReference.child("CaptureWell").child(Key).child("currentlyRunningOrNot").setValue(setCurrentlyRunningOrNot);
                                        databaseReference.child("CaptureWell").child(Key).child("waterAvailabilityDistance").setValue(setWaterAvailabilityDistance);
                                        databaseReference.child("CaptureWell").child(Key).child("knowRockType").setValue(setKnowRockType);
                                        if("Yes".equals(setKnowRockType)){
                                            databaseReference.child("CaptureWell").child(Key).child("rockName").setValue(setRockName);
                                        }
                                        else {
                                            databaseReference.child("CaptureWell").child(Key).child("rockName").setValue("-");
                                        }
                                        databaseReference.child("CaptureWell").child(Key).child("wellDepth").setValue(setWellDepth);
                                        databaseReference.child("CaptureWell").child(Key).child("waterDischarge").setValue(setWaterDischarge);
                                        pd.dismiss();
                                        Toast.makeText(CaptureWell.this, "Details has been submitted successfully!", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CaptureWell.this, ""+e, Toast.LENGTH_SHORT).show();
                            }
                        });


                }
            }
        });

    }
    private void showRunningOrNotSelectionDialog() {
        final String[] selection = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setItems(selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RunningOrNot = selection[which];
                        runningOrNot.setText(RunningOrNot);
                    }
                });

        builder.show();
    }
    private void showRockTypeSelectionDialog() {
        final String[] selection = {"Yes", "No"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("")
                .setItems(selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RockTypeSelection = selection[which];
                        knowRockType.setText(RockTypeSelection);
                      if (RockTypeSelection == "Yes"){
                          rockName.setVisibility(View.VISIBLE);
                      }
                    }
                });

        builder.show();
    }

}
