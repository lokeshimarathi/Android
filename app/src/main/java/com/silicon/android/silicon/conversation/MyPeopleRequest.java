package com.silicon.android.silicon.conversation;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPeople#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPeopleRequest extends Fragment {

    RecyclerView myPeopleRecyclerView;
    private MyPeopleRequestAdapter myPeopleAdapter;
    private String getKey;
    private final List<MyPeopleRequestList> myPeopleLists = new ArrayList<>();

    public MyPeopleRequest() {
        // Required empty public constructor
    }
    public static MyPeopleRequest newInstance() {
        return new MyPeopleRequest();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_people_request, container, false);
        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setCancelable(false);
        pd.setMessage("Please wait...");


        myPeopleRecyclerView = view.findViewById(R.id.myPeopleRecyclerView);
        myPeopleRecyclerView.setHasFixedSize(true);
        myPeopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        myPeopleAdapter = new MyPeopleRequestAdapter(myPeopleLists, getContext());
        myPeopleRecyclerView.setAdapter(myPeopleAdapter);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String AuthUserId = user.getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Users").child(AuthUserId).child("connection").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String getKey = snapshot1.getKey();
                    String getCondition = snapshot1.child("condition").getValue(String.class);

                    if (getKey != null && Objects.equals(getCondition, "requested")) {
                        pd.show();
                        databaseReference.child("Users").child(getKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String getUserProfileImage = snapshot.child("profileImage").getValue(String.class);
                                String getUserName = snapshot.child("userName").getValue(String.class);
                                String getName = snapshot.child("name").getValue(String.class);
                                String getGender = snapshot.child("gender").getValue(String.class);

                                MyPeopleRequestList myPeopleRequestList = new MyPeopleRequestList(getUserProfileImage, getUserName, getName, getGender, getKey);
                                myPeopleLists.add(myPeopleRequestList);
                                myPeopleAdapter.notifyDataSetChanged();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                pd.dismiss();
                                Toast.makeText(view.getContext(), "Error! Please try again..", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(view.getContext(), "Error! Please try again..", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}