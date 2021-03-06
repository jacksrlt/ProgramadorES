package com.ja.programadores.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.ja.programadores.POJO.Board;
import com.ja.programadores.EditProfile;
import com.ja.programadores.EditProfileOp;
import com.ja.programadores.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragmentOp extends Fragment {

    ImageView profileIv;
    TextView nameTv;
    TextView descTv;
    TextView webTv;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    FloatingActionButton fab;
    String currentUser;
    RecyclerView userBoardRecycler;
    BoardAdapter userBoardAdapter;
    CollectionReference collectionReferenceBoards;
    CollectionReference collectionReferenceUsers;
    List<Board> userBoardList;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //User Data
        View view = inflater.inflate(R.layout.fragment_profile_op, container, false);
        userBoardRecycler = view.findViewById(R.id.userBoardRecycler);
        userBoardRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        profileIv = view.findViewById(R.id.profileIv);
        nameTv = view.findViewById(R.id.nameTv);
        descTv = view.findViewById(R.id.descTv);
        webTv = view.findViewById(R.id.webTv);
        collectionReferenceUsers = fStore.collection("Users");
        currentUser = mAuth.getUid();
        collectionReferenceBoards = fStore.collection("Boards");
        userBoardList = new ArrayList<>();
        userBoardAdapter = new BoardAdapter(getActivity(), userBoardList);
        userBoardRecycler.setAdapter(userBoardAdapter);
        progressBar = view.findViewById(R.id.progressBar);
        loadPosts();
        showProfile();
        return view;
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);
        Query boardQuery = collectionReferenceBoards.whereEqualTo("useruid", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);
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
                    DocumentReference userRef = collectionReferenceUsers.document(posterUid);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DocumentReference userType = collectionReferenceUsers.document(currentUser);
        userType.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getBoolean("op") == true) {
                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setVisibility(View.VISIBLE);
                    fab.setClickable(true);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent
                                    = new Intent(getContext(),
                                    EditProfileOp.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent
                                    = new Intent(getContext(),
                                    EditProfile.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    public void showProfile() {
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nameTv.setText(document.getString("name").toString());
                        descTv.setText(document.getString("desc").toString());
                        webTv.setText(document.getString("web").toString());
                        Glide.with(getContext())
                                .load(document.getString("image").toString())
                                .into(profileIv);

                    }
                }
            }
        });
    }
}
