package com.silicon.android.silicon.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;
import com.silicon.android.silicon.signin.SignIn;

public class SignUp extends AppCompatActivity {
TextView usernameError, passwordError, phoneNumber;
EditText username, name, password;
Button nextButton;
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
        setContentView(R.layout.activity_sign_up);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        usernameError =findViewById(R.id.usernameError);
        passwordError = findViewById(R.id.passwordError);
        phoneNumber = findViewById(R.id.phoneNumber);
        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        nextButton = findViewById(R.id.nextButton);
        eye = findViewById(R.id.eye);
        Intent intent = getIntent();
        String PhoneNumber = intent.getStringExtra("PhoneNumber");
        String AuthUserId =  intent.getStringExtra("AuthUserId");
        phoneNumber.setText(PhoneNumber);
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
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordError.setVisibility(View.INVISIBLE);
                usernameError.setVisibility(View.INVISIBLE);

                String UserName = username.getText().toString().toLowerCase().replaceAll(" ", "");                String Name = name.getText().toString();
                String Password = password.getText().toString();

                if (TextUtils.isEmpty(Name) ||TextUtils.isEmpty(UserName)|| TextUtils.isEmpty(Password) ){
                    Toast.makeText(SignUp.this, "Please fill every field!", Toast.LENGTH_SHORT).show();
                } else if (Password.length()<8) {
                    passwordError.setVisibility(View.VISIBLE);
                    usernameError.setVisibility(View.INVISIBLE);

                }
                else {
                    databaseReference.child("Users").orderByChild("userName").equalTo(UserName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                               passwordError.setVisibility(View.INVISIBLE);
                               usernameError.setVisibility(View.VISIBLE);
                            }
                            else {
                                passwordError.setVisibility(View.INVISIBLE);
                                usernameError.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(getApplicationContext(), ProfileCreating.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                databaseReference.child("Users").child(AuthUserId).child("name").setValue(Name);
                                databaseReference.child("Users").child(AuthUserId).child("userName").setValue(UserName);
                                databaseReference.child("Users").child(AuthUserId).child("password").setValue(Password);
                                databaseReference.child("Users").child(AuthUserId).child("phoneNumber").setValue(PhoneNumber);
                                startActivity(intent);
                                Toast.makeText(SignUp.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                        @Override
                        public void onCancelled (@NonNull DatabaseError error){
                            Toast.makeText(SignUp.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();

                        }
                    });
                }


            }
        });

    }
}