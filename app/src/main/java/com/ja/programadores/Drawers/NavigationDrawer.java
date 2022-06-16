package com.ja.programadores.Drawers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ja.programadores.CreatePost;
import com.ja.programadores.Fragments.BoardFragment;
import com.ja.programadores.Fragments.DirectFragment;
import com.ja.programadores.Fragments.HomeFragment;
import com.ja.programadores.Fragments.ProfileFragment;
import com.ja.programadores.Fragments.ProfileFragmentOp;
import com.ja.programadores.Login;
import com.ja.programadores.R;

public class NavigationDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    FirebaseDatabase firebaseDatabase;
    Boolean op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        //User Data
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //UI
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        getSupportFragmentManager().beginTransaction().add(R.id.content, new HomeFragment()).commit();
        setTitle("Home");

        //Setup Toggle
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);

        //Setup Header
        View navHeaderView = navigationView.getHeaderView(0);
        ImageView avatar = (ImageView) navHeaderView.findViewById(R.id.avatar);
        TextView name = (TextView) navHeaderView.findViewById(R.id.name);
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText(document.getString("name"));
                        Glide.with(getApplicationContext())
                                .load(document.getString("image"))
                                .circleCrop()
                                .into(avatar);
                        op = document.getBoolean("op");
                    }
                }
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectItemNav(item);
        return true;
    }

    private void selectItemNav(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_home:
                ft.replace(R.id.content, new HomeFragment()).commit();
                break;
            case R.id.nav_profile:
                if (op != true) {
                    ft.replace(R.id.content, new ProfileFragment()).commit();
                    break;
                } else {
                    ft.replace(R.id.content, new ProfileFragmentOp()).commit();
                    break;
                }
            case R.id.nav_md:
                ft.replace(R.id.content, new DirectFragment()).commit();
                break;
            case R.id.nav_board:
                ft.replace(R.id.content, new BoardFragment()).commit();
                break;
            case R.id.nav_logout:
                SharedPreferences preferences = getApplicationContext().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "flase");
                editor.apply();
                Intent logOut = new Intent(getApplicationContext(), Login.class);
                startActivity(logOut);
                finish();
                break;
        }
        setTitle(item.getTitle());
        mDrawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}