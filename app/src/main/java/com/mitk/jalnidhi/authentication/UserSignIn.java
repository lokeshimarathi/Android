package com.mitk.jalnidhi.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.mitk.jalnidhi.R;
import androidx.annotation.NonNull;

import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mitk.jalnidhi.user.Home;


import java.util.concurrent.TimeUnit;
public class UserSignIn extends AppCompatActivity {
  EditText phoneNumber;
  TextView warnText;
  Button verifyButton;
  ProgressBar progressBar;
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
        setContentView(R.layout.activity_user_sign_in);
        phoneNumber = findViewById(R.id.phoneNumber);
        warnText = findViewById(R.id.warnText);
        verifyButton = findViewById(R.id.verifyBtn);
        progressBar = findViewById(R.id.progressBar);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!= null){
            startActivity(new Intent(UserSignIn.this, Home.class));
            finish();
        }

        else{
            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String phonenumber = "+91" + phoneNumber.getText().toString();

                    if (TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(phoneNumber.getText().toString()))
                    {
                        progressBar.setVisibility(View.INVISIBLE);
                        warnText.setVisibility(View.VISIBLE);
                    }
                    else {
                        warnText.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);

                        verifyButton.setVisibility(View.INVISIBLE);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "" + phonenumber,
                                60,
                                TimeUnit.SECONDS,
                                UserSignIn.this,
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
                                        warnText.setVisibility(View.VISIBLE);
                                        warnText.setText(e.getMessage());

                                    }

                                    @Override
                                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        verifyButton.setVisibility(View.VISIBLE);
                                        Intent intent = new Intent(getApplicationContext(), OtpVerification.class);
                                        intent.putExtra("PhoneNumber",phonenumber);
                                        intent.putExtra("verificationId",verificationId);
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
}