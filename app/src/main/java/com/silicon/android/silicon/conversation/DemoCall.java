package com.silicon.android.silicon.conversation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.silicon.android.silicon.R;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

public class DemoCall extends AppCompatActivity {
 EditText authUserId;
 Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_call);

        String UsersAuthUserId = getIntent().getStringExtra("UsersAuthUserId");
        String AuthUserId = getIntent().getStringExtra("AuthUserId");
        authUserId = findViewById(R.id.authUserId);
        login = findViewById(R.id.login);
        authUserId.setText(AuthUserId);
      login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              startmyservice(AuthUserId);
              Intent intent = new Intent(DemoCall.this, DemoProfile.class);
              intent.putExtra("UsersAuthUserId", UsersAuthUserId);
              intent.putExtra("AuthUserId", AuthUserId);
              startActivity(intent);
          }


      });
    }
    private void startmyservice(String userId) {
        Application application = getApplication(); // Android's application context
        long appID = 598683270;   // yourAppID
        String appSign ="e912cdc484be086d75ff5f87700df0070d9df7d4053e0acaa602791519c4e055";  // yourAppSign
        String userID =userId; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName =userID;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
        callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }
    public void onDestroy(){
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}