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
import com.ja.programadores.Constructors.Post;
import com.ja.programadores.PostDetail;
import com.ja.programadores.R;
import com.ja.programadores.UserProfile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.titleTV.setText(mData.get(position).getTitle());
        holder.nameTV.setText(mData.get(position).getName());
        Glide.with(mContext).load(mData.get(position).getImage()).into(holder.imageIV);
        Glide.with(mContext).load(mData.get(position).getAvatar()).circleCrop().into(holder.avatarIV);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV, nameTV;
        ImageView imageIV, avatarIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.boardTitleTV);
            nameTV = itemView.findViewById(R.id.postNameTV);
            imageIV = itemView.findViewById(R.id.postImageIV);
            avatarIV = itemView.findViewById(R.id.boardAvatarIV);

            avatarIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, UserProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getPosteruid());
                    mContext.startActivity(userDetail);
                }
            });

            nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, UserProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getPosteruid());
                    mContext.startActivity(userDetail);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetail = new Intent(mContext, PostDetail.class);
                    int position = getAdapterPosition();

                    postDetail.putExtra("title", mData.get(position).getTitle());
                    postDetail.putExtra("image", mData.get(position).getImage());
                    postDetail.putExtra("content", mData.get(position).getContent());
                    postDetail.putExtra("postkey", mData.get(position).getPostkey());
                    postDetail.putExtra("avatar", mData.get(position).getAvatar());
                    postDetail.putExtra("name", mData.get(position).getName());
                    Timestamp timestamp = (Timestamp) mData.get(position).getTimestamp();
                    postDetail.putExtra("timestamp", timestamp);
                    mContext.startActivity(postDetail);

                }
            });


        }
    }

}
