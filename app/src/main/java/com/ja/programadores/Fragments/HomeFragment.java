package com.ja.programadores.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ja.programadores.Adapters.AdapterHome;
import com.ja.programadores.Constructors.Post;
import com.ja.programadores.R;

import java.util.ArrayList;
import java.util.Map;


public class HomeFragment extends Fragment {

    private DatabaseReference postDatabase;
    AdapterHome adapterHome;
    private RecyclerView recyclerViewHome;
    ArrayList<Post> messageList = new ArrayList<>();

    String mProfileImageUrl;
    ProgressDialog progressDialog;
    String user = null, message = null;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postDatabase = FirebaseDatabase.getInstance("https://programadores-ffb69-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Messages");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerViewHome = view.findViewById(R.id.homeRecycler);

        mostrarData();
        cargarLista();
        dialogoProgreso();
        return view;
    }

    private void cargarLista() {
        messageList.clear();
        postDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                allListData(dataSnapshot);
                adapterHome.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @SuppressLint("NewApi")
    public void allListData(final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
            if (map.get("useruid") != null) {
                user = map.get("useruid").toString();
            }
            if (map.get("message") != null) {
                message = map.get("message").toString();
            }
            if (map.get("image") != null) {
                mProfileImageUrl = map.get("image").toString();
            }
        }

        progressDialog.dismiss();

        messageList.add(new Post(user, message, mProfileImageUrl));
    }

    public void mostrarData() {
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterHome = new AdapterHome(getContext(), messageList);
        recyclerViewHome.setItemAnimator(new DefaultItemAnimator());
        recyclerViewHome.setAdapter(adapterHome);
    }

    public void dialogoProgreso() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

}