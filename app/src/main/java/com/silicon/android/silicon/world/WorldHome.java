package com.silicon.android.silicon.world;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;
import com.silicon.android.silicon.conversation.Calls;
import com.silicon.android.silicon.conversation.ConversationHome;
import com.silicon.android.silicon.conversation.Messages;
import com.silicon.android.silicon.conversation.MyPeople;
import com.silicon.android.silicon.signup.SignUp;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class WorldHome extends AppCompatActivity {
    SmoothBottomBar bottomBar;
    FrameLayout container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.aqua));
        //for full screen activity and visible status bar icon
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_world_home);
        container = findViewById(R.id.container);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new Home());
        transaction.commit();
        FirebaseAuth mAuth =  FirebaseAuth.getInstance();
        FirebaseUser currentUser= mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String PhoneNumber = snapshot.child("Users").child(AuthUserId).child("phoneNumber").getValue(String.class);
                String UserName = snapshot.child("Users").child(AuthUserId).child("userName").getValue(String.class);
                if (UserName == null ){
                    Toast.makeText(WorldHome.this, "Please fill the User name", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WorldHome.this, SignUp.class);
                    intent.putExtra("PhoneNumber", PhoneNumber);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WorldHome.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        });
        bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @SuppressLint("CommitTransaction")
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (i) {
                    case 0:
                        transaction.replace(R.id.container, new Home());
                        break;

                    case 1:
                        transaction.replace(R.id.container, new People());
                        break;

                    case 2:
                        transaction.replace(R.id.container, new Upload());
                        break;

                    case 3:
                        finish();
                        break;
                }
                transaction.commit(); // Commit the transaction after replacing the fragment
                return false;
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
