package com.silicon.android.silicon.signup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.silicon.android.silicon.R;
import com.silicon.android.silicon.conversation.ConversationHome;

import java.util.Calendar;

public class ProfileCreating extends AppCompatActivity {
    CircularImageView profileImage;
    private TextView date, gender, skipButton;
    private EditText place, profession,interest, about;
    Button saveButton;
    ActivityResultLauncher<String> mGetContent;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_profile_creating);
        profileImage = findViewById(R.id.profileImage);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        place = findViewById(R.id.place);
        profession = findViewById(R.id.profession);
        interest = findViewById(R.id.interest);
        about = findViewById(R.id.about);
        skipButton = findViewById(R.id.skipButton);
        saveButton = findViewById(R.id.saveButton);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileCreating.this, ConversationHome.class));
                finish();
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
                Intent intent =new Intent(ProfileCreating.this,ProfileCropper.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderSelectionDialog();
            }
        });
        ProgressDialog pd = new ProgressDialog(ProfileCreating.this);
        pd.setTitle("Creating profile");
        pd.setCancelable(false);
        pd.setMessage("Please wait..");
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String Date = date.getText().toString();
                String Gender = gender.getText().toString().toLowerCase();
                String Place = place.getText().toString().toLowerCase();
                String Profession = profession.getText().toString().toLowerCase();
                String Interest = interest.getText().toString().toLowerCase();
                String About = about.getText().toString();

                if(TextUtils.isEmpty(Date) && TextUtils.isEmpty(Gender) && TextUtils.isEmpty(Place) && TextUtils.isEmpty(Profession) && TextUtils.isEmpty(Interest) && TextUtils.isEmpty(About) ){
                    pd.dismiss();
                    Toast.makeText(ProfileCreating.this, "All fields are empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                    databaseReference.child("Users").child(AuthUserId).child("dateOfBirth").setValue(Date);
                    databaseReference.child("Users").child(AuthUserId).child("gender").setValue(Gender);
                    databaseReference.child("Users").child(AuthUserId).child("place").setValue(Place);
                    databaseReference.child("Users").child(AuthUserId).child("profession").setValue(Profession);
                    databaseReference.child("Users").child(AuthUserId).child("interest").setValue(Interest);
                    databaseReference.child("Users").child(AuthUserId).child("about").setValue(About);
                    pd.dismiss();
                    Toast.makeText(ProfileCreating.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProfileCreating.this, ConversationHome.class));
                    finish();
                }
            }
        });

    }
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String getDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        date.setText(getDate);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }
    private void showGenderSelectionDialog() {
        final String[] genders = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedGender = genders[which];
                        gender.setText(selectedGender);
                    }
                });

        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 &&  requestCode == 101){
            assert data != null;
            String result = data.getStringExtra("RESULTP");
            final Uri[] resultUri = {null};
            if(result!=null){
                resultUri[0] = Uri.parse(result);
            }
            profileImage.setImageURI(resultUri[0]);
            Uri ProfileImageUri = Uri.parse(result);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            assert currentUser != null;
            String AuthUserId = currentUser.getUid();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String Date = date.getText().toString();
                    String Gender = gender.getText().toString().toLowerCase();
                    String Place = place.getText().toString().toLowerCase();
                    String Profession = profession.getText().toString().toLowerCase();
                    String Interest = interest.getText().toString().toLowerCase();
                    String About = about.getText().toString();
                    if(result == null && TextUtils.isEmpty(Date) && TextUtils.isEmpty(Gender) && TextUtils.isEmpty(Place) && TextUtils.isEmpty(Profession) && TextUtils.isEmpty(Interest) && TextUtils.isEmpty(About) ){

                        Toast.makeText(ProfileCreating.this, "All fields are empty!", Toast.LENGTH_SHORT).show();
                    }
                   else {
                        ProgressDialog pd = new ProgressDialog(ProfileCreating.this);
                        pd.setTitle("Profile creating");
                        pd.setCancelable(false);
                        pd.show();

                        storage = FirebaseStorage.getInstance();
                        StorageReference profileImagePath = storage.getReference().child(AuthUserId + "/ProfileImage").child(ProfileImageUri.getLastPathSegment());
                        profileImagePath.putFile(ProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri profileImageDownloadUri) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = database.getReference();
                                        databaseReference.child("Users").child(AuthUserId).child("profileImage").setValue(profileImageDownloadUri.toString());
                                        databaseReference.child("Users").child(AuthUserId).child("dateOfBirth").setValue(Date);
                                        databaseReference.child("Users").child(AuthUserId).child("gender").setValue(Gender);
                                        databaseReference.child("Users").child(AuthUserId).child("place").setValue(Place);
                                        databaseReference.child("Users").child(AuthUserId).child("profession").setValue(Profession);
                                        databaseReference.child("Users").child(AuthUserId).child("interest").setValue(Interest);
                                        databaseReference.child("Users").child(AuthUserId).child("about").setValue(About);
                                        pd.dismiss();
                                        Toast.makeText(ProfileCreating.this, "Profile created successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ProfileCreating.this, ConversationHome.class));
                                        finish();
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                pd.setMessage("Please wait: " + (int) percent + "%");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(ProfileCreating.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
            });
        }


    }
}