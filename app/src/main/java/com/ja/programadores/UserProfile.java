package com.ja.programadores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ja.programadores.Adapters.PostAdapter;
import com.ja.programadores.POJO.Post;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends AppCompatActivity {

    ImageView profileIv;
    TextView nameTv;
    TextView bioTv;
    TextView githubTv;
    TextView linkedinTv;
    private FirebaseFirestore fStore;
    RecyclerView userPostRecycler;
    CollectionReference collectionReferenceUsers;
    CollectionReference collectionReferencePosts;
    List<Post> userPostList;
    Button sendDirect;
    PostAdapter userPostAdapter;
    ProgressBar progressBar;
    String useruid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fStore = FirebaseFirestore.getInstance();
        sendDirect = findViewById(R.id.directBt);
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        bioTv = findViewById(R.id.bioTv);
        githubTv = findViewById(R.id.githubTv);
        linkedinTv = findViewById(R.id.linkedinTv);
        userPostRecycler = findViewById(R.id.userPostRecycler);
        collectionReferenceUsers = fStore.collection("Users");
        userPostRecycler.setVisibility(View.INVISIBLE);
        userPostRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        collectionReferencePosts = fStore.collection("Posts");
        userPostList = new ArrayList<>();
        userPostAdapter = new PostAdapter(getApplicationContext(), userPostList);
        userPostRecycler.setAdapter(userPostAdapter);
        userPostAdapter.notifyDataSetChanged();
        progressBar = findViewById(R.id.progressBar);

        useruid = getIntent().getExtras().getString("useruid");

        sendDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createDirect = new Intent(UserProfile.this, CreateDirect.class);
                createDirect.putExtra("recuid", useruid);
                startActivity(createDirect);
            }
        });

        showProfile();
        loadPosts();

    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);
        Query postQuery = collectionReferencePosts.whereEqualTo("useruid", useruid).orderBy("timestamp", Query.Direction.DESCENDING);
        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Post post = new Post();
                    post.setTitle(document.getString("title"));
                    post.setContent(document.getString("content"));
                    post.setImage(document.getString("image"));
                    post.setPostkey(document.getId());
                    post.setTimestamp(document.get("timestamp"));
                    String posterUid = document.getString("useruid");
                    post.setPosteruid(posterUid);
                    DocumentReference userRef = collectionReferenceUsers.document(posterUid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            post.setName(documentSnapshot.getString("name"));
                            post.setAvatar(documentSnapshot.getString("image"));
                            userPostList.add(post);
                            userPostAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                            userPostRecycler.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void showProfile() {

        DocumentReference docRef = fStore.collection("Users").document(useruid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nameTv.setText(document.getString("name").toString());
                        bioTv.setText(document.getString("bio").toString());
                        linkedinTv.setText(document.getString("linkedin").toString());
                        githubTv.setText(document.getString("github").toString());
                        Glide.with(getApplicationContext())
                                .load(document.getString("image").toString())
                                .circleCrop()
                                .into(profileIv);
                    }
                }
            }
        });
    }
}