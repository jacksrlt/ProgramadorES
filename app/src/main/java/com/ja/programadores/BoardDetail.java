package com.ja.programadores;


import android.os.Bundle;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BoardDetail extends AppCompatActivity {

    ImageView imgUserPost;
    TextView txtBoardDesc, txtBoardName, txtBoardTitle, txtBoardLocation;
    String PostKey;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore fStore;
    CollectionReference collectionReferenceUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        // ini Views
        imgUserPost = findViewById(R.id.board_detail_user_img);
        txtBoardTitle = findViewById(R.id.board_detail_title);
        txtBoardDesc = findViewById(R.id.board_detail_desc);
        txtBoardName = findViewById(R.id.board_detail_date_name);
        txtBoardLocation = findViewById(R.id.board_detail_location);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        fStore = FirebaseFirestore.getInstance();
        collectionReferenceUsers = fStore.collection("Users");

        String boardTitle = getIntent().getExtras().getString("title");
        txtBoardTitle.setText(boardTitle);

        String boardLocation = getIntent().getExtras().getString("location");
        txtBoardLocation.setText(boardLocation);

        String userpostImage = getIntent().getExtras().getString("avatar");
        Glide.with(this).load(userpostImage).into(imgUserPost);

        String boardDescription = getIntent().getExtras().getString("content");
        txtBoardDesc.setText(boardDescription);

        String txtDate = dateToString((Timestamp) getIntent().getExtras().get("timestamp"));
        String txtName = getIntent().getExtras().getString("name");
        txtBoardName.setText(txtDate + " | " + txtName);

        // get post id
        PostKey = getIntent().getExtras().getString("postkey");

    }


    private String dateToString(Timestamp timestamp) {

        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String txtDate = dateFormat.format(date);
        return txtDate;

    }


}