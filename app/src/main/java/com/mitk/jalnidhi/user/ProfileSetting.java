package com.mitk.jalnidhi.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mitk.jalnidhi.R;
import com.mitk.jalnidhi.admin.Support;
import com.mitk.jalnidhi.authentication.SignInOption;

public class ProfileSetting extends AppCompatActivity {
LinearLayout profile, language, support, logout;
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
        setContentView(R.layout.activity_profile_setting);
        profile = findViewById(R.id.profile);
        language = findViewById(R.id.language);
        support = findViewById(R.id.support);
        logout = findViewById(R.id.logout);

        ProgressDialog pd = new ProgressDialog(ProfileSetting.this);
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                pd.dismiss();
                startActivity(new Intent(ProfileSetting.this, SignInOption.class));
                finish();
            }
        });
        support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSetting.this, Support.class));

            }
        });
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileSetting.this, "Language changed", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileSetting.this, UpdateProfile.class));
            }
        });
    }
}