package com.ja.programadores.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ja.programadores.Constructors.Post;
import com.ja.programadores.R;

import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.MyViewHolder> {

    public List<Post> post;
    private RelativeLayout linearLayout;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView user, content;
        ImageView avatar;
        public MyViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.name);
            content = view.findViewById(R.id.message);
            avatar = view.findViewById(R.id.avatar);
            linearLayout = view.findViewById(R.id.messageCard);
        }
    }

    public AdapterHome(Context context, List<Post> post) {
        this.post = post;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_post, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Post recycleList = post.get(position);
        holder.user.setText(recycleList.getUseruid());
        holder.content.setText(recycleList.getMessage());
        Glide.with(context)
                .load(recycleList.getImage())
                .circleCrop()
                .into(holder.avatar);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post recycleList = post.get(position);
                String user = recycleList.getUseruid();
                Toast.makeText(context, "User Name : " + user, Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public int getItemCount() {
        return post.size();
    }

}
