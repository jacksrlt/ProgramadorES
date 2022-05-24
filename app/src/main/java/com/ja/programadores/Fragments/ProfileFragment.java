package com.ja.programadores.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ja.programadores.R;

public class ProfileFragment extends Fragment {

    ImageView profileIv;
    TextView nameTv;
    TextView bioTv;
    TextView githubTv;
    TextView linkedinTv;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

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
        showProfile();
        return view;
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
                        if (!document.contains("image")) {
                            Glide.with(getContext())
                                    .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541\"")
                                    .into(profileIv);
                        } else {
                            Glide.with(getContext())
                                    .load(document.getString("image").toString())
                                    .into(profileIv);
                        }
                    }
                } else {
                    nameTv.setText("ERROR");
                    Glide.with(getContext())
                            .load("https://upload.wikimedia.org/wikipedia/commons/7/7c/Profile_avatar_placeholder_large.png?20150327203541\"")
                            .into(profileIv);
                }
            }
        });
    }
}
