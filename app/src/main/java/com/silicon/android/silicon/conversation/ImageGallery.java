package com.silicon.android.silicon.conversation;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.silicon.android.silicon.R;
import com.silicon.android.silicon.signup.ProfileCreating;
import com.silicon.android.silicon.signup.ProfileCropper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ImageGallery extends AppCompatActivity {
ActivityResultLauncher<String> mContent;
RoundedImageView image;
EditText caption;
ImageView sendButton;
static String chatKey;
static String AuthUserId;
static String UsersAuthUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_image_gallery);
image = findViewById(R.id.image);
caption = findViewById(R.id.caption);
sendButton = findViewById(R.id.sendButton);
chatKey= getIntent().getStringExtra("chatKey");
        AuthUserId= getIntent().getStringExtra("AuthUserId");
        UsersAuthUserId= getIntent().getStringExtra("UsersAuthUserId");
        String From = getIntent().getStringExtra("From");
        String Image = getIntent().getStringExtra("Image");

        if(Objects.equals(From, "ChatAdapter")){
            Uri imageUri = Uri.parse(Image);
            Picasso.get().load(imageUri).into(image);
            caption.setVisibility(View.INVISIBLE);
            sendButton.setVisibility(View.INVISIBLE);
        }

       else{
            mContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    // Handle the selected video here
                    Intent intent =new Intent(ImageGallery.this, ProfileCropper.class);
                    intent.putExtra("DATA", result.toString());
                    startActivityForResult(intent, 101);
                }
            });

            mContent.launch("image/*");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 &&  requestCode == 101) {
            assert data != null;
            String result = data.getStringExtra("RESULTP");
            final Uri[] resultUri = {null};
            if (result != null) {
                resultUri[0] = Uri.parse(result);
                Picasso.get().load(resultUri[0]).into(image);

                Uri imageUri = Uri.parse(result);
               String Caption= caption.getText().toString();

               sendButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       ProgressDialog pd = new ProgressDialog(ImageGallery.this);
                       pd.setCancelable(true);
                       pd.setTitle("Sending..");
                       pd.show();

                      FirebaseDatabase firebaseDatabase  = FirebaseDatabase.getInstance();
                     DatabaseReference databaseReference = firebaseDatabase.getReference();
                      FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                       StorageReference profileImagePath = firebaseStorage.getReference().child(chatKey + "/ media" ).child(imageUri.getLastPathSegment());
                       profileImagePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                               profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri mediaDownloadUri) {
                                       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                       SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                       String currentDate = simpleDateFormat.format(new Date());
                                       String currentTime = simpleTimeFormat.format(new Date());
                                       final String currentTimestamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                                       assert chatKey != null;
                                       databaseReference.child("Chats").child(chatKey).child("user_1").setValue(AuthUserId);
                                       databaseReference.child("Chats").child(chatKey).child("user_2").setValue(UsersAuthUserId);
                                       databaseReference.child("Chats").child(chatKey).child("lastMessage").setValue("Image");
                                       databaseReference.child("Chats").child(chatKey).child("lastMessageDeleteCondition").setValue("notDeleted");
                                       databaseReference.child("Chats").child(chatKey).child("lastMessageDate").setValue(currentDate);
                                       databaseReference.child("Chats").child(chatKey).child("lastMessageTime").setValue(currentTime);
                                       databaseReference.child("Chats").child(chatKey).child("lastMessageAuthUserId").setValue(AuthUserId);
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("msg").setValue("");
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("msgType").setValue("media");
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("mediaType").setValue("image");
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("caption").setValue(caption.getText().toString());
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("media").setValue(String.valueOf(mediaDownloadUri));
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("date").setValue(currentDate);
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("time").setValue(currentTime);
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("AuthUserId").setValue(AuthUserId);
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("condition").setValue("notRead");
                                       databaseReference.child("Chats").child(chatKey).child("messages").child(currentTimestamp).child("deleteCondition").setValue("notDeleted");
                                       Toast.makeText(ImageGallery.this, "Sent", Toast.LENGTH_SHORT).show();
                                       pd.dismiss();
                                       Intent intent = new Intent (getApplicationContext(), Chat.class);
                                       startActivity(intent);
                                   }
                               });
                           }
                       }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                               float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                               pd.setMessage("Please wait: " + (int) percent + "%");
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               pd.dismiss();
                               Toast.makeText(ImageGallery.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                           }
                       });                   }
               });

            }
            else {
                Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            }
        }


    }
}