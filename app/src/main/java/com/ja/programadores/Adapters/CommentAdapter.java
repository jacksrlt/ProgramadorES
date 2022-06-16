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
import com.ja.programadores.POJO.Comment;
import com.ja.programadores.R;
import com.ja.programadores.UserProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;


    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        Glide.with(mContext).load(mData.get(position).getAvatar()).circleCrop().into(holder.img_user);
        holder.tv_name.setText(mData.get(position).getName());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(dateToString((Timestamp) mData.get(position).getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView img_user;
        TextView tv_name, tv_content, tv_date;

        public CommentViewHolder(View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.userAvatarIV);
            tv_name = itemView.findViewById(R.id.commentNameTV);
            tv_content = itemView.findViewById(R.id.commentContentTV);
            tv_date = itemView.findViewById(R.id.commentDateTV);

            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, UserProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getUseruid());
                    mContext.startActivity(userDetail);
                }
            });

            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent userDetail = new Intent(mContext, UserProfile.class);
                    int position = getAdapterPosition();
                    userDetail.putExtra("useruid", mData.get(position).getUseruid());
                    mContext.startActivity(userDetail);
                }
            });


        }
    }


    private String dateToString(Timestamp timestamp) {

        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String txtDate = dateFormat.format(date);
        return txtDate;

    }


}
