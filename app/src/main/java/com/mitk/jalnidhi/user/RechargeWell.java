package com.mitk.jalnidhi.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mitk.jalnidhi.R;

public class RechargeWell extends AppCompatActivity {
TextView whatIs, whatIsGroundWater, importanceOfGroundWater, howToRecharge;
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
        setContentView(R.layout.activity_recharge_well);
        whatIs = findViewById(R.id.whatIs);
        whatIsGroundWater = findViewById(R.id.whatIsGroundWaterRecharge);
        importanceOfGroundWater = findViewById(R.id.importanceOfGroundWater);
        howToRecharge = findViewById(R.id.howToRecharge);

        whatIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://youtu.be/oNWAerr_xEE?si=obNeMoXYAuViwxo4";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });
        whatIsGroundWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://youtu.be/9UDBYSgDeFs?si=13Iu1T7ebiXhzown";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
        });

        importanceOfGroundWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://youtu.be/v8VotOaT4lk?si=iou_n1mmG5JEn9Cn";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        howToRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://youtu.be/9UDBYSgDeFs?si=0_Xk--mBdF0DXsON";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }
}