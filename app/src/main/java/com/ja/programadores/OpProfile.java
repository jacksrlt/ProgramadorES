package com.ja.programadores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ja.programadores.Adapters.BoardAdapter;
import com.ja.programadores.Constructors.Board;

import java.util.ArrayList;
import java.util.List;

public class OpProfile extends AppCompatActivity {

    ImageView profileIv;
    TextView nameTv;
    TextView descTv;
    TextView webTv;
    private FirebaseFirestore fStore;
    RecyclerView userBoardRecycler;
    BoardAdapter userBoardAdapter;
    CollectionReference collectionReferenceBoards;
    CollectionReference collectionReferenceUsers;
    List<Board> userBoardList;
    ProgressBar progressBar;
    String useruid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_profile);

        userBoardRecycler = findViewById(R.id.userBoardRecycler);
        userBoardRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fStore = FirebaseFirestore.getInstance();
        profileIv = findViewById(R.id.profileIv);
        nameTv = findViewById(R.id.nameTv);
        descTv = findViewById(R.id.descTv);
        webTv = findViewById(R.id.webTv);
        collectionReferenceUsers = fStore.collection("Users");
        collectionReferenceBoards = fStore.collection("Boards");
        userBoardList = new ArrayList<>();
        userBoardAdapter = new BoardAdapter(getApplicationContext(), userBoardList);
        userBoardRecycler.setAdapter(userBoardAdapter);
        progressBar = findViewById(R.id.progressBar);

        useruid = getIntent().getExtras().getString("useruid");

        loadPosts();
        showProfile();

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
                        descTv.setText(document.getString("desc").toString());
                        webTv.setText(document.getString("web").toString());
                        Glide.with(getApplicationContext())
                                .load(document.getString("image").toString())
                                .into(profileIv);

                    }
                }
            }
        });
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);
        Query boardQuery = collectionReferenceBoards.whereEqualTo("useruid", useruid).orderBy("timestamp", Query.Direction.DESCENDING);
        boardQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Board board = new Board();
                    board.setTitle(document.getString("title"));
                    board.setContent(document.getString("content"));
                    board.setBoardkey(document.getId());
                    board.setLocation(document.getString("location"));
                    board.setTimestamp(document.get("timestamp"));
                    String posterUid = document.getString("useruid");
                    DocumentReference userRef = collectionReferenceUsers.document(useruid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            board.setName(documentSnapshot.getString("name"));
                            board.setAvatar(documentSnapshot.getString("image"));
                            userBoardList.add(board);
                            userBoardAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }
}