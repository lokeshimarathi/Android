package com.mitk.jalnidhi.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mitk.jalnidhi.R;

public class Broschure extends AppCompatActivity {
ImageView checkMark;
Button finishButton;
TextView submitText, approvalText;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_broschure);
        checkMark =  findViewById(R.id.checkMark);
        finishButton = findViewById(R.id.finishButton);
        submitText = findViewById(R.id.submitText);
        approvalText = findViewById(R.id.approvalText);
        checkMark.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));
        submitText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));
        approvalText.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in));
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}