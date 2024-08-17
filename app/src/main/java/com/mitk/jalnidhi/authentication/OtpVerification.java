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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.user.Home;
import com.mitk.jalnidhi.signup.ProfileCreating;


import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity  {

    EditText otp;
    Button verifyButton;
    TextView warnText, resendOtp;
    private String verificationId;
    FirebaseAuth Auth;
    DatabaseReference reference;
    ProgressDialog pd;
    public FirebaseAuth auth;
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
        setContentView(R.layout.activity_otp_verification);
        otp = findViewById(R.id.otp);
        verifyButton = findViewById(R.id.verifyBtn);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        resendOtp = findViewById(R.id.resendOtp);
        warnText = findViewById(R.id.warnText);
        verificationId = getIntent().getStringExtra("verificationId");

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp.getText().toString().isEmpty())
                {
                    warnText.setVisibility(View.VISIBLE);
                    return;
                }
                String code =
                        otp.getText().toString();
                if (verificationId != null) {
                    warnText.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    verifyButton.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    auth = FirebaseAuth.getInstance();
                                    progressBar.setVisibility(View.GONE);
                                    verifyButton.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        assert firebaseUser != null;
                                        String userid = firebaseUser.getUid();
                                        String phone = getIntent().getStringExtra("PhoneNumber");

                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                        reference.child("Users").orderByChild("phoneNumber").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.getValue() != null) {
                                                    Toast.makeText(OtpVerification.this, "Signed In successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else {
                                                    Intent intent = new Intent(getApplicationContext(), ProfileCreating.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("AuthUserId",userid);
                                                    intent.putExtra("PhoneNumber",phone);

                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRef = database.getReference();
                                                    myRef.child("Users").child(userid).child("phoneNumber").setValue(phone);
                                                    myRef.child("Users").child(userid).child("name").setValue("");

                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                            @Override
                                            public void onCancelled (@NonNull DatabaseError error){

                                            }
                                        });

                                    }

                                    else {
                                        warnText.setText("Sorry,Cannot be verified!");
                                        warnText.setVisibility(View.VISIBLE);
                                    }
                                }


                            });
                }

            }
        });
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "%s"+getIntent().getStringExtra("phone"),
                        60,
                        TimeUnit.SECONDS,
                        OtpVerification.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(OtpVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(OtpVerification.this, "OTP Sent", Toast.LENGTH_SHORT).show();

                            }

                        }
                );
            }
        });

    }


}