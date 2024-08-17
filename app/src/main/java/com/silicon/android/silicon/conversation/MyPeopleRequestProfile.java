package com.silicon.android.silicon.conversation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPeopleRequestProfile extends AppCompatActivity {

    CircleImageView profileImage;
    TextView name, username,date, about, gender, interest, place, profession;
    Button acceptButton;
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
        setContentView(R.layout.activity_my_people_request_profile);

        username = findViewById(R.id.userName);
        name = findViewById(R.id.name);
        acceptButton = findViewById(R.id.acceptButton);
        profileImage = findViewById(R.id.profileImage);
        date = findViewById(R.id.dateOfBirth);
        gender = findViewById(R.id.gender);
        place = findViewById(R.id.place);
        profession = findViewById(R.id.profession);
        interest = findViewById(R.id.interest);
        about = findViewById(R.id.about);
        String UsersAuthUserId = getIntent().getStringExtra("usersAuthUserId");

       FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String AuthUserId = user.getUid();

        ProgressDialog pd = new ProgressDialog(MyPeopleRequestProfile.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("Users").child(UsersAuthUserId).child("name").getValue(String.class);
                name.setText(Name);
                String ProfileImage = snapshot.child("Users").child(UsersAuthUserId).child("profileImage").getValue(String.class);
                if (ProfileImage!=null){
                    Uri profileImageUri = Uri.parse(ProfileImage);
                    Picasso.get().load(profileImageUri).into(profileImage);
                }
                else {
                    profileImage.setImageResource(R.drawable.faceemoji);

                }
                String Username = snapshot.child("Users").child(UsersAuthUserId).child("userName").getValue(String.class);
                username.setText(Username);
                String DateOfBirth = snapshot.child("Users").child(UsersAuthUserId).child("dateOfBirth").getValue(String.class);
                date .setText(DateOfBirth);
                String Gender = snapshot.child("Users").child(UsersAuthUserId).child("gender").getValue(String.class);
                gender.setText(Gender);
                String Place = snapshot.child("Users").child(UsersAuthUserId).child("place").getValue(String.class);
                place.setText(Place);
                String Profession = snapshot.child("Users").child(UsersAuthUserId).child("profession").getValue(String.class);
                profession.setText(Profession);
                String Interest = snapshot.child("Users").child(UsersAuthUserId).child("interest").getValue(String.class);
                interest.setText(Interest);
                String About = snapshot.child("Users").child(UsersAuthUserId).child("about").getValue(String.class);
                about.setText(About);
                String buttonText = snapshot.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").getValue(String.class);
                if (Objects.equals(buttonText, "requested")) {
                    acceptButton.setText("Accept");
                    pd.dismiss();
                }
                else if (Objects.equals(buttonText, "connected")) {
                    acceptButton.setText("Connected");
                    pd.dismiss();
                }
                else{
                    acceptButton.setText("Connect");
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyPeopleRequestProfile.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (acceptButton.getText().equals("Accept")){
                    databaseReference.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").setValue("connected");
                    databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("connected");
                    acceptButton.setText("Connected");
                    Toast.makeText(MyPeopleRequestProfile.this, "Connected each other!", Toast.LENGTH_SHORT).show();
                }
               else if (acceptButton.getText().equals("Connected")){
                    databaseReference.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").setValue("noRequested");
                    databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("notRequested");
                    acceptButton.setText("Connect");
                    Toast.makeText(MyPeopleRequestProfile.this, "Request declined!", Toast.LENGTH_SHORT).show();
                }
                else if (acceptButton.getText().equals("Connect")){
                    databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("requested");
                    acceptButton.setText("Requested");
                    Toast.makeText(MyPeopleRequestProfile.this, "Request declined!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

}