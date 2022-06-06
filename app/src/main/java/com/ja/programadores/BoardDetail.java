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
import com.ja.programadores.Adapters.PostAdapter;
import com.ja.programadores.Constructors.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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