package com.silicon.android.silicon.conversation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.silicon.android.silicon.R;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class DemoProfile extends AppCompatActivity {
    EditText usersAthUserId;
    Button next;
ZegoSendCallInvitationButton  callbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_profile);
        String UsersAuthUserId = getIntent().getStringExtra("UsersAuthUserId");
        String AuthUserId = getIntent().getStringExtra("AuthUserId");
        usersAthUserId = findViewById(R.id.usersAuthUserId);
        next = findViewById(R.id.next);
        callbtn = findViewById(R.id.callbtn);
        usersAthUserId.setText(UsersAuthUserId);
            startvideocall(UsersAuthUserId);
    }
public void startvideocall(String usersAuthUserId){
    /*String targetUserID = ; // The ID of the user you want to call.
    String targetUserName = ; // The username of the user you want to call.
    Context context = ; // Android context.
*/
    callbtn.setIsVideoCall(true);
    callbtn.setResourceID("zego_uikit_call");
    callbtn.setInvitees(Collections.singletonList(new ZegoUIKitUser(usersAuthUserId,usersAuthUserId)));
}

}