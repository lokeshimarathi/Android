package com.silicon.android.silicon.world.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
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
import com.silicon.android.silicon.conversation.ProfileSettings;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    CircleImageView profileImage;
    TextView name, username,date, about, gender, interest, place, profession;
    Button connectButton;
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
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.userName);
        name = findViewById(R.id.name);
        connectButton = findViewById(R.id.connectButton);
        profileImage = findViewById(R.id.profileImage);
        date = findViewById(R.id.dateOfBirth);
        gender = findViewById(R.id.gender);
        place = findViewById(R.id.place);
        profession = findViewById(R.id.profession);
        interest = findViewById(R.id.interest);
        about = findViewById(R.id.about);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String AuthUserId = user.getUid();
        String UsersAuthUserId = getIntent().getStringExtra("usersAuthUserId");
        ProgressDialog pd = new ProgressDialog(Profile.this);
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
                String buttonText = snapshot.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").getValue(String.class);
                if (Objects.equals(buttonText, "requested")){
                    connectButton.setText("Requested");
                }
               else if(Objects.equals(buttonText, "notRequested")  || buttonText == null){
                   String MyButtonText = snapshot.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").getValue(String.class);
                   if (Objects.equals(MyButtonText, "requested")){
                       connectButton.setText("Accept");
                   }
                   else {
                       connectButton.setText("Connect");

                   }
                }
             else if (Objects.equals(buttonText, "connected")){
                    connectButton.setText("Connected");
                }

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              pd.dismiss();
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
               if (connectButton.getText().equals("Connect")){
                   databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("requested");
                   connectButton.setText("Requested");
                   pd.dismiss();
               }
                else if (connectButton.getText().equals("Requested")){
                    databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("notRequested");
                    connectButton.setText("Connect");
                    pd.dismiss();
                }
            else if (connectButton.getText().equals("Connected")){
                   databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("notRequested");
                   databaseReference.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").setValue("notRequested");
                   connectButton.setText("Connect");
                   pd.dismiss();
               }
               else if (connectButton.getText().equals("Accept")){
                   databaseReference.child("Users").child(UsersAuthUserId).child("connection").child(AuthUserId).child("condition").setValue("connected");
                   databaseReference.child("Users").child(AuthUserId).child("connection").child(UsersAuthUserId).child("condition").setValue("connected");
                   connectButton.setText("Connected");
                   pd.dismiss();
               }
            }
        });



    }
}