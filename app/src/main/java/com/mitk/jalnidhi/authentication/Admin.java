package com.mitk.jalnidhi.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.admin.Home;
import com.mitk.jalnidhi.admin.MainActivity;

import java.util.Objects;

public class Admin extends AppCompatActivity {
EditText username, password;
Button signInButton;
TextView warnText;
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
        setContentView(R.layout.activity_admin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signInButton = findViewById(R.id.signInButton);
        warnText = findViewById(R.id.warnText);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        ProgressDialog pd = new ProgressDialog(Admin.this);
        pd.setMessage("Please wait");


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warnText.setVisibility(View.INVISIBLE);
                pd.show();
                String UserName = username.getText().toString();
                String Password = password.getText().toString();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                     try {
                         String getTaluk = snapshot.child("Admins").child(UserName).child("taluk").getValue(String.class);
                         String getPassword = snapshot.child("Admins").child(UserName).child("password").getValue(String.class);
                         if (Objects.equals(Password,getPassword)){
                             warnText.setVisibility(View.INVISIBLE);
                             pd.dismiss();
                             Toast.makeText(Admin.this, "Signed In Successfully!", Toast.LENGTH_SHORT).show();
                             Intent intent = new Intent(Admin.this, MainActivity.class);
                             intent.putExtra("Taluk",getTaluk);
                             startActivity(intent);
                             finish();

                         }
                         else {
                             pd.dismiss();
                             warnText.setVisibility(View.VISIBLE);
                         }
                     }
                     catch (Exception e){
                         warnText.setVisibility(View.VISIBLE);
                         pd.dismiss();

                     }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Admin.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
            }
        });


    }
}