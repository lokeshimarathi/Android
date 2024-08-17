package com.raitamitra.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class DocumentUpload extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private Button uploadButton, saveButton;
    private ImageView aadharImage;
    private Uri selectedImageUri;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // for full screen activity
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.aquablue));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aquablue));
        //for full screen activity and transparent status bar
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_document_upload);

        Intent i = getIntent();
        String Phone = i.getStringExtra("Phone");

        String Name = i.getStringExtra("Name");
        String DateOfBirth = i.getStringExtra("DateOfBirth");
        String Gender = i.getStringExtra("Gender");
        String Address = i.getStringExtra("Address");
        String AadharNo = i.getStringExtra("AadharNo");
        String PanNo = i.getStringExtra("PanNo");
        String KisanNo = i.getStringExtra("KisanNo");
        String Land = i.getStringExtra("Land");
        String Income = i.getStringExtra("Income");
        String Feed = i.getStringExtra("Feed");
        String ProfileImage = i.getStringExtra("ProfileImage");
        Uri ProfileImageUri = Uri.parse(ProfileImage);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        assert firebaseUser != null;
        String AuthUserId = firebaseUser.getUid();

        uploadButton = findViewById(R.id.uploadButton);
        saveButton = findViewById(R.id.saveButton);
        aadharImage = findViewById(R.id.aadharImage);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    ProgressDialog pd = new ProgressDialog(DocumentUpload.this);
                    pd.setTitle("Please wait..");
                    pd.setCancelable(false);
                    pd.show();

                    storage = FirebaseStorage.getInstance();
                    StorageReference profileImagePath = storage.getReference().child(AuthUserId + "/ProfileImage").child(ProfileImageUri.getLastPathSegment());
                    profileImagePath.putFile(ProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileImagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri profileImageDownloadUri) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference();
                                    PersonalDataHolder obj = new PersonalDataHolder(Phone, Name, DateOfBirth, Gender, Address, AadharNo, PanNo, KisanNo, Land, Feed, null, profileImageDownloadUri.toString(), "");
                                    databaseReference.child("Users").child(AuthUserId).setValue(obj);
                                    int income = Integer.parseInt(Income);
                                    databaseReference.child("Users").child(AuthUserId).child("income").setValue(income);
                                    StorageReference aadharCardPath = storage.getReference().child(AuthUserId + "/AadharCard").child(selectedImageUri.getLastPathSegment());
                                    aadharCardPath.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            aadharCardPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri aadharCardDownloadUri) {
                                                    obj.setAadharCard(aadharCardDownloadUri.toString());
                                                    databaseReference.child("Users").child(AuthUserId).setValue(obj);
                                                    int income = Integer.parseInt(Income);
                                                    databaseReference.child("Users").child(AuthUserId).child("income").setValue(income);
                                                    pd.dismiss();
                                                    Toast.makeText(DocumentUpload.this, "Account created successfully!", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(DocumentUpload.this, Home.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(DocumentUpload.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            pd.setMessage("Registering: " + (int) percent + "%");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(DocumentUpload.this, "Error! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(DocumentUpload.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showOptionsDialog() {
        String[] options = {"Camera", "Gallery"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Choose Option");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Camera
                    openCamera();
                    break;
                case 1: // Gallery
                    openGallery();
                    break;
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "Camera app not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle extras = data.getExtras();
                if (extras != null && extras.containsKey("data")) {
                    Bitmap photo = (Bitmap) extras.get("data");
                    if (photo != null) {
                        aadharImage.setImageBitmap(photo);
                        saveButton.setVisibility(View.VISIBLE);
                        saveButton.setEnabled(true);
                        selectedImageUri = bitmapToUri(photo);
                    }
                }
            } else if (requestCode == REQUEST_GALLERY) {
                selectedImageUri = data.getData();
                aadharImage.setImageURI(selectedImageUri);
                saveButton.setVisibility(View.VISIBLE);
                saveButton.setEnabled(true);
            }
        }
    }

    private Uri bitmapToUri(Bitmap bitmap) {
        // Get the external cache directory
        File cacheDir = getExternalCacheDir();
        File tempFile = new File(cacheDir, "temp_image.jpg");

        try {
            // Compress the bitmap and write it to the temporary file
            OutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Return the URI for the temporary file
        return Uri.fromFile(tempFile);
    }
}
