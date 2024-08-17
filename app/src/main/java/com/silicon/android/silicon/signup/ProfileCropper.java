package com.silicon.android.silicon.signup;

import static com.yalantis.ucrop.UCrop.getError;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.silicon.android.silicon.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class ProfileCropper extends AppCompatActivity {

    String result;
    Uri fileUri;
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
        setContentView(R.layout.activity_profile_cropper);

        readIntent();


        String dest_uri=new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop.Options options = new UCrop.Options();
        UCrop.of(fileUri,Uri.fromFile(new File(getCacheDir(),dest_uri)))
                .withOptions(options)
                .withAspectRatio(1,1)
                .withMaxResultSize(9000,9000)
                .start(ProfileCropper.this);
    }
    private void readIntent() {
        Intent intent=getIntent();
        if(intent.getExtras()!=null)
        {
            result = intent.getStringExtra("DATA");
            fileUri = Uri.parse(result);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            assert data != null;
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULTP", resultUri+"");
            setResult(-1, returnIntent);
            finish();
        }
        else if (resultCode == UCrop.RESULT_ERROR) {
            assert data != null;
            final  Throwable cropError;
            cropError = getError(data);
        }

    }

}