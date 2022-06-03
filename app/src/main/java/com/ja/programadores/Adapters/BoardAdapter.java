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
import com.ja.programadores.Constructors.Board;
import com.ja.programadores.PostDetail;
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
        Glide.with(mContext).load(mData.get(position).getAvatar()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent boardDetail = new Intent(mContext, BoardDetail.class);
                    int position = getAdapterPosition();

                    boardDetail.putExtra("title",mData.get(position).getTitle());
                    boardDetail.putExtra("content",mData.get(position).getContent());
                    boardDetail.putExtra("location",mData.get(position).getLocation());
                    boardDetail.putExtra("postkey",mData.get(position).getPostKey());
                    boardDetail.putExtra("avatar",mData.get(position).getAvatar());
                    boardDetail.putExtra("name",mData.get(position).getName());
                    Timestamp timestamp  = (Timestamp) mData.get(position).getTimestamp();
                    boardDetail.putExtra("timestamp",timestamp) ;
                    mContext.startActivity(boardDetail);

                }
            });


        }
    }
}
