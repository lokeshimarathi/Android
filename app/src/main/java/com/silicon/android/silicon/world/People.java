package com.silicon.android.silicon.world;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.silicon.android.silicon.world.people.PeopleAdapter;
import com.silicon.android.silicon.world.people.PeopleList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link People#newInstance} factory method to
 * create an instance of this fragment.
 */
public class People extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView peopleRecyclerView;
    private List<PeopleList> peopleLists = new ArrayList<>();
    private PeopleAdapter peopleAdapter;
    private boolean dataSet = false;
    private String mParam1;
    private String mParam2;

    public People() {
        // Required empty public constructor
    }

    public static People newInstance(String param1, String param2) {
        People fragment = new People();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        peopleRecyclerView = view.findViewById(R.id.peopleRecyclerView);
        peopleRecyclerView.setHasFixedSize(true);
        peopleRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        peopleAdapter = new PeopleAdapter(peopleLists, view.getContext());
        peopleRecyclerView.setAdapter(peopleAdapter);

        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setMessage("Please wait..");
        pd.setCancelable(false);
        pd.show();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String authUserId = user.getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                peopleLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String usersAuthUserId = dataSnapshot.getKey();
                    dataSet = false;
                    if (!Objects.equals(usersAuthUserId, authUserId)) {
                        String getProfileImage = dataSnapshot.child("profileImage").getValue(String.class);
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String getUserName = dataSnapshot.child("userName").getValue(String.class);
                        String getGender = dataSnapshot.child("gender").getValue(String.class);
                        if (!dataSet) {
                            dataSet = true;


                        }
                        PeopleList peopleList = new PeopleList(getProfileImage, getName, getUserName, getGender, usersAuthUserId);
                        peopleLists.add(peopleList);
                        peopleAdapter.updateData(peopleLists);
                        // Reverse the order of the postList
                        Collections.reverse(peopleLists);
                        peopleAdapter.notifyDataSetChanged();
                    }
                }

                peopleAdapter.updateData(peopleLists);
                pd.dismiss();

                // Handle the case when no data is available or an empty list is retrieved
                if (peopleLists.isEmpty()) {
                    // Show a message or handle this situation as needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(view.getContext(), "Error! Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
