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
import android.widget.ProgressBar;
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

public class CreateDirect extends AppCompatActivity {

    FirebaseUser currentUser;
    ProgressBar progressBar;
    private StorageReference storageProfilePicRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    TextView nameTv;
    ImageView avatarIV, pictureIV;
    EditText subjectEt, messageEt;
    Button sendBt;
    private String myUri = "";
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_direct);

        //User Data
        progressBar = findViewById(R.id.progressBar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("direct_pictures");

        progressBar.setVisibility(View.INVISIBLE);

        //Setup User Show
        showUserData();

        //Load picture
        pictureIV = findViewById(R.id.pictureIV);
        pictureIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPicture();
            }
        });

        //Make post
        subjectEt = findViewById(R.id.titleEt);
        messageEt = findViewById(R.id.contentEt);
        sendBt = findViewById(R.id.sendBt);
        sendBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                sendBt.setClickable(false);
                sendDirect();
            }
        });

        if (getIntent().getExtras().getString("subject") != null) {
            subjectEt.setText("RE:" + getIntent().getExtras().getString("subject"));
        }

    }

    private void sendDirect() {
        String rec = getIntent().getExtras().getString("recuid");
        final String title = subjectEt.getText().toString();
        final String content = messageEt.getText().toString();
        final String senderuid = mAuth.getCurrentUser().getUid();
        final String recuid = rec;
        Object myTimestamp = FieldValue.serverTimestamp();
        if (title.isEmpty() || content.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            sendBt.setClickable(true);
            Toast.makeText(CreateDirect.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
        } else {
            SavePost(title, content, pickedImgUri, senderuid, recuid, myTimestamp);
        }

    }

    private void SavePost(String subject, String message, Uri pickedImgUri, String senderuid, String recuid, Object myTimeStamp) {

        if (pickedImgUri != null) {

            StorageReference imageFilePath = storageProfilePicRef.child(pickedImgUri.getLastPathSegment());
            UploadTask uploadTask = imageFilePath.putFile(pickedImgUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageFilePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = (Uri) task.getResult();
                        myUri = downloadUrl.toString();

                        HashMap<String, Object> direct = new HashMap();
                        direct.put("image", myUri);
                        direct.put("subject", subject);
                        direct.put("message", message);
                        direct.put("sender", senderuid);
                        direct.put("rec", recuid);
                        direct.put("timestamp", myTimeStamp);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Directs").push();
                        String key = myRef.getKey();

                        DocumentReference documentReference = fStore.collection("Directs").document(key);
                        documentReference.set(direct);

                        progressBar.setVisibility(View.INVISIBLE);
                        sendBt.setClickable(true);

                        Toast.makeText(CreateDirect.this, "Mensaje enviado", Toast.LENGTH_SHORT).show();

                        Intent intent
                                = new Intent(CreateDirect.this,
                                NavigationDrawer.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        } else {
            HashMap<String, Object> direct = new HashMap();
            direct.put("image", myUri);
            direct.put("subject", subject);
            direct.put("message", message);
            direct.put("sender", senderuid);
            direct.put("rec", recuid);
            direct.put("timestamp", myTimeStamp);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Posts").push();
            String key = myRef.getKey();

            DocumentReference documentReference = fStore.collection("Directs").document(key);
            documentReference.set(direct);

            progressBar.setVisibility(View.INVISIBLE);
            sendBt.setClickable(true);

            Toast.makeText(CreateDirect.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();

            Intent intent
                    = new Intent(CreateDirect.this,
                    NavigationDrawer.class);
            startActivity(intent);
            finish();

        }
    }

    private void loadPicture() {
        if (Build.VERSION.SDK_INT >= 22) {
            checkAndRequestForPermission();
        } else {
            openGallery();
        }
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(CreateDirect.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateDirect.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(CreateDirect.this, "Debe aceptar los permisos", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(CreateDirect.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {
            pickedImgUri = data.getData();
            pictureIV.setImageURI(pickedImgUri);
        }
    }

    private void showUserData() {
        String rec = getIntent().getExtras().getString("recuid");
        avatarIV = findViewById(R.id.avatarIV);
        nameTv = findViewById(R.id.nameTV);
        DocumentReference docRef = fStore.collection("Users").document(rec);
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