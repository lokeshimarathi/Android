package com.silicon.android.silicon.conversation;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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
import com.mikhaellopez.circularimageview.CircularImageView;

import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Messages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Messages extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final List<MessagesList> messagesLists = new ArrayList<>();

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;

    CircularImageView profileImage;
    private int unseenMessages = 0;
    private String chatKey = "";
    private boolean dataSet = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Messages() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Messages.
     */
    // TODO: Rename and change types and number of parameters
    public static Messages newInstance(String param1, String param2) {
        Messages fragment = new Messages();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);


        profileImage =view.findViewById(R.id.profileImage);
        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        //set Adapter to  recycler view
        messagesAdapter = new MessagesAdapter(messagesLists, view.getContext());
        messagesRecyclerView.setAdapter( messagesAdapter);
        ProgressDialog pd = new ProgressDialog(view.getContext());
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String AuthUserId = currentUser.getUid();
        int userUnseenMessages;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ProfileImage = snapshot.child("Users").child(AuthUserId).child("profileImage").getValue(String.class);
                String username = snapshot.child("Users").child(AuthUserId).child("userName").getValue(String.class);
                if (ProfileImage != null){
                    Uri profileImageUri = Uri.parse(ProfileImage);
                    Picasso.get().load(profileImageUri).into(profileImage);
                    pd.dismiss();
                }
                else {
                    profileImage.setImageResource(R.drawable.faceemoji);
                    pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(view.getContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), ProfileSettings.class));
            }
        });


        dataSet = false;
        databaseReference.child("Chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();

                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    final String getKey = dataSnapshot1.getKey();
                    chatKey = getKey;
                    assert chatKey != null;
                    if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")) {
                        final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                        final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);
                        final String getLastMessage = dataSnapshot1.child("lastMessage").getValue(String.class);
                        final String getLastMessageDate = dataSnapshot1.child("lastMessageDate").getValue(String.class);
                        final String getLastMessageTime = dataSnapshot1.child("lastMessageTime").getValue(String.class);

                        if (Objects.equals(getUserOne, AuthUserId) || Objects.equals(getUserTwo, AuthUserId)) {
                            int userUnseenMessages = 0;

                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.child("messages").getChildren()) {
                                String msgKey = dataSnapshot2.getKey();
                                String conditionAuthUserId = dataSnapshot2.child("AuthUserId").getValue(String.class);

                                if (!Objects.equals(conditionAuthUserId, AuthUserId)) {
                                    String condition = dataSnapshot2.child("condition").getValue(String.class);
                                    if (Objects.equals(condition, "notRead")) {
                                        userUnseenMessages++;
                                    }
                                }
                            }

                            if (Objects.equals(getUserOne, AuthUserId)) {
                                int finalUserUnseenMessages1 = userUnseenMessages;
                                databaseReference.child("Users").child(getUserTwo).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String usersProfileImage = snapshot.child("profileImage").getValue(String.class);
                                        String name = snapshot.child("name").getValue(String.class);

                                        if (!dataSet) {
                                            dataSet = true;
                                        }

                                        MessagesList messagesList = new MessagesList(getUserTwo, name, getLastMessage, usersProfileImage, getLastMessageDate, getLastMessageTime, "", finalUserUnseenMessages1);
                                        messagesLists.add(messagesList);
                                        messagesAdapter.updateData(messagesLists);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }

                            if (Objects.equals(getUserTwo, AuthUserId)) {
                                int finalUserUnseenMessages = userUnseenMessages;
                                databaseReference.child("Users").child(getUserOne).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String usersProfileImage = snapshot.child("profileImage").getValue(String.class);
                                        String name = snapshot.child("name").getValue(String.class);

                                        if (!dataSet) {
                                            dataSet = true;
                                        }

                                       MessagesList messagesList = new MessagesList(getUserOne, name, getLastMessage, usersProfileImage, getLastMessageDate, getLastMessageTime, "", finalUserUnseenMessages);
                                        messagesLists.add(messagesList);
                                        messagesAdapter.updateData(messagesLists);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        return view;

    }

}