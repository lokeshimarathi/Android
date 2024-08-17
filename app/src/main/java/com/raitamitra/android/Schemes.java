package com.raitamitra.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Schemes extends AppCompatActivity {
    private final List<SchemesList> schemesLists = new ArrayList<>();

    private RecyclerView schemesRecyclerView;
    private SchemesAdapter schemesAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    TextView available;
    int userIncome;

    private boolean dataSet = false;
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
        setContentView(R.layout.activity_schemes);
        String AuthUserId = currentUser.getUid();
        schemesRecyclerView = findViewById(R.id.schemesRecyclerView);
        schemesRecyclerView.setHasFixedSize(true);
        schemesRecyclerView.setLayoutManager(new LinearLayoutManager(Schemes.this));
        available = findViewById(R.id.available);

        //set Adapter to  recycler view
        schemesAdapter = new SchemesAdapter(schemesLists, Schemes.this);
        schemesRecyclerView.setAdapter( schemesAdapter);

        ProgressDialog pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
        pd.show();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                schemesLists.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String getKey = dataSnapshot.getKey();
                    dataSet = false;


                    if(AuthUserId.equals(getKey)){
                        Integer userIncome = dataSnapshot.child("income").getValue(Integer.class);

                        databaseReference.child("Schemes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                    final String schemesKey = dataSnapshot1.getKey();
                                    assert schemesKey != null;
                                    Integer schemeIncome = dataSnapshot1.child("Income").getValue(Integer.class);
                                    if(userIncome<=schemeIncome){
                                        String getLink = dataSnapshot1.child("Link").getValue(String.class);
                                        String getScheme = dataSnapshot1.child("Scheme").getValue(String.class);
                                        String getFeed = dataSnapshot1.child("Feed").getValue(String.class);
                                        String getLand = dataSnapshot1.child("Land").getValue(String.class);

                                        if (!dataSet) {
                                            dataSet = true;

                                        }

                                        SchemesList schemeList = new SchemesList(getScheme, getLink,String.valueOf(schemeIncome), getFeed, getLand);
                                        schemesLists.add(schemeList);
                                        schemesAdapter.updateData(schemesLists);
                                        pd.dismiss();



                                    }
                                    else{
                                        pd.dismiss();
                                        setContentView(R.layout.sad_layout);
                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });

    }
}