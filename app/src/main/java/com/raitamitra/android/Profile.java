package com.raitamitra.android;

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
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    CircleImageView profileImage;
    TextView Gender1, PhoneNumber1, DateOfBirth1;
    EditText Name1, Address1, Aadhar1, Pan1, Kisan1, Land1, Feed1, Income1;
    Button saveButton;
    FirebaseStorage storage;
    ActivityResultLauncher<String> mGetContent;
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
        setContentView(R.layout.activity_profile);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        profileImage = findViewById(R.id.profileImage);
        Gender1 = findViewById(R.id.gender);
        PhoneNumber1 = findViewById(R.id.phoneNumber);
        Name1 = findViewById(R.id.name);
        Address1 = findViewById(R.id.address);
        Aadhar1 = findViewById(R.id.aadhar);
        Pan1 = findViewById(R.id.pan);
        Kisan1 = findViewById(R.id.kisan);
        Land1 = findViewById(R.id.land);
        Feed1= findViewById(R.id.feed);
        Income1 = findViewById(R.id.income);
        DateOfBirth1 = findViewById(R.id.dateOfBirth);
        saveButton =  findViewById(R.id.saveButton);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String AuthUserId = user.getUid();
        ProgressDialog pd = new ProgressDialog(Profile.this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profileImageUri = snapshot.child(AuthUserId).child("profileImage").getValue(String.class);

                if(profileImageUri!= null){
                    Picasso.get().load(profileImageUri).into(profileImage);

                }
                else {
                    profileImage.setImageResource(R.drawable.face);

                }
             String gender = snapshot.child(AuthUserId).child("gender").getValue(String.class);
                Gender1.setText(gender);
                 String phoneNumber = snapshot.child(AuthUserId).child("phone").getValue(String.class);
                  PhoneNumber1.setText(phoneNumber);
                String name = snapshot.child(AuthUserId).child("name").getValue(String.class);
                Name1.setText(name);
                String address = snapshot.child(AuthUserId).child("address").getValue(String.class);
                Address1.setText(address);
                String aadhar = snapshot.child(AuthUserId).child("aadharNo").getValue(String.class);
                Aadhar1.setText(aadhar);
                String pan = snapshot.child(AuthUserId).child("panNo").getValue(String.class);
                Pan1.setText(pan);
                String kisan = snapshot.child(AuthUserId).child("kisanNo").getValue(String.class);
                Kisan1.setText(kisan);
                String land = snapshot.child(AuthUserId).child("land").getValue(String.class);
                Land1.setText(land);
                String feed = snapshot.child(AuthUserId).child("feed").getValue(String.class);
                Feed1.setText(feed);
                Integer income = snapshot.child(AuthUserId).child("income").getValue(Integer.class);
                String Income = String.valueOf(income);
                Income1.setText(Income);
                String dateOfBirth = snapshot.child(AuthUserId).child("dateOfBirth").getValue(String.class);
                DateOfBirth1.setText(dateOfBirth);
                pd.dismiss();

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
                Intent intent =new Intent(Profile.this,ProfileCropper.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
            }
        });
        DateOfBirth1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        Gender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderSelectionDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                String Name = Name1.getText().toString().toLowerCase();
                String DateOfBirth = DateOfBirth1.getText().toString();
                String Gender = Gender1.getText().toString();
                String Phone = PhoneNumber1.getText().toString();
                String Address = Address1.getText().toString().toLowerCase();
                String AadharNo = Aadhar1.getText().toString();
                String PanNo = Pan1.getText().toString();
                String KisanNo = Kisan1.getText().toString();
                String  Income = Income1.getText().toString();
                String Land = Land1.getText().toString().toLowerCase();
                String Feed = Feed1.getText().toString().toLowerCase();
                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(DateOfBirth) ||TextUtils.isEmpty(Gender)|| TextUtils.isEmpty(Income) ||TextUtils.isEmpty(Phone) ||TextUtils.isEmpty(Address) ||TextUtils.isEmpty(AadharNo) ||TextUtils.isEmpty(PanNo) ||TextUtils.isEmpty(KisanNo) ||TextUtils.isEmpty(Land) ||TextUtils.isEmpty(Feed) || profileImage == null){
                    Toast.makeText(Profile.this, "Please fill every field", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else {

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = database.getReference();
                    int income = Integer.parseInt(Income);
                    databaseReference.child("Users").child(AuthUserId).child("income").setValue(income);
                    databaseReference.child("Users").child(AuthUserId).child("phone").setValue(Phone);
                    databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                    databaseReference.child("Users").child(AuthUserId).child("dateOfBirth").setValue(DateOfBirth);
                    databaseReference.child("Users").child(AuthUserId).child("gender").setValue(Gender);
                    databaseReference.child("Users").child(AuthUserId).child("aadharNo").setValue(AadharNo);
                    databaseReference.child("Users").child(AuthUserId).child("panNo").setValue(PanNo);
                    databaseReference.child("Users").child(AuthUserId).child("kisanNo").setValue(KisanNo);
                    databaseReference.child("Users").child(AuthUserId).child("land").setValue(Land);
                    databaseReference.child("Users").child(AuthUserId).child("address").setValue(Address);

                    pd.dismiss();
                    Toast.makeText(Profile.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void showGenderSelectionDialog() {
        final String[] genders = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender")
                .setItems(genders, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedGender = genders[which];
                        Gender1.setText(selectedGender);
                    }
                });

        builder.show();
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
                        DateOfBirth1.setText(getDate);
                    }
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
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
            Uri  profileImageUri = Uri.parse(result);
            profileImage.setImageURI(resultUri[0]);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String AuthUserId = user.getUid();
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Name = Name1.getText().toString().toLowerCase();
                    String DateOfBirth = DateOfBirth1.getText().toString();
                    String Gender = Gender1.getText().toString();
                    String Phone = PhoneNumber1.getText().toString();
                    String Address = Address1.getText().toString().toLowerCase();
                    String AadharNo = Aadhar1.getText().toString();
                    String PanNo = Pan1.getText().toString();
                    String KisanNo = Kisan1.getText().toString();
                    String  Income = Income1.getText().toString();
                    String Land = Land1.getText().toString().toLowerCase();
                    String Feed = Feed1.getText().toString().toLowerCase();
                    if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(DateOfBirth) ||TextUtils.isEmpty(Gender) ||TextUtils.isEmpty(Phone) ||TextUtils.isEmpty(Address) ||TextUtils.isEmpty(AadharNo) ||TextUtils.isEmpty(PanNo) ||TextUtils.isEmpty(KisanNo) ||TextUtils.isEmpty(Land) ||TextUtils.isEmpty(Feed) || profileImage == null){
                        Toast.makeText(Profile.this, "Please fill every field", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        ProgressDialog pd = new ProgressDialog(Profile.this);
                        pd.setTitle("Please wait..");
                        pd.setCancelable(false);
                        pd.show();

                        storage = FirebaseStorage.getInstance();
                        StorageReference profileImagePath = storage.getReference().child(AuthUserId + "/ProfileImage").child(profileImageUri.getLastPathSegment());
                        profileImagePath.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri profileImageDownloadUri) {
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = database.getReference();
                                        int income = Integer.parseInt(Income);
                                        databaseReference.child("Users").child(AuthUserId).child("income").setValue(income);
                                        databaseReference.child("Users").child(AuthUserId).child("phone").setValue(Phone);
                                        databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                                        databaseReference.child("Users").child(AuthUserId).child("dateOfBirth").setValue(DateOfBirth);
                                        databaseReference.child("Users").child(AuthUserId).child("gender").setValue(Gender);
                                        databaseReference.child("Users").child(AuthUserId).child("aadharNo").setValue(AadharNo);
                                        databaseReference.child("Users").child(AuthUserId).child("panNo").setValue(PanNo);
                                        databaseReference.child("Users").child(AuthUserId).child("kisanNo").setValue(KisanNo);
                                        databaseReference.child("Users").child(AuthUserId).child("land").setValue(Land);
                                        databaseReference.child("Users").child(AuthUserId).child("feed").setValue(Feed);
                                        databaseReference.child("Users").child(AuthUserId).child("address").setValue(Address);
                                        databaseReference.child("Users").child(AuthUserId).child("profileImage").setValue(profileImageDownloadUri.toString());
                                        pd.dismiss();
                                        Toast.makeText(Profile.this, "Updated successfully", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                pd.setMessage("Updating: " + (int) percent + "%");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(Profile.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

            });
        }


    }
}