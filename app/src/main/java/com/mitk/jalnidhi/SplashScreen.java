package com.mitk.jalnidhi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitk.jalnidhi.authentication.SignInOption;
import com.mitk.jalnidhi.authentication.UserSignIn;
import com.mitk.jalnidhi.user.Home;

public class SplashScreen extends AppCompatActivity {
ImageView logo;
FirebaseAuth mAuth;
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
        logo = findViewById(R.id.logo);
        // Create a fade animation
        logo.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               if (firebaseUser != null){
                   startActivity(new Intent(SplashScreen.this, Home.class));
                   finish();
               }
               else {
                   startActivity(new Intent(SplashScreen.this, UserSignIn.class));
                   finish();
               }
            }
        }, 3000);


    }
}