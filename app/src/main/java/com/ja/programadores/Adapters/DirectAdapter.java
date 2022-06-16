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
import com.google.firebase.Timestamp;
import com.ja.programadores.DirectDetail;
import com.ja.programadores.POJO.Direct;
import com.ja.programadores.PostDetail;
import com.ja.programadores.R;

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

        holder.nameTV.setText(mData.get(position).getName());
        holder.subjectTV.setText(mData.get(position).getSubject());
        Glide.with(mContext).load(mData.get(position).getAvatar()).circleCrop().into(holder.avatarIV);

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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent directDetail = new Intent(mContext, DirectDetail.class);
                    int position = getAdapterPosition();

                    directDetail.putExtra("subject", mData.get(position).getSubject());
                    directDetail.putExtra("senderuid", mData.get(position).getSenderuid());
                    directDetail.putExtra("image", mData.get(position).getImage());
                    directDetail.putExtra("message", mData.get(position).getMessage());
                    directDetail.putExtra("directkey", mData.get(position).getDirectkey());
                    directDetail.putExtra("avatar", mData.get(position).getAvatar());
                    directDetail.putExtra("name", mData.get(position).getName());
                    Timestamp timestamp = (Timestamp) mData.get(position).getTimestamp();
                    directDetail.putExtra("timestamp", timestamp);
                    mContext.startActivity(directDetail);

                }
            });

        }
    }
}