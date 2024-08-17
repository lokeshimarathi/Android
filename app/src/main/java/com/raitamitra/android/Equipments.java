package com.raitamitra.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Equipments extends AppCompatActivity {
 LinearLayout tractor, tiller, cutting, spray, milk, baler, thresher,supari;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        requestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        //for full screen activity and transparent status bar
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_equipments);
        tractor = findViewById(R.id.tractor);
        tiller = findViewById(R.id.tiller);
        cutting = findViewById(R.id.cutting);
        spray = findViewById(R.id.spray);
        milk = findViewById(R.id.milk);
        baler = findViewById(R.id.baler);
        thresher = findViewById(R.id.thresher);
        supari = findViewById(R.id.supari);
        tractor.setOnClickListener(view -> gotoUrl());
        tiller.setOnClickListener(view -> gotoUrl());
        cutting.setOnClickListener(view -> gotoUrl());
        spray.setOnClickListener(view -> gotoUrl());
        milk.setOnClickListener(view -> gotoUrl());
        baler.setOnClickListener(view -> gotoUrl());
        thresher.setOnClickListener(view -> gotoUrl());
        supari.setOnClickListener(view -> gotoUrl());


    }
    private void gotoUrl() {
        Uri uri = Uri.parse("tel:+919686356444");
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }
}