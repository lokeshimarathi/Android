package com.mitk.jalnidhi.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mitk.jalnidhi.R;

public class SignInOption extends AppCompatActivity {
static LinearLayout userSelectionBtn, adminSelectionBtn;
Button nextBtn;
ImageView userImage, adminImage;
TextView userText, adminText;

static String selected = null;
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
        setContentView(R.layout.activity_sign_in_option);
        userSelectionBtn = findViewById(R.id.userSelectionBtn);
        adminSelectionBtn = findViewById(R.id.adminSelectionBtn);
        nextBtn = findViewById(R.id.nextBtn);
        userImage = findViewById(R.id.userImage);
        adminImage = findViewById(R.id.adminImage);
        userText = findViewById(R.id.userText);
        adminText = findViewById(R.id.adminText);
        userSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               nextBtn.setVisibility(View.VISIBLE);
                selected= "user";
                userImage.setColorFilter(Color.WHITE);
                userText.setTextColor(Color.WHITE);
                adminImage.setColorFilter(Color.BLACK);
                adminText.setTextColor(Color.BLACK);
                userSelectionBtn.setBackgroundResource(R.drawable.selected_account_type);
                adminSelectionBtn.setBackgroundResource(R.drawable.account_type);
            }
        });
        adminSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextBtn.setVisibility(View.VISIBLE);
                selected= "admin";
                adminImage.setColorFilter(Color.WHITE);
                adminText.setTextColor(Color.WHITE);
                userImage.setColorFilter(Color.BLACK);
                userText.setTextColor(Color.BLACK);
                userSelectionBtn.setBackgroundResource(R.drawable.account_type);
                adminSelectionBtn.setBackgroundResource(R.drawable.selected_account_type);

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 if(selected == "user"){
                     startActivity(new Intent(getApplicationContext(), UserSignIn.class));
                 }
                 else if(selected == "admin"){
                     startActivity(new Intent(getApplicationContext(), Admin.class));
                 }



            }
        });

    }

}