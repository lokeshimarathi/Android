package com.silicon.android.silicon.conversation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;
    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;

    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {

        MessagesList list2 = messagesLists.get(position);
        if (!list2.getUsersProfileImage().isEmpty()){
            Uri uri = Uri.parse(list2.getUsersProfileImage());
            Picasso.get().load(uri).into(holder.profileImage);

        }
        else {
            holder.profileImage.setImageResource(R.drawable.faceemoji);
        }
        holder.name.setText(list2.getName());

        holder.lastMessage.setText(list2.getLastMessage());
        holder.time.setText(list2.getGetLastMessageDate()+":"+list2.getGetLastMessageTime());

        if (list2.getUnseenMessages()== 0 ){
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));

        }
        else{
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(list2.getUnseenMessages()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.blue));

        }
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("usersAuthUserId",list2.getGetUserTwo());
                context.startActivity(intent);


            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<MessagesList> messagesLists){

        this.messagesLists = messagesLists;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profileImage;
        private final TextView name;
        private final TextView lastMessage;
        private final TextView unseenMessages;
        private final TextView time;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            profileImage = itemView.findViewById(R.id.profileImage);
            name= itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages= itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            time = itemView.findViewById(R.id.lastMessageTime);
        }
    }
}
