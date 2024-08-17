package com.silicon.android.silicon.conversation;



import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {
    ImageView backButton, sendButton, gallery;
    ZegoSendCallInvitationButton  videoCall,voiceCall;
    CircleImageView profileImage;
    TextView name;
    EditText text;
    private final List<ChatList> chatLists = new ArrayList<>();
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;
    String usersProfileImage;
    String ProfileImage;
    // Assuming you have the values for 'read' and 'sent' variables
    boolean read = false; // Set to 'true' if the message is already read
    boolean sent = true; // Set to 'false' if the message is received from the opponent
    String username;
    String usersname;
  static String UsersAuthUserId;
  static String AuthUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.lightgray));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.lightgray));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_chat);
        chattingRecyclerView = findViewById(R.id.chatRecyclerView);

        UsersAuthUserId = getIntent().getStringExtra("usersAuthUserId");
        backButton = findViewById(R.id.backButton);
        videoCall = findViewById(R.id.videoCall);
        voiceCall = findViewById(R.id.voiceCall);
        sendButton = findViewById(R.id.sendButton);
        profileImage = findViewById(R.id.profileImage);
        name = findViewById(R.id.name);
        text = findViewById(R.id.text);
        gallery = findViewById(R.id.gallery);
        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));
        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView .setAdapter(chatAdapter);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        AuthUserId = user.getUid();
       gallery.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

             showMediaSelector();

           }
       });

        String chatKey = generateChatKey(AuthUserId, UsersAuthUserId);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username= snapshot.child(AuthUserId).child("userName").getValue(String.class);
                startmyservice(username);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usersProfileImage = snapshot.child(UsersAuthUserId).child("profileImage").getValue(String.class);
                String usersName = snapshot.child(UsersAuthUserId).child("name").getValue(String.class);
                usersname = snapshot.child(UsersAuthUserId).child("userName").getValue(String.class);

                Uri ProfileImageUri = Uri.parse(usersProfileImage);
                if (usersProfileImage!= null){
                    Picasso.get().load(ProfileImageUri).into(profileImage);
                }
                name.setText(usersName);
                startvoicecall(usersname,UsersAuthUserId,AuthUserId);
                startvideocall(usersname, UsersAuthUserId,AuthUserId);

                ProfileImage = snapshot.child(AuthUserId).child("profileImage").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chat.this, "Error! Please try again...", Toast.LENGTH_SHORT).show();
            }
        });



        assert chatKey != null;

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Chats")){
                    if(snapshot.child("Chats").child(chatKey).hasChild("messages") ){
                        chatLists.clear();
                        for (DataSnapshot messagesSnapshot : snapshot.child("Chats").child(chatKey).child("messages").getChildren()){

                            if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("AuthUserId")){
                                final String messageTimestamps = messagesSnapshot.getKey();

                                final String getAuthUserId = messagesSnapshot.child("AuthUserId").getValue(String.class);
                                final String getMsg = messagesSnapshot.child("msg").getValue(String.class);
                                final String getDate = messagesSnapshot.child("date").getValue(String.class);
                                final String getTime = messagesSnapshot.child("time").getValue(String.class);
                                final String getCondition = messagesSnapshot.child("condition").getValue(String.class);
                                final String getMsgType = messagesSnapshot.child("msgType").getValue(String.class);
                                final String getMediaType = messagesSnapshot.child("mediaType").getValue(String.class);
                                final String getMedia = messagesSnapshot.child("media").getValue(String.class);
                                final String getCaption = messagesSnapshot.child("caption").getValue(String.class);
                                final String getDeleteCondition = messagesSnapshot.child("deleteCondition").getValue(String.class);

                                ChatList chatList = new ChatList(getAuthUserId, AuthUserId,usersProfileImage,ProfileImage, getMsg,getDate, getTime,read,sent,getCondition,getMsgType,getMediaType,getCaption,getMedia,getDeleteCondition,chatKey, messageTimestamps);
                                chatLists.add(chatList);
                                loadingFirstTime = false;
                                chatAdapter.updateChatList(chatLists);
                                chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                if (Objects.equals(UsersAuthUserId, getAuthUserId)){
                                    databaseReference.child("Chats").child(chatKey).child("messages").child(messageTimestamps).child("condition").setValue("read");
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TextMessage =  text.getText().toString();
                if (!TextMessage.equals("")){
                    playNotificationSound();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                    String currentDate = simpleDateFormat.format(new Date());
                    String currentTime = simpleTimeFormat.format(new Date());
                    final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                    assert chatKey != null;
                    databaseReference.child("Chats").child(chatKey).child("user_1").setValue(AuthUserId);
                    databaseReference.child("Chats").child(chatKey).child("user_2").setValue(UsersAuthUserId);
                    databaseReference.child("Chats").child(chatKey).child("lastMessageKey").setValue(currentTimestamp);
                    databaseReference.child("Chats").child(chatKey).child("lastMessage").setValue(TextMessage);
                    databaseReference.child("Chats").child(chatKey).child("lastMessageDate").setValue(currentDate);
                    databaseReference.child("Chats").child(chatKey).child("lastMessageTime").setValue(currentTime);
                    databaseReference.child("Chats").child(chatKey).child("lastMessageAuthUserId").setValue(AuthUserId);
                    databaseReference.child("Chats").child(chatKey).child("lastMessageDeleteCondition").setValue("notDeleted");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue(TextMessage);
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("date").setValue(currentDate);
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("time").setValue(currentTime);
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("AuthUserId").setValue(AuthUserId);
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("condition").setValue("notRead");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("msgType").setValue("textMessage");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("mediaType").setValue("");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("caption").setValue("");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("media").setValue("");
                    databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("deleteCondition").setValue("notDeleted");

                    text.setText("");

                }
            }
        });
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    private String generateChatKey(String user1Id, String user2Id) {
        // concatenate user IDs and sort them alphabetically
        String concatenatedIds = user1Id.compareTo(user2Id) < 0 ? user1Id + user2Id : user2Id + user1Id;
        try {
            // hash the concatenated IDs using SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(concatenatedIds.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash); // convert the hashed bytes to hexadecimal string
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void playNotificationSound() {
        try {
            Uri notificationUri = Uri.parse("android.resource://" + getPackageName() + "/raw/chatnotification");
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), notificationUri);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void startmyservice(String username) {
        Application application = getApplication(); // Android's application context
        long appID = 598683270;   // yourAppID
        String appSign ="e912cdc484be086d75ff5f87700df0070d9df7d4053e0acaa602791519c4e055";  // yourAppSign
        String userID =username; // yourUserID, userID should only contain numbers, English characters, and '_'.
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
    public void startvideocall(String usersname, String UsersAuthUserId, String AuthUserId){
    /*String targetUserID = ; // The ID of the user you want to call.
    String targetUserName = ; // The username of the user you want to call.
    Context context = ; // Android context.
*/

        videoCall.setIsVideoCall(true);
        videoCall.setResourceID("zego_uikit_call");
        videoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(usersname,usersname)));
       /* FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
        String currentDate = simpleDateFormat.format(new Date());
        String currentTime = simpleTimeFormat.format(new Date());
         databaseReference.child("Users").child(AuthUserId).child("callHistory").child(currentDate+":"+currentTime).child("outGoingCall").child(UsersAuthUserId).child("type").setValue("videoCall");
         databaseReference.child("Users").child(UsersAuthUserId).child("callHistory").child(currentDate+":"+currentTime).child("incomingCall").child(AuthUserId).child("type").setValue("videoCall");
*/
    }
    public void startvoicecall(String usersname, String UsersAuthUserId, String AuthUserId){
    /*String targetUserID = ; // The ID of the user you want to call.
    String targetUserName = ; // The username of the user you want to call.
    Context context = ; // Android context.
*/
        voiceCall.setIsVideoCall(false);
        voiceCall.setResourceID("zego_uikit_call");
        voiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(usersname,usersname)));

       /* FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
        String currentDate = simpleDateFormat.format(new Date());
        String currentTime = simpleTimeFormat.format(new Date());
        databaseReference.child("Users").child(AuthUserId).child("callHistory").child(currentDate+":"+currentTime).child("outGoingCall").child(UsersAuthUserId).child("type").setValue("voiceCall");
        databaseReference.child("Users").child(UsersAuthUserId).child("callHistory").child(currentDate+":"+currentTime).child("incomingCall").child(AuthUserId).child("type").setValue("voiceCall");
*/
    }
    private ActivityResultLauncher<String> vContent;

    private void showMediaSelector() {
        final String[] media = {"Image", "Video"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Media")
                .setItems(media, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedMedia = media[which];
                        try {
                            if (Objects.equals(selectedMedia, "Image")) {
                              Intent intent = new Intent(getApplicationContext(),ImageGallery.class);
                                String chatKey = generateChatKey(AuthUserId, UsersAuthUserId);
                              intent.putExtra("chatKey",chatKey);
                                intent.putExtra("AuthUserId",AuthUserId);
                                intent.putExtra("UsersAuthUserId",UsersAuthUserId);
                              startActivity(intent);

                            } else if (Objects.equals(selectedMedia, "Video")) {

                                Intent intent = new Intent(getApplicationContext(),VideoGallery.class);
                                String chatKey = generateChatKey(AuthUserId, UsersAuthUserId);
                                intent.putExtra("chatKey",chatKey);
                                intent.putExtra("AuthUserId",AuthUserId);
                                intent.putExtra("UsersAuthUserId",UsersAuthUserId);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            Toast.makeText(Chat.this, "Error! Please try again..", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        builder.show();
    }


}