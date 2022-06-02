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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ja.programadores.Drawers.NavigationDrawer;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

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

        nameEt = findViewById(R.id.nameEt);
        bioEt = findViewById(R.id.descEt);
        linkedinEt = findViewById(R.id.webEt);
        githubEt = findViewById(R.id.githubEt);
        progressBar = findViewById(R.id.progressBar);
        saveBt = findViewById(R.id.saveBt);

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

        showData();

        saveBt = findViewById(R.id.saveBt);
        saveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                final String name = nameEt.getText().toString();
                final String bio = bioEt.getText().toString();
                final String linkedin = linkedinEt.getText().toString();
                final String github = githubEt.getText().toString();
                SaveProfile(name, bio, linkedin, github, pickedImgUri, mAuth.getCurrentUser());

            }
        });

    }

    private void showData() {
        DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    nameEt.setText(document.getString("name"));
                    bioEt.setText(document.getString("bio"));
                    linkedinEt.setText(document.getString("linkedin"));
                    githubEt.setText(document.getString("github"));
                    Glide.with(getApplicationContext())
                            .load(document.getString("image"))
                            .into(profileIv);
                }
            }
        });
    }

    private void SaveProfile(String name, String bio, String linkedin, String github, Uri pickedImgUri, FirebaseUser currentUser) {

        HashMap<String, Object> profileUpdate = new HashMap();

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

                        HashMap<String, Object> updateProfile = new HashMap();
                        updateProfile.put("image", myUri);
                        updateProfile.put("name", name);
                        updateProfile.put("linkedin", linkedin);
                        updateProfile.put("github", github);
                        updateProfile.put("bio", bio);
                        updateProfile.put("first", false);

                        DocumentReference documentReference = fStore.collection("Users").document(currentUser.getUid());
                        documentReference.set(updateProfile);

                        Toast.makeText(EditProfile.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();

                        Intent intent
                                = new Intent(EditProfile.this,
                                NavigationDrawer.class);
                        startActivity(intent);
                        finish();

                    }
                }
            });
        } else {

            DocumentReference documentReference = fStore.collection("Users").document(currentUser.getUid());

            HashMap<String, Object> updateProfile = new HashMap();
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    updateProfile.put("name", name);
                    updateProfile.put("linkedin", linkedin);
                    updateProfile.put("github", github);
                    updateProfile.put("bio", bio);
                    updateProfile.put("first", false);
                    updateProfile.put("image", documentSnapshot.getString("image"));
                    documentReference.set(updateProfile);
                }
            });

            Toast.makeText(EditProfile.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();

            Intent intent
                    = new Intent(EditProfile.this,
                    NavigationDrawer.class);
            startActivity(intent);
            finish();

        }


    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(EditProfile.this, "Debe aceptar los permisos", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(EditProfile.this,
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
            profileIv.setImageURI(pickedImgUri);
        }
    }
}