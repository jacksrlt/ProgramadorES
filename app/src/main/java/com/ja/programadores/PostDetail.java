package com.ja.programadores;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ja.programadores.Adapters.CommentAdapter;
import com.ja.programadores.POJO.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PostDetail extends AppCompatActivity {

    ImageView imgPost, imgUserPost, imgCurrentUser;
    TextView txtPostDesc, txtPostDateName, txtPostTitle;
    EditText editTextComment;
    Button btnAddComment;
    String PostKey;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore fStore;
    CollectionReference collectionReferenceComments;
    CollectionReference collectionReferenceUsers;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> commentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //Views
        RvComment = findViewById(R.id.commentRecycler);
        imgPost = findViewById(R.id.postPictureIV);
        imgUserPost = findViewById(R.id.postPosterAvatarIV);
        imgCurrentUser = findViewById(R.id.postAvatarIV);

        txtPostTitle = findViewById(R.id.postTitleTV);
        txtPostDesc = findViewById(R.id.postContentTV);
        txtPostDateName = findViewById(R.id.postNameDateTV);

        editTextComment = findViewById(R.id.postCommentET);
        btnAddComment = findViewById(R.id.postCommentBT);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fStore = FirebaseFirestore.getInstance();
        collectionReferenceComments = fStore.collection("Comments");
        collectionReferenceUsers = fStore.collection("Users");

        //Hide comment

        userType();

        //Make a comment

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendComment();
            }
        });


        String postImage = getIntent().getExtras().getString("image");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userpostImage = getIntent().getExtras().getString("avatar");
        Glide.with(this).load(userpostImage).circleCrop().into(imgUserPost);

        String postDescription = getIntent().getExtras().getString("content");
        txtPostDesc.setText(postDescription);

        String txtDate = dateToString((Timestamp) getIntent().getExtras().get("timestamp"));
        String txtName = getIntent().getExtras().getString("name");
        txtPostDateName.setText(txtDate + " | " + txtName);

        // setcomment user image
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Glide.with(getApplicationContext())
                            .load(document.getString("image"))
                            .circleCrop()
                            .into(imgCurrentUser);
                }
            }
        });

        //Postkey
        PostKey = getIntent().getExtras().getString("postkey");

        //Comments
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        RvComment.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
        loadComments();

    }


    private void userType() {
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.getBoolean("op") == true) {
                        imgCurrentUser.setVisibility(View.GONE);
                        editTextComment.setVisibility(View.GONE);
                        btnAddComment.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void sendComment() {
        final String content = editTextComment.getText().toString();
        final String useruid = mAuth.getCurrentUser().getUid();
        final String postkey = PostKey;
        Object myTimestamp = FieldValue.serverTimestamp();
        if (content.isEmpty()) {
            Toast.makeText(PostDetail.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            SaveComment(content, useruid, myTimestamp, postkey);
            commentAdapter.notifyDataSetChanged();
        }
    }

    private void SaveComment(String content, String useruid, Object myTimestamp, String postkey) {
        HashMap<String, Object> comment = new HashMap();
        comment.put("content", content);
        comment.put("useruid", useruid);
        comment.put("timestamp", myTimestamp);
        comment.put("postkey", postkey);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Comment").push();
        String key = myRef.getKey();

        DocumentReference documentReference = fStore.collection("Comments").document(key);
        documentReference.set(comment);

        Toast.makeText(PostDetail.this, "Comentario creado", Toast.LENGTH_SHORT).show();
        commentAdapter.notifyDataSetChanged();
    }

    private void loadComments() {
        RvComment.setLayoutManager(new LinearLayoutManager(this));

        Query commentQuery = collectionReferenceComments
                .whereEqualTo("postkey", getIntent().getExtras().getString("postkey"))
                .orderBy("timestamp", Query.Direction.DESCENDING);
        commentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Comment comment = new Comment();
                    comment.setContent(document.getString("content"));
                    comment.setTimestamp(document.get("timestamp"));
                    String commenterUid = document.getString("useruid");
                    DocumentReference userRef = collectionReferenceUsers.document(commenterUid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            comment.setName(documentSnapshot.getString("name"));
                            comment.setAvatar(documentSnapshot.getString("image"));
                            commentList.add(comment);
                            commentAdapter.notifyDataSetChanged();

                        }
                    });
                }
            }
        });

    }

    private String dateToString(Timestamp timestamp) {

        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String txtDate = dateFormat.format(date);
        return txtDate;

    }


}