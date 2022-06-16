package com.ja.programadores;


import android.content.Intent;
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

public class DirectDetail extends AppCompatActivity {

    ImageView imgPost, imgUserPost;
    TextView txtPostDesc, txtPostDateName, txtPostTitle;
    Button answerBt;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore fStore;
    CollectionReference collectionReferenceUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_detail);

        //Views
        imgPost = findViewById(R.id.postPictureIV);
        imgUserPost = findViewById(R.id.board_detail_user_img);
        answerBt = findViewById(R.id.directBt);

        txtPostTitle = findViewById(R.id.board_detail_title);
        txtPostDesc = findViewById(R.id.board_detail_desc);
        txtPostDateName = findViewById(R.id.board_detail_date_name);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fStore = FirebaseFirestore.getInstance();
        collectionReferenceUsers = fStore.collection("Users");


        String directImage = getIntent().getExtras().getString("image");
        Glide.with(this).load(directImage).into(imgPost);

        String directTitle = getIntent().getExtras().getString("subject");
        txtPostTitle.setText(directTitle);

        String userpostImage = getIntent().getExtras().getString("avatar");
        Glide.with(this).load(userpostImage).circleCrop().into(imgUserPost);

        String directDescription = getIntent().getExtras().getString("message");
        txtPostDesc.setText(directDescription);

        String txtDate = dateToString((Timestamp) getIntent().getExtras().get("timestamp"));
        String txtName = getIntent().getExtras().getString("name");
        txtPostDateName.setText(txtDate + " | " + txtName);

        answerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createDirect = new Intent(DirectDetail.this, CreateDirect.class);
                createDirect.putExtra("recuid", getIntent().getExtras().getString("senderuid"));
                createDirect.putExtra("subject", getIntent().getExtras().getString("subject"));
                startActivity(createDirect);
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