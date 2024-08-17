package com.silicon.android.silicon.conversation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.silicon.android.silicon.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MyPeopleAdapter extends RecyclerView.Adapter<MyPeopleAdapter.MyViewHolder> {
    private List<MyPeopleList> peopleLists;
    private Context context;

    public MyPeopleAdapter(List<MyPeopleList> peopleLists, Context context) {
        this.peopleLists = peopleLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_people_adapter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyPeopleList list = peopleLists.get(position);
        holder.name.setText(list.getName());

        if (list.getProfileImage() != null) {
            Uri profileImageUri = Uri.parse(list.getProfileImage());
            Picasso.get().load(profileImageUri).into(holder.profileImage);
        } else if (Objects.equals(list.getGender(), "male")) {
            holder.profileImage.setImageResource(R.drawable.faceemoji);
        } else if (Objects.equals(list.getGender(), "female")) {
            holder.profileImage.setImageResource(R.drawable.femalefaceemoji);
        } else {
            holder.profileImage.setImageResource(R.drawable.faceemoji);
        }

        holder.userName.setText(list.getUserName());

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   Intent intent = new Intent(context, Chat.class);
                    intent.putExtra("usersAuthUserId", list.getAuthUserId());
                    context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return peopleLists.size();
    }

    public void updateData(List<MyPeopleList> peopleLists) {
        this.peopleLists = peopleLists;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircularImageView profileImage;
        TextView name, userName;
        LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.name);
            userName = itemView.findViewById(R.id.userName);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }
    }
}
