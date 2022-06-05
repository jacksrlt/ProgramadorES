package com.ja.programadores.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.ja.programadores.CreatePost;
import com.ja.programadores.EditProfile;
import com.ja.programadores.EditProfileOp;
import com.ja.programadores.R;

public class ProfileFragment extends Fragment {

    ImageView profileIv;
    TextView nameTv;
    TextView bioTv;
    TextView githubTv;
    TextView linkedinTv;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    FloatingActionButton fab;
    String currentUser;
    CollectionReference collectionReferenceUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //User Data
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        profileIv = view.findViewById(R.id.profileIv);
        nameTv = view.findViewById(R.id.nameTv);
        bioTv = view.findViewById(R.id.bioTv);
        githubTv = view.findViewById(R.id.githubTv);
        linkedinTv = view.findViewById(R.id.linkedinTv);
        collectionReferenceUsers = fStore.collection("Users");
        currentUser = mAuth.getUid();
        showProfile();
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
                } else {
                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
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
                        bioTv.setText(document.getString("bio").toString());
                        linkedinTv.setText(document.getString("linkedin").toString());
                        githubTv.setText(document.getString("github").toString());
                        Glide.with(getContext())
                                .load(document.getString("image").toString())
                                .into(profileIv);
                    }
                }
            }
        });
    }
}
