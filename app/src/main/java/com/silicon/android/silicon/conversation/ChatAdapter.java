package com.silicon.android.silicon.conversation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private  List<ChatList> chatLists;
    private final Context context;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private String authUserId;

    {
        assert currentUser != null;
        authUserId = currentUser.getUid();
    }

    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;



    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout,null));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatList list2 = chatLists.get(position);
        if (list2.getUsersAuthUserId().equals(list2.getAuthUserId())) {
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppoLayout.setVisibility(View.GONE);

           if (Objects.equals(list2.getDeleteCondition(), "notDeleted")){
               if(Objects.equals(list2.getMsgType(), "textMessage")){
                   holder.myMessage.setVisibility(View.VISIBLE);
                   holder.myMessage.setText(" "+list2.getMsg()+" ");
               }
               else if(Objects.equals(list2.getMsgType(), "media")){
                   if(Objects.equals(list2.getMediaType(), "image")){
                       holder.Caption.setVisibility(View.VISIBLE);
                       holder.video.setVisibility(View.GONE);
                       holder.cardView.setVisibility(View.VISIBLE);
                       holder.image.setVisibility(View.VISIBLE);
                       Uri imageUri = Uri.parse(list2.getMedia());
                       Picasso.get().load(imageUri).into(holder.image);
                       holder.Caption.setText(" "+list2.getCaption()+" ");
                       holder.image.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Intent intent = new Intent(context.getApplicationContext(), ImageGallery.class);
                               intent.putExtra("From","ChatAdapter");
                               intent.putExtra("Image", list2.getMedia());
                               context.startActivity(intent);
                           }
                       });

                   }
                   else if(Objects.equals(list2.getMediaType(), "video")){
                       holder.Caption.setVisibility(View.VISIBLE);
                       holder.image.setVisibility(View.GONE);
                       holder.cardView.setVisibility(View.VISIBLE);
                       holder.video.setVisibility(View.VISIBLE);
                       Uri videoUri = Uri.parse(list2.getMedia());
                       holder.video.setVideoURI(videoUri);
                       // Mute the video
                       holder.video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                           @Override
                           public void onPrepared(MediaPlayer mp) {
                               mp.setVolume(0f, 0f); // Set volume to 0 (mute)
                           }
                       });
                       holder.Caption.setText(" "+list2.getCaption()+" ");
                       holder.video.start();
                       holder.video.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Intent intent = new Intent(context.getApplicationContext(), VideoGallery.class);
                               intent.putExtra("From","ChatAdapter");
                               intent.putExtra("Video", list2.getMedia());
                               context.startActivity(intent);
                           }
                       });

                   }
                   else{
                       Toast.makeText(context, "Please update your app", Toast.LENGTH_SHORT).show();
                   }
               }
               holder.myTime.setText(list2.getDate() + " : " + list2.getTime());
               if(list2.getProfileImage() != null){
                   Uri uri = Uri.parse(list2.getProfileImage());
                   Picasso.get().load(uri).into(holder.profileImage);
               }
               else{
                   holder.profileImage.setImageResource(R.drawable.faceemoji);
               }
               if (Objects.equals(list2.getCondition(), "read")){
                   holder.condition.setImageResource(R.drawable.ic_check);
               }

               Animation slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
               holder.myMessage.startAnimation(slideUpAnimation);
               holder.myMessage.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View view) {
                       final String[] media = {"Delete for everyone"};

                       AlertDialog.Builder builder = new AlertDialog.Builder(context);
                       builder.setTitle("Delete Message")
                               .setItems(media, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       String selectedMedia = media[which];
                                       try {
                                           if (Objects.equals(selectedMedia, "Delete for everyone")) {
                                               FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                               DatabaseReference databaseReference = firebaseDatabase.getReference();
                                               databaseReference.child("Chats").child(list2.getChatKey()).child("messages").child(list2.getMessageKey()).child("deleteCondition").setValue("deleted");
                                                databaseReference.child("Chats").child(list2.getChatKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                     @Override
                                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                         String lastMessageKey = snapshot.child("lastMessageKey").getValue(String.class);
                                                         if(Objects.equals(lastMessageKey, list2.messageKey)){
                                                             databaseReference.child("Chats").child(list2.getChatKey()).child("lastMessage").setValue("This message was deleted");
                                                             databaseReference.child("Chats").child(list2.getChatKey()).child("lastMessageDeleteCondition").setValue("deleted");
                                                         }
                                                     }

                                                     @Override
                                                     public void onCancelled(@NonNull DatabaseError error) {
                                                         Toast.makeText(context, "Error please try again!", Toast.LENGTH_SHORT).show();
                                                     }
                                                 });
                                           }
                                       } catch (Exception e) {
                                           Toast.makeText(context, "Error! Please try again..", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });

                       builder.show();
                       return false;
                   }
               });
               holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View view) {
                       final String[] media = {"Delete for everyone"};

                       AlertDialog.Builder builder = new AlertDialog.Builder(context);
                       builder.setTitle("Delete Message")
                               .setItems(media, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       String selectedMedia = media[which];
                                       try {
                                           if (Objects.equals(selectedMedia, "Delete for everyone")) {
                                               FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                               DatabaseReference databaseReference = firebaseDatabase.getReference();
                                               databaseReference.child("Chats").child(list2.getChatKey()).child("messages").child(list2.getMessageKey()).child("deleteCondition").setValue("deleted");

                                           }
                                       } catch (Exception e) {
                                           Toast.makeText(context, "Error! Please try again..", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });

                       builder.show();
                       return false;
                   }
               });
               holder.video.setOnLongClickListener(new View.OnLongClickListener() {
                   @Override
                   public boolean onLongClick(View view) {
                       final String[] media = {"Delete for everyone"};

                       AlertDialog.Builder builder = new AlertDialog.Builder(context);
                       builder.setTitle("Delete Message")
                               .setItems(media, new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       String selectedMedia = media[which];
                                       try {
                                           if (Objects.equals(selectedMedia, "Delete for everyone")) {
                                               FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                               DatabaseReference databaseReference = firebaseDatabase.getReference();
                                               databaseReference.child("Chats").child(list2.getChatKey()).child("messages").child(list2.getMessageKey()).child("deleteCondition").setValue("deleted");

                                           }
                                       } catch (Exception e) {
                                           Toast.makeText(context, "Error! Please try again..", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });

                       builder.show();
                       return false;
                   }
               });
           } else if (Objects.equals(list2.getDeleteCondition(), "deleted")) {
               holder.myMessage.setVisibility(View.VISIBLE);
               holder.myTime.setText(list2.getDate() + " : " + list2.getTime());
               if(list2.getProfileImage() != null){
                   Uri uri = Uri.parse(list2.getProfileImage());
                   Picasso.get().load(uri).into(holder.profileImage);
               }
               else{
                   holder.profileImage.setImageResource(R.drawable.faceemoji);
               }
               holder.myMessage.setText(" You deleted this message ");
               holder.myMessage.setTextColor(R.color.gray);
           }

        } else {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppoLayout.setVisibility(View.VISIBLE);
          if(Objects.equals(list2.getDeleteCondition(), "notDeleted")){
              if(Objects.equals(list2.getMsgType(), "textMessage")){
                  holder.oppoMessage.setVisibility(View.VISIBLE);
                  holder.oppoMessage.setText(" "+list2.getMsg()+" ");
              }
              else if(Objects.equals(list2.getMsgType(), "media")){
                  if(Objects.equals(list2.getMediaType(), "image")){
                      holder.oppoCaption.setVisibility(View.VISIBLE);
                      holder.oppoVideo.setVisibility(View.GONE);
                      holder.oppoCardView.setVisibility(View.VISIBLE);
                      holder.oppoImage.setVisibility(View.VISIBLE);
                      Uri imageUri = Uri.parse(list2.getMedia());
                      Picasso.get().load(imageUri).into(holder.oppoImage);

                      holder.oppoCaption.setText(" "+list2.getCaption()+" ");
                      holder.oppoImage.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              Intent intent = new Intent(context, ImageGallery.class);
                              intent.putExtra("From","ChatAdapter");
                              intent.putExtra("Image", list2.getMedia());
                              context.startActivity(intent);
                          }
                      });
                  }
                  else if(Objects.equals(list2.getMediaType(), "video")){
                      holder.oppoCaption.setVisibility(View.VISIBLE);
                      holder.oppoImage.setVisibility(View.GONE);
                      holder.oppoCardView.setVisibility(View.VISIBLE);
                      holder.oppoVideo.setVisibility(View.VISIBLE);
                      Uri videoUri = Uri.parse(list2.getMedia());
                      holder.oppoVideo.setVideoURI(videoUri);
                      // Mute the video
                      holder.oppoVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                          @Override
                          public void onPrepared(MediaPlayer mp) {
                              mp.setVolume(0f, 0f); // Set volume to 0 (mute)
                          }
                      });

                      holder.oppoVideo.start();
                      holder.oppoCaption.setText(" "+list2.getCaption()+" ");
                      holder.oppoVideo.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              Intent intent = new Intent(context, VideoGallery.class);
                              intent.putExtra("From","ChatAdapter");
                              intent.putExtra("Video", list2.getMedia());
                              context.startActivity(intent);
                          }
                      });
                  }
                  else{
                      Toast.makeText(context, "Please update your app", Toast.LENGTH_SHORT).show();
                  }
              }
              holder.oppoTime.setText(list2.getDate() + " : " + list2.getTime());
             if(list2.usersProfileImage !=null){
                 Uri uri = Uri.parse(list2.getUsersProfileImage());
                 Picasso.get().load(uri).into(holder.usersProfileImage);
             }
             else{
                 holder.usersProfileImage.setImageResource(R.drawable.faceemoji);
             }
              // Check if the date is today and the time is the current time
              SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
              SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
              String currentDate = simpleDateFormat.format(new Date());
              String currentTime = simpleTimeFormat.format(new Date());// Get the current time in the desired format
              if ((!list2.isRead() || list2.isSent()) && list2.getDate().equals(currentDate) && list2.getTime().equals(currentTime)) {
                  playChatNotificationMusic();
              }
          }
          else if(Objects.equals(list2.getDeleteCondition(), "deleted")) {
              holder.oppoTime.setText(list2.getDate() + " : " + list2.getTime());
              holder.oppoMessage.setVisibility(View.VISIBLE);
              holder.oppoMessage.setText(" This message was deleted ");
              holder.oppoMessage.setTextColor(R.color.gray);
              if(list2.usersProfileImage !=null){
                  Uri uri = Uri.parse(list2.getUsersProfileImage());
                  Picasso.get().load(uri).into(holder.usersProfileImage);
              }
              else{
                  holder.usersProfileImage.setImageResource(R.drawable.faceemoji);
              }
          }
        }
    }

    private void playChatNotificationMusic() {
        try {
            Uri musicUri = Uri.parse("android.resource://" + context.getPackageName() + "/raw/chatnotification");
            MediaPlayer mediaPlayer = MediaPlayer.create(context, musicUri);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void deleteCondition(){

    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists){
        this.chatLists = chatLists;

    }
    static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout oppoLayout, myLayout;
        private TextView oppoMessage, myMessage;
        private TextView oppoTime, myTime,oppoCaption,Caption;
        CircleImageView usersProfileImage, profileImage;
        ImageView condition,oppoImage, image;
        VideoView oppoVideo,video;
        CardView cardView,oppoCardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            oppoLayout =itemView.findViewById(R.id.oppoLayout);
            myLayout =itemView.findViewById(R.id.myLayout);
            oppoMessage =itemView.findViewById(R.id.oppoMessage);
            myMessage =itemView.findViewById(R.id.myMessage);
            oppoTime =itemView.findViewById(R.id.oppoMsgTime);
            myTime =itemView.findViewById(R.id.myMsgTime);
            usersProfileImage = itemView.findViewById(R.id.usersProfileImage);
            profileImage =  itemView.findViewById(R.id.profileImage);
            condition = itemView.findViewById(R.id.condition);
            oppoCaption = itemView.findViewById(R.id.oppoCaption);
            Caption = itemView.findViewById(R.id.Caption);
            oppoImage = itemView.findViewById(R.id.oppoImage);
            image = itemView.findViewById(R.id.image);
            oppoVideo = itemView.findViewById(R.id.oppoVideo);
            video = itemView.findViewById(R.id.video);
            cardView = itemView.findViewById(R.id.cardView);
            oppoCardView = itemView.findViewById(R.id.oppoCardView);

        }
    }
}
