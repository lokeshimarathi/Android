package com.raitamitra.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;




public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        requestWindowFeature(1);
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // here if condition is used to jump to conversation activity if user is signed in
                if(currentUser!= null){
                    startActivity(new Intent(SplashScreen.this, Home.class));
                    finish();
                }

                else{
                    Intent i=new Intent(SplashScreen.this, Introduction.class);
                    startActivity(i);
                    finish();
                }
            }
        }, 3000);


    }
}