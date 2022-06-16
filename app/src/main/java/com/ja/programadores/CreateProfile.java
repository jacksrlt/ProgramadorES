package com.ja.programadores;

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
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.ja.programadores.Drawers.NavigationDrawer;

import java.util.HashMap;

public class CreateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private String myUri = "";
    ImageView profileIv;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private EditText nameEt, bioEt, linkedinEt, githubEt;
    Button saveBt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("users_photos");

        nameEt = findViewById(R.id.nameTv);
        bioEt = findViewById(R.id.descEt);
        linkedinEt = findViewById(R.id.webEt);
        githubEt = findViewById(R.id.githubTv);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        profileIv = findViewById(R.id.profileIv);
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }
            }
        });

        saveBt = findViewById(R.id.saveBt);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                saveBt.setClickable(false);
                final String name = nameEt.getText().toString();
                final String bio = bioEt.getText().toString();
                final String linkedin = linkedinEt.getText().toString();
                final String github = githubEt.getText().toString();
                if (pickedImgUri == null) {
                    Toast.makeText(CreateProfile.this, "Debe seleccionar una imagen de perfil", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    saveBt.setClickable(true);
                } else if (name.isEmpty() || bio.isEmpty() || linkedin.isEmpty() || github.isEmpty()) {
                    Toast.makeText(CreateProfile.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    saveBt.setClickable(true);
                } else {
                    SaveProfile(name, bio, linkedin, github, pickedImgUri, mAuth.getCurrentUser());
                }
            }
        });

    }

    private void SaveProfile(String name, String bio, String linkedin, String github, Uri pickedImgUri, FirebaseUser currentUser) {

        StorageReference imageFilePath = storageProfilePicRef.child(mAuth.getCurrentUser().getUid()+".jpg");
        uploadTask = imageFilePath.putFile(pickedImgUri);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if(!task.isSuccessful()) {
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

                    HashMap<String, Object> userMap = new HashMap();
                    userMap.put("image", myUri);
                    userMap.put("name", name);
                    userMap.put("bio", bio);
                    userMap.put("linkedin", linkedin);
                    userMap.put("github", github);
                    userMap.put("first", false);
                    userMap.put("op", false);

                    String userUID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Users").document(userUID);
                    documentReference.set(userMap);
                    progressBar.setVisibility(View.INVISIBLE);
                    saveBt.setClickable(true);

                    Intent intent
                            = new Intent(CreateProfile.this,
                            NavigationDrawer.class);
                    startActivity(intent);
                    finish();

                }
            }
        });


    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(CreateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(CreateProfile.this, "Debe aceptar los permisos", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(CreateProfile.this,
                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
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
            profileIv.setImageURI(pickedImgUri);
        }
    }
}