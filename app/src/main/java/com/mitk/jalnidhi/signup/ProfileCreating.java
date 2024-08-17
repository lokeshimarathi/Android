package com.mitk.jalnidhi.signup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.user.Home;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreating extends AppCompatActivity {
CircleImageView profileImage;
EditText name,state, taluk, district, village;
Button nextButton;
ProgressBar progressBar;
TextView warnText;
ActivityResultLauncher<String> mGetContent;
FirebaseStorage storage;
static boolean profilePicState = false;
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
        setContentView(R.layout.activity_profile_creating);
        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        nextButton = findViewById(R.id.nextBtn);
        progressBar = findViewById(R.id.progressBar);
        warnText = findViewById(R.id.warnText);
        state = findViewById(R.id.state);
        district = findViewById(R.id.district);
        taluk = findViewById(R.id.taluk);
        village = findViewById(R.id.village);
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
                profilePicState = true;
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = database.getReference();
                String Name = name.getText().toString();
                String State = state.getText().toString();
                String District = district.getText().toString();
                String Taluk = taluk.getText().toString();
                String Village = village.getText().toString();

                if(TextUtils.isEmpty(Name) || TextUtils.isEmpty(State) || TextUtils.isEmpty(District) || TextUtils.isEmpty(Taluk) || TextUtils.isEmpty(Village)){
                    warnText.setVisibility(View.VISIBLE);

                }
               else{
                   nextButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    warnText.setVisibility(View.INVISIBLE);
                   if (profilePicState){
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
                                       nextButton.setVisibility(View.VISIBLE);
                                       Toast.makeText(ProfileCreating.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                                       startActivity(new Intent(ProfileCreating.this, Home.class));
                                       finish();
                                   }
                               });
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               warnText.setText("Failed to create account ");
                               warnText.setVisibility(View.VISIBLE);
                               progressBar.setVisibility(View.INVISIBLE);
                               nextButton.setVisibility(View.VISIBLE);
                           }
                       });
                   }
                   else {
                       databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                       databaseReference.child("Users").child(AuthUserId).child("state").setValue(State);
                       databaseReference.child("Users").child(AuthUserId).child("district").setValue(District);
                       databaseReference.child("Users").child(AuthUserId).child("taluk").setValue(Taluk);
                       progressBar.setVisibility(View.INVISIBLE);
                       nextButton.setVisibility(View.VISIBLE);
                       Toast.makeText(ProfileCreating.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(ProfileCreating.this, Home.class));
                       finish();
                   }
                }
            }
        });
    }

}