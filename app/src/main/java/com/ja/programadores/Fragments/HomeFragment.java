package com.ja.programadores.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ja.programadores.Adapters.PostAdapter;
import com.ja.programadores.Constructors.Post;
import com.ja.programadores.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView postRecyclerView;
    PostAdapter postAdapter;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReferencePosts;
    CollectionReference collectionReferenceUsers;
    List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        postRecyclerView = view.findViewById(R.id.postList);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReferencePosts = firebaseFirestore.collection("Posts");
        collectionReferenceUsers = firebaseFirestore.collection("Users");
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), postList);
        postRecyclerView.setAdapter(postAdapter);
        postRecyclerView.setItemViewCacheSize(2);
        loadPosts();

        return view;
    }

    private void loadPosts() {
        Query postQuery = collectionReferencePosts.orderBy("timestamp", Query.Direction.DESCENDING);
        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Post post = new Post();
                    post.setTitle(document.getString("title"));
                    post.setContent(document.getString("content"));
                    post.setImage(document.getString("image"));
                    String posterUid = document.getString("useruid");
                    DocumentReference docRef = collectionReferenceUsers.document(posterUid);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                post.setName(document.getString("name").toString());
                                post.setAvatar(document.getString("image").toString());
                            }
                        }
                    });
                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }
        });
    }


}