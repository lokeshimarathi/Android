package com.raitamitra.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SchemesAdapter extends RecyclerView.Adapter<SchemesAdapter.MyViewHolder> {

    private  List<SchemesList> schemesLists;
    private final Context context;

    public SchemesAdapter(List<SchemesList> schemesLists, Context context) {
        this.schemesLists = schemesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public SchemesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.scheme_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SchemesAdapter.MyViewHolder holder, int position) {

        SchemesList list2 = schemesLists.get(position);
        holder.schemeTextView.setText(list2.getScheme());
        holder.linkTextView.setText(list2.getLink());
        holder.incomeTextView.setText(list2.getIncome());
        holder.landTextView.setText(list2.getLand());
        holder.feedTextView.setText(list2.getFeed());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = list2.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
               context.startActivity(intent);


            }
        });
    }


    public void updateData(List<SchemesList> schemesLists){

        this.schemesLists = schemesLists;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return schemesLists.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView schemeTextView;
        TextView linkTextView;
        TextView incomeTextView;
        TextView landTextView;
        TextView feedTextView;
        LinearLayout rootLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            schemeTextView = itemView.findViewById(R.id.schemeTextView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
            incomeTextView = itemView.findViewById(R.id.incomeTextView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            landTextView = itemView.findViewById(R.id.landTextView);
            feedTextView = itemView.findViewById(R.id.feedTextView);

        }
    }
}