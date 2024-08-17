package com.silicon.android.silicon.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.silicon.android.silicon.R;

import java.util.concurrent.TimeUnit;

public class PhoneNumberVerification extends AppCompatActivity {
    private static final int RESOLVE_HINT = 0;
    EditText phoneNumber;
    Button verifyButton;
    CountryCodePicker ccp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_phone_number_verification);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        phoneNumber = findViewById(R.id.phoneNumber);
        verifyButton = findViewById(R.id.verifyButton);
        String Face ="";
        ProgressDialog pd = new ProgressDialog(PhoneNumberVerification.this);
        pd.setCancelable(false);
        pd.setTitle("Please wait");
        pd.setMessage("Verifying your account..");
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = "+91" + phoneNumber.getText().toString();
                System.out.println(phonenumber);

                if (TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(phoneNumber.getText().toString()))
                {
                    Toast.makeText(PhoneNumberVerification.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                }
                else {

                    pd.show();
                    FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(false);

                    verifyButton.setVisibility(View.INVISIBLE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "" + phonenumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneNumberVerification.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    pd.dismiss();
                                    verifyButton.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    pd.dismiss();
                                    verifyButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(PhoneNumberVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    pd.dismiss();
                                    verifyButton.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), OtpVerification.class);
                                    intent.putExtra("PhoneNumber",phonenumber);
                                    intent.putExtra("verificationId",verificationId);
                                    intent.putExtra("Face",Face);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                    );
                }


            }

        });

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                String Country = selectedCountry.getPhoneCode().toString();
                DatabaseReference reference;
                ProgressDialog pd = new ProgressDialog(PhoneNumberVerification.this);
                pd.setCancelable(false);
                pd.setTitle("Please wait..");
                verifyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String phonenumber = "+" + Country + phoneNumber.getText().toString();
                        System.out.println(phonenumber);

                        if (TextUtils.isEmpty(phonenumber) || TextUtils.isEmpty(phoneNumber.getText().toString()))
                        {
                            Toast.makeText(PhoneNumberVerification.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            pd.show();
                            verifyButton.setVisibility(View.INVISIBLE);
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "" +phonenumber,
                                    60,
                                    TimeUnit.SECONDS,
                                    PhoneNumberVerification.this,
                                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            pd.dismiss();
                                            verifyButton.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            pd.dismiss();
                                            verifyButton.setVisibility(View.VISIBLE);
                                            Toast.makeText(PhoneNumberVerification.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            pd.dismiss();
                                            verifyButton.setVisibility(View.VISIBLE);
                                            Intent intent = new Intent(getApplicationContext(), OtpVerification.class);
                                            intent.putExtra("PhoneNumber",phonenumber);
                                            intent.putExtra("verificationId",verificationId);
                                            intent.putExtra("Face",Face);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                            );

                        }
                    }
                });
            }
        });

    }

}