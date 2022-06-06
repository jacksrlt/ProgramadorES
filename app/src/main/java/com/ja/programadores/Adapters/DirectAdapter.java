package com.ja.programadores.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ja.programadores.Constructors.Direct;
import com.ja.programadores.R;
import com.ja.programadores.UserProfile;

import java.util.List;

public class DirectAdapter extends RecyclerView.Adapter<DirectAdapter.MyViewHolder> {

    Context mContext;
    List<Direct> mData;

    public DirectAdapter(Context mContext, List<Direct> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.direct_layout, parent, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.subjectTV.setText(mData.get(position).getSubject());
        holder.nameTV.setText(mData.get(position).getName());
        Glide.with(mContext).load(mData.get(position).getAvatar()).into(holder.avatarIV);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView subjectTV, nameTV;
        ImageView avatarIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTV = itemView.findViewById(R.id.subjectTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            avatarIV = itemView.findViewById(R.id.avatarIV);

        }
    }
}