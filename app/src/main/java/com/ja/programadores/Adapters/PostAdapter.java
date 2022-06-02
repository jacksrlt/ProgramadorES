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

        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getImage()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getAvatar()).into(holder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetail = new Intent(mContext,PostDetail.class);
                    int position = getAdapterPosition();

                    postDetail.putExtra("title",mData.get(position).getTitle());
                    postDetail.putExtra("image",mData.get(position).getImage());
                    postDetail.putExtra("content",mData.get(position).getContent());
                    postDetail.putExtra("postkey",mData.get(position).getPostKey());
                    postDetail.putExtra("avatar",mData.get(position).getAvatar());
                    postDetail.putExtra("name",mData.get(position).getName());
                    Timestamp timestamp  = (Timestamp) mData.get(position).getTimestamp();
                    postDetail.putExtra("timestamp",timestamp) ;
                    mContext.startActivity(postDetail);

                }
            });


        }
    }
}
