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
import com.ja.programadores.Adapters.DirectAdapter;
import com.ja.programadores.Constructors.Direct;
import com.ja.programadores.CreatePost;
import com.ja.programadores.NewDirect;
import com.ja.programadores.R;

import java.util.ArrayList;
import java.util.List;

public class DirectFragment extends Fragment {

    RecyclerView directRecyclerView;
    DirectAdapter directAdapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    CollectionReference collectionReferenceDirects;
    CollectionReference collectionReferenceUsers;
    List<Direct> directList;
    String currentUser;
    FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_direct, container, false);

        directRecyclerView = view.findViewById(R.id.directList);
        directRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getUid();
        collectionReferenceDirects = firebaseFirestore.collection("Directs");
        collectionReferenceUsers = firebaseFirestore.collection("Users");
        directList = new ArrayList<>();
        directAdapter = new DirectAdapter(getActivity(), directList);
        directRecyclerView.setAdapter(directAdapter);
        loadDirects();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent
                        = new Intent(getContext(),
                        NewDirect.class);
                startActivity(intent);
            }
        });
    }

    private void loadDirects() {

        Query directQuery = collectionReferenceDirects.whereEqualTo("rec", currentUser).orderBy("timestamp", Query.Direction.DESCENDING);
        directQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Direct direct = new Direct();
                    direct.setSubject(document.getString("subject"));
                    direct.setMessage(document.getString("message"));
                    direct.setDirectkey(document.getId());
                    direct.setImage(document.getString("image"));
                    String senderUid = document.getString("sender");
                    DocumentReference userRef = collectionReferenceUsers.document(senderUid);
                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            direct.setName(documentSnapshot.getString("name"));
                            direct.setAvatar(documentSnapshot.getString("image"));
                            directList.add(direct);
                            directAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }


}