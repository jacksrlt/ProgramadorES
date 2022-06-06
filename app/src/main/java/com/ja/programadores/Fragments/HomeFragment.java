package com.ja.programadores.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import com.ja.programadores.Adapters.PostAdapter;
import com.ja.programadores.Constructors.Post;
import com.ja.programadores.CreatePost;
import com.ja.programadores.EditProfile;
import com.ja.programadores.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    CollectionReference collectionReferencePosts;
    CollectionReference collectionReferenceUsers;
    List<Post> postList;
    String currentUser;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postRecyclerView = view.findViewById(R.id.postList);
        postRecyclerView.setVisibility(View.INVISIBLE);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getUid();
        collectionReferencePosts = firebaseFirestore.collection("Posts");
        collectionReferenceUsers = firebaseFirestore.collection("Users");
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), postList);
        postRecyclerView.setAdapter(postAdapter);
        postAdapter.notifyDataSetChanged();
        progressBar = view.findViewById(R.id.progressBar);
        loadPosts();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DocumentReference userType = collectionReferenceUsers.document(currentUser);
        userType.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getBoolean("op") != true) {
                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setVisibility(View.VISIBLE);
                    fab.setClickable(true);
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent
                                    = new Intent(getContext(),
                                    CreatePost.class);
                            startActivity(intent);
                        }
                    });
                } else {
                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                }
            }
        });
    }

    private void loadPosts() {

        progressBar.setVisibility(View.VISIBLE);
        Query postQuery = collectionReferencePosts.orderBy("timestamp", Query.Direction.DESCENDING);
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
                            postList.add(post);
                            postAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.INVISIBLE);
                            postRecyclerView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }


}