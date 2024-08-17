package com.silicon.android.silicon.conversation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.silicon.android.silicon.R;

public class ChatGallerySelection extends AppCompatActivity {
  ActivityResultLauncher<String> mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gallery_selection);
       String media = getIntent().getStringExtra("Media");

    }
}