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
import com.ja.programadores.BoardDetail;
import com.ja.programadores.POJO.Board;
import com.ja.programadores.OpProfile;
import com.ja.programadores.R;

import java.util.List;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.MyViewHolder> {

    Context mContext;
    List<Board> mData;

    public BoardAdapter(Context mContext, List<Board> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.board_layout, parent, false);

        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvName.setText(mData.get(position).getName());
        holder.tvLocation.setText(mData.get(position).getLocation());
        Glide.with(mContext).load(mData.get(position).getAvatar()).circleCrop().into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvLocation, tvName;
        ImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.boardTitleTV);
            tvName = itemView.findViewById(R.id.boardNameTV);
            tvLocation = itemView.findViewById(R.id.boardLocationTV);
            imgPostProfile = itemView.findViewById(R.id.boardAvatarIV);

            imgPostProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, OpProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getPosteruid());
                    mContext.startActivity(userDetail);
                }
            });

            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, OpProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getPosteruid());
                    mContext.startActivity(userDetail);
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent boardDetail = new Intent(mContext, BoardDetail.class);
                    int position = getAdapterPosition();

                    boardDetail.putExtra("title", mData.get(position).getTitle());
                    boardDetail.putExtra("content", mData.get(position).getContent());
                    boardDetail.putExtra("location", mData.get(position).getLocation());
                    boardDetail.putExtra("boardkey", mData.get(position).getBoardkey());
                    boardDetail.putExtra("avatar", mData.get(position).getAvatar());
                    boardDetail.putExtra("name", mData.get(position).getName());
                    Timestamp timestamp = (Timestamp) mData.get(position).getTimestamp();
                    boardDetail.putExtra("timestamp", timestamp);
                    mContext.startActivity(boardDetail);

                }
            });


        }
    }
}
