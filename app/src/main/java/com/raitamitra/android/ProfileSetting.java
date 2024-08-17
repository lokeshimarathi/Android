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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSetting extends AppCompatActivity {
CircleImageView profileImage;
TextView gender, phoneNumber, dateOfBirth;
EditText name, address, aadhar, pan, kisan, land, feed, income;
Button saveButton;
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
        setContentView(R.layout.activity_profile_setting);


        profileImage = findViewById(R.id.profileImage);
        gender = findViewById(R.id.gender);
        phoneNumber = findViewById(R.id.phoneNumber);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        aadhar = findViewById(R.id.aadhar);
        pan = findViewById(R.id.pan);
        kisan = findViewById(R.id.kisan);
        land = findViewById(R.id.land);
        feed= findViewById(R.id.feed);
        income = findViewById(R.id.income);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        saveButton =  findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString().toLowerCase();
                String DateOfBirth = dateOfBirth.getText().toString();
                String Gender = gender.getText().toString();
                String Phone = phoneNumber.getText().toString();
                String Address = address.getText().toString().toLowerCase();
                String AadharNo = aadhar.getText().toString();
                String PanNo = pan.getText().toString();
                String KisanNo = kisan.getText().toString();
                String  Income = income.getText().toString();
                String Land = land.getText().toString().toLowerCase();
                String Feed = feed.getText().toString().toLowerCase();

                if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(DateOfBirth) ||TextUtils.isEmpty(Gender) ||TextUtils.isEmpty(Phone) ||TextUtils.isEmpty(Address) ||TextUtils.isEmpty(AadharNo) || TextUtils.isEmpty(Income)||TextUtils.isEmpty(PanNo) ||TextUtils.isEmpty(KisanNo) ||TextUtils.isEmpty(Land) ||TextUtils.isEmpty(Feed) ){
                    Toast.makeText(ProfileSetting.this, "Please select profile image and fill all fields", Toast.LENGTH_SHORT).show();
                }
               else {
                    Toast.makeText(ProfileSetting.this, "Please select profile image", Toast.LENGTH_SHORT).show();
                }

            }
        });
        Intent i= getIntent();
        String Phone = i.getStringExtra("PhoneNumber");
        phoneNumber.setText(Phone);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderSelectionDialog();
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
                Intent intent =new Intent(ProfileSetting.this,ProfileCropper.class);
                intent.putExtra("DATA", result.toString());
                startActivityForResult(intent, 101);
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
                        gender.setText(selectedGender);
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
                        dateOfBirth.setText(getDate);

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
            profileImage.setImageURI(resultUri[0]);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Name = name.getText().toString().toLowerCase();
                    String DateOfBirth = dateOfBirth.getText().toString();
                    String Gender = gender.getText().toString();
                    String Phone = phoneNumber.getText().toString();
                    String Address = address.getText().toString().toLowerCase();
                    String AadharNo = aadhar.getText().toString();
                    String PanNo = pan.getText().toString();
                    String KisanNo = kisan.getText().toString();
                    String  Income = income.getText().toString();
                    String Land = land.getText().toString().toLowerCase();
                    String Feed = feed.getText().toString().toLowerCase();
                    if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(DateOfBirth) ||TextUtils.isEmpty(Gender) ||TextUtils.isEmpty(Phone) ||TextUtils.isEmpty(Address) ||TextUtils.isEmpty(AadharNo) || TextUtils.isEmpty(Income)||TextUtils.isEmpty(PanNo) ||TextUtils.isEmpty(KisanNo) ||TextUtils.isEmpty(Land) ||TextUtils.isEmpty(Feed)){
                        Toast.makeText(ProfileSetting.this, "Please fill every field", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Intent intent = new Intent(getApplication(), DocumentUpload.class);
                        intent.putExtra("Name", Name);
                        intent.putExtra("DateOfBirth", DateOfBirth);
                        intent.putExtra("Gender", Gender);
                        intent.putExtra("Phone", Phone);
                        intent.putExtra("Address", Address);
                        intent.putExtra("AadharNo", AadharNo);
                        intent.putExtra("PanNo", PanNo);
                        intent.putExtra("KisanNo", KisanNo);
                        intent.putExtra("Land", Land);
                        intent.putExtra("Feed", Feed);
                        intent.putExtra("Income",Income);
                        final Uri resultUri = Uri.parse(result);
                        intent.putExtra("ProfileImage", resultUri+"");
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }


    }
}