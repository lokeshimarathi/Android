package com.silicon.android.silicon.conversation;

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
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.silicon.android.silicon.signup.ProfileCreating;
import com.silicon.android.silicon.signup.ProfileCropper;
import com.silicon.android.silicon.signup.SignUp;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Objects;

public class ProfileSettings extends AppCompatActivity {
    TextView usernameError, passwordError, phoneNumber, date, gender;
    EditText username, name, password,place, profession,interest, about;
    Button saveButton;
    CircularImageView profileImage;
    ActivityResultLauncher<String> mGetContent;
    FirebaseStorage storage;
    ImageView eye;

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
        setContentView(R.layout.activity_profile_settings);
        usernameError =findViewById(R.id.usernameError);
        passwordError = findViewById(R.id.passwordError);
        phoneNumber = findViewById(R.id.phoneNumber);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        saveButton = findViewById(R.id.saveButton);
        profileImage = findViewById(R.id.profileImage);
        date = findViewById(R.id.date);
        gender = findViewById(R.id.gender);
        place = findViewById(R.id.place);
        profession = findViewById(R.id.profession);
        interest = findViewById(R.id.interest);
        about = findViewById(R.id.about);
        eye = findViewById(R.id.eye);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();
        ProgressDialog pd = new ProgressDialog(ProfileSettings.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("Users").child(AuthUserId).child("name").getValue(String.class);
                name.setText(Name);
                String ProfileImage = snapshot.child("Users").child(AuthUserId).child("profileImage").getValue(String.class);
                if (ProfileImage!=null){
                    Uri profileImageUri = Uri.parse(ProfileImage);
                    Picasso.get().load(profileImageUri).into(profileImage);
                }
                else {
                    profileImage.setImageResource(R.drawable.faceemoji);

                }
                String Username = snapshot.child("Users").child(AuthUserId).child("userName").getValue(String.class);
                username.setText(Username);
                String Password = snapshot.child("Users").child(AuthUserId).child("password").getValue(String.class);
                password.setText(Password);
                String PhoneNumber = snapshot.child("Users").child(AuthUserId).child("phoneNumber").getValue(String.class);
                phoneNumber.setText(PhoneNumber);
                String DateOfBirth = snapshot.child("Users").child(AuthUserId).child("dateOfBirth").getValue(String.class);
                date .setText(DateOfBirth);
                String Gender = snapshot.child("Users").child(AuthUserId).child("gender").getValue(String.class);
                gender.setText(Gender);
                String Place = snapshot.child("Users").child(AuthUserId).child("place").getValue(String.class);
                place.setText(Place);
                String Profession = snapshot.child("Users").child(AuthUserId).child("profession").getValue(String.class);
                profession.setText(Profession);
                String Interest = snapshot.child("Users").child(AuthUserId).child("interest").getValue(String.class);
                interest.setText(Interest);
                String About = snapshot.child("Users").child(AuthUserId).child("about").getValue(String.class);
                about.setText(About);
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(ProfileSettings.this, "Please try again", Toast.LENGTH_SHORT).show();
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
                Intent intent =new Intent(ProfileSettings.this, ProfileCropper.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
        password.setTransformationMethod(new PasswordTransformationMethod());
        eye.setOnClickListener(new View.OnClickListener() {
            boolean passwordVisible = false;
            @Override
            public void onClick(View view) {
                // Toggle the password visibility flag
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    // If the flag is true, show the password
                    password.setTransformationMethod(null);
                    eye.setImageResource(R.drawable.ic_eye);
                    // Change the button icon
                } else {
                    // If the flag is false, hide the password
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    eye.setImageResource(R.drawable.ic_eye);
                    // Change the button icon
                }

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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordError.setVisibility(View.INVISIBLE);
                usernameError.setVisibility(View.INVISIBLE);
                String UserName = username.getText().toString().toLowerCase().replaceAll(" ", "");
                String Name = name.getText().toString();
                String Password = password.getText().toString();
                String Date = date.getText().toString();
                String Gender = gender.getText().toString().toLowerCase();
                String Place = place.getText().toString().toLowerCase();
                String Profession = profession.getText().toString().toLowerCase();
                String Interest = interest.getText().toString().toLowerCase();
                String About = about.getText().toString();
                if(TextUtils.isEmpty(UserName)){
                    Toast.makeText(ProfileSettings.this, "Please fill Username", Toast.LENGTH_SHORT).show();


                }
                if(TextUtils.isEmpty(UserName)){
                    Toast.makeText(ProfileSettings.this, "Please fill Name", Toast.LENGTH_SHORT).show();

                }
                if(TextUtils.isEmpty(UserName)){
                    Toast.makeText(ProfileSettings.this, "Please fill Password", Toast.LENGTH_SHORT).show();

                }

                if (Password.length()<8){
                    passwordError.setVisibility(View.VISIBLE);
                    usernameError.setVisibility(View.INVISIBLE);
                }
                else {
                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    String userKey = userSnapshot.getKey();
                                    // Do something with the user key
                                    if(!Objects.equals(userKey, AuthUserId)){
                                        assert userKey != null;
                                        databaseReference.child("Users").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String username = snapshot.child("userName").getValue(String.class);
                                                if (UserName.equals(username)) {
                                                    Toast.makeText(ProfileSettings.this, "This User name is not available", Toast.LENGTH_SHORT).show();
                                                    usernameError.setVisibility(View.VISIBLE);
                                                }
                                                else {
                                                    usernameError.setVisibility(View.INVISIBLE);
                                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                                    DatabaseReference databaseReference = firebaseDatabase.getReference();
                                                    databaseReference.child("Users").child(AuthUserId).child("dateOfBirth").setValue(Date);
                                                    databaseReference.child("Users").child(AuthUserId).child("gender").setValue(Gender);
                                                    databaseReference.child("Users").child(AuthUserId).child("place").setValue(Place);
                                                    databaseReference.child("Users").child(AuthUserId).child("profession").setValue(Profession);
                                                    databaseReference.child("Users").child(AuthUserId).child("interest").setValue(Interest);
                                                    databaseReference.child("Users").child(AuthUserId).child("about").setValue(About);
                                                    databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                                                    databaseReference.child("Users").child(AuthUserId).child("userName").setValue(UserName);
                                                    databaseReference.child("Users").child(AuthUserId).child("password").setValue(Password);

                                                    Toast.makeText(ProfileSettings.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled (@NonNull DatabaseError error){
                                                Toast.makeText(ProfileSettings.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ProfileSettings.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();

                        }
                    });


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
                    passwordError.setVisibility(View.INVISIBLE);
                    usernameError.setVisibility(View.INVISIBLE);
                    String UserName = username.getText().toString().toLowerCase().replaceAll(" ", "");                    String Name = name.getText().toString();
                    String Password = password.getText().toString();
                    String Date = date.getText().toString();
                    String Gender = gender.getText().toString().toLowerCase();
                    String Place = place.getText().toString().toLowerCase();
                    String Profession = profession.getText().toString().toLowerCase();
                    String Interest = interest.getText().toString().toLowerCase();
                    String About = about.getText().toString();
                    if(TextUtils.isEmpty(UserName)){
                        Toast.makeText(ProfileSettings.this, "Please fill Username", Toast.LENGTH_SHORT).show();


                    }
                    if(TextUtils.isEmpty(Name)){
                        Toast.makeText(ProfileSettings.this, "Please fill Name", Toast.LENGTH_SHORT).show();

                    }
                    if(TextUtils.isEmpty(Password)){
                        Toast.makeText(ProfileSettings.this, "Please fill Password", Toast.LENGTH_SHORT).show();

                    }
                    if (Password.length()<8){
                        passwordError.setVisibility(View.VISIBLE);
                        usernameError.setVisibility(View.INVISIBLE);
                    }
                    else {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = firebaseDatabase.getReference();
                        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        String userKey = userSnapshot.getKey();
                                        // Do something with the user key
                                        if(!Objects.equals(userKey, AuthUserId)){
                                            assert userKey != null;
                                            databaseReference.child("Users").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String username = snapshot.child("userName").getValue(String.class);
                                                    if (UserName.equals(username)) {
                                                        Toast.makeText(ProfileSettings.this, "This User name is not available", Toast.LENGTH_SHORT).show();
                                                        usernameError.setVisibility(View.VISIBLE);
                                                    }
                                                    else {
                                                        passwordError.setVisibility(View.INVISIBLE);
                                                        usernameError.setVisibility(View.INVISIBLE);
                                                        ProgressDialog pd = new ProgressDialog(ProfileSettings.this);
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
                                                                        databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                                                                        databaseReference.child("Users").child(AuthUserId).child("userName").setValue(UserName);
                                                                        databaseReference.child("Users").child(AuthUserId).child("password").setValue(Password);
                                                                        pd.dismiss();
                                                                        Toast.makeText(ProfileSettings.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

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
                                                                Toast.makeText(ProfileSettings.this, "Please try again", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                         }
                                                }
                                                @Override
                                                public void onCancelled (@NonNull DatabaseError error){
                                                    Toast.makeText(ProfileSettings.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(ProfileSettings.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }


                }
            });
        }


    }
}