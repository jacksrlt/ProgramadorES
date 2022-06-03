package com.ja.programadores;

import static com.ja.programadores.CreateProfile.PReqCode;
import static com.ja.programadores.CreateProfile.REQUESCODE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ja.programadores.Drawers.NavigationDrawer;

import java.util.HashMap;

public class CreateBoard extends AppCompatActivity {

    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    TextView nameTv;
    ImageView avatarIV;
    EditText titleEt, contentEt, locationEt;
    Button sendBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);

        //User Data
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Setup User Show
        showUserData();

        //Make Board
        titleEt = findViewById(R.id.titleEt);
        contentEt = findViewById(R.id.contentEt);
        locationEt = findViewById(R.id.locationEt);
        sendBt = findViewById(R.id.sendBt);
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBoard();
            }
        });

    }

    private void sendBoard() {
        final String title = titleEt.getText().toString();
        final String content = contentEt.getText().toString();
        final String useruid = mAuth.getCurrentUser().getUid();
        final String location = locationEt.getText().toString();
        Object myTimestamp = FieldValue.serverTimestamp();
        if (title.isEmpty() || content.isEmpty() || location.isEmpty()) {
            Toast.makeText(CreateBoard.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            SaveBoard(title, content, location, useruid, myTimestamp);
        }

    }

    private void SaveBoard(String title, String content, String location, String useruid, Object myTimeStamp) {

        HashMap<String, Object> board = new HashMap();
        board.put("location", location);
        board.put("title", title);
        board.put("content", content);
        board.put("useruid", useruid);
        board.put("timestamp", myTimeStamp);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Boards").push();
        String key = myRef.getKey();

        DocumentReference documentReference = fStore.collection("Boards").document(key);
        documentReference.set(board);

        Toast.makeText(CreateBoard.this, "Publicación creada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(CreateBoard.this,
                NavigationDrawer.class);
        startActivity(intent);
        finish();

    }

    private void showUserData() {
        avatarIV = findViewById(R.id.avatarIV);
        nameTv = findViewById(R.id.nameTV);
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        nameTv.setText(document.getString("name").toString());
                        Glide.with(getApplicationContext())
                                .load(document.getString("image").toString())
                                .into(avatarIV);
                    }
                }
            }
        });
    }
}