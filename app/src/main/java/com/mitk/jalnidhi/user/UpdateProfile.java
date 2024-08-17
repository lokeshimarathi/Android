package com.mitk.jalnidhi.user;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.signup.ProfileCreating;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    CircleImageView profileImage;
    EditText name,state, taluk, district, village;
    Button updateButton;
    ProgressBar progressBar;
    TextView warnText;
    ActivityResultLauncher<String> mGetContent;
    FirebaseStorage storage;
    static Uri profileImageUri;
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
        setContentView(R.layout.activity_update_profile);
        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        updateButton = findViewById(R.id.updateButton);
        progressBar = findViewById(R.id.progressBar);
        warnText = findViewById(R.id.warnText);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        taluk = findViewById(R.id.taluk);
        village = findViewById(R.id.village);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String AuthUserId = firebaseUser.getUid();
        assert AuthUserId !=null;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        ProgressDialog pd  = new ProgressDialog(UpdateProfile.this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.show();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               try {
                    String getProfileImage = snapshot.child(AuthUserId).child("profileImage").getValue(String.class);
                    String getName = snapshot.child(AuthUserId).child("name").getValue(String.class);
                    String getTaluk = snapshot.child(AuthUserId).child("taluk").getValue(String.class);
                    String getDistrict = snapshot.child(AuthUserId).child("district").getValue(String.class);
                    String getState = snapshot.child(AuthUserId).child("state").getValue(String.class);
                    String getVillage = snapshot.child(AuthUserId).child("village").getValue(String.class);

                    if (profileImage != null) {
                        Picasso.get().load(Uri.parse(getProfileImage)).into(profileImage);

                    } else {
                        profileImage.setImageResource(R.drawable.profilelogo);
                    }
                    name.setText(getName);
                    taluk.setText(getTaluk);
                    district.setText(getDistrict);
                    state.setText(getState);
                    village.setText(getVillage);
                    pd.dismiss();
                }
               catch (Exception e){
                   String getName = snapshot.child(AuthUserId).child("name").getValue(String.class);
                   String getTaluk = snapshot.child(AuthUserId).child("taluk").getValue(String.class);
                   String getDistrict = snapshot.child(AuthUserId).child("district").getValue(String.class);
                   String getState = snapshot.child(AuthUserId).child("state").getValue(String.class);
                   String getVillage = snapshot.child(AuthUserId).child("village").getValue(String.class);

                   profileImage.setImageResource(R.drawable.profilelogo);
                   name.setText(getName);
                   taluk.setText(getTaluk);
                   district.setText(getDistrict);
                   state.setText(getState);
                   village.setText(getVillage);
                   pd.dismiss();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              pd.dismiss();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });
        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profileImageUri = result;
                Picasso.get().load(result).into(profileImage);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                String Name = name.getText().toString();
                String State = state.getText().toString();
                String District = district.getText().toString();
                String Taluk = taluk.getText().toString();
                String Village = village.getText().toString();

                if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(State) || TextUtils.isEmpty(District) || TextUtils.isEmpty(Taluk) || TextUtils.isEmpty(Village) ){
                    warnText.setVisibility(View.VISIBLE);

                }
                else{
                    updateButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    warnText.setVisibility(View.INVISIBLE);
                    if (profileImageUri != null){
                        storage = FirebaseStorage.getInstance();
                        StorageReference profileImagePath = storage.getReference().child(AuthUserId + "/ProfileImage").child(profileImageUri.getLastPathSegment());
                        profileImagePath.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri profileImageDownloadUri) {
                                        databaseReference.child("Users").child(AuthUserId).child("profileImage").setValue(profileImageDownloadUri.toString());
                                        databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                                        databaseReference.child("Users").child(AuthUserId).child("state").setValue(State);
                                        databaseReference.child("Users").child(AuthUserId).child("district").setValue(District);
                                        databaseReference.child("Users").child(AuthUserId).child("taluk").setValue(Taluk);
                                        databaseReference.child("Users").child(AuthUserId).child("village").setValue(Village);

                                        progressBar.setVisibility(View.INVISIBLE);
                                        updateButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(UpdateProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                warnText.setText("Failed to create account ");
                                warnText.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                updateButton.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    else{
                        databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                        databaseReference.child("Users").child(AuthUserId).child("state").setValue(State);
                        databaseReference.child("Users").child(AuthUserId).child("district").setValue(District);
                        databaseReference.child("Users").child(AuthUserId).child("taluk").setValue(Taluk);
                        databaseReference.child("Users").child(AuthUserId).child("village").setValue(Village);

                        progressBar.setVisibility(View.INVISIBLE);
                        updateButton.setVisibility(View.VISIBLE);
                        Toast.makeText(UpdateProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
}