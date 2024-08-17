package com.silicon.android.silicon;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.silicon.android.silicon.authentication.OtpVerification;
import com.silicon.android.silicon.authentication.PhoneNumberVerification;
import com.silicon.android.silicon.conversation.ConversationHome;
import com.silicon.android.silicon.signup.ProfileCreating;
import com.silicon.android.silicon.signup.SignUp;


public class SplashScreen extends AppCompatActivity {
    LinearLayout logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        supportRequestWindowFeature(1);
        getWindow().setFlags (WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //for full screen activity and transparent status bar
        getWindow().setStatusBarColor(Color.WHITE);
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_splash_screen);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseApp.initializeApp(/*context=*/ this);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);

        // Create a fade animation
        logo.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // here if condition is used to jump to conversation activity if user is signed in
                if(currentUser!= null){
                    startActivity(new Intent(SplashScreen.this, ConversationHome.class));
                    finish();
                }

                else{
                    Intent i=new Intent(SplashScreen.this, PhoneNumberVerification.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.

                    finish();
                    //the current activity will get finished.
                }
            }
        }, 3000);


    }
}