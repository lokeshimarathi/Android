package com.raitamitra.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
public class PSignup extends AppCompatActivity {
    EditText phoneNumber;
    Button verifyButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        requestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        //for full screen activity and transparent status bar
        getWindow().setStatusBarColor(Color.WHITE);

        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_psignup);


        phoneNumber = findViewById(R.id.phoneNumber);
        verifyButton = findViewById(R.id.verifyButton);
        progressBar = findViewById(R.id.progressBar);


        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = "+91" + phoneNumber.getText().toString();
                System.out.println(phonenumber);

                if (TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(phoneNumber.getText().toString())) {
                    Toast.makeText(PSignup.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                } else {


                    verifyButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "" + phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            PSignup.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    verifyButton.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    verifyButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(PSignup.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    verifyButton.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), POtpVerification.class);
                                    intent.putExtra("PhoneNumber", phonenumber);
                                    intent.putExtra("verificationId", verificationId);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                    );
                }


            }

        });



    }

}