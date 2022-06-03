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

public class CreateProfileOp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private String myUri = "";
    ImageView profileIv;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private EditText nameEt, descEt, webEt;
    Button saveBt;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_op);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageProfilePicRef = FirebaseStorage.getInstance().getReference().child("users_photos");

        nameEt = findViewById(R.id.nameTv);
        descEt = findViewById(R.id.descEt);
        webEt = findViewById(R.id.webEt);
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
                final String name = nameEt.getText().toString();
                final String desc = descEt.getText().toString();
                final String web = webEt.getText().toString();
                if (pickedImgUri == null) {
                    Toast.makeText(CreateProfileOp.this, "Debe seleccionar una imagen de perfil", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else if (name.isEmpty() || desc.isEmpty() || web.isEmpty()) {
                    Toast.makeText(CreateProfileOp.this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    SaveProfile(name, desc, web, pickedImgUri, mAuth.getCurrentUser());
                }
            }
        });

    }

    private void SaveProfile(String name, String desc, String web, Uri pickedImgUri, FirebaseUser currentUser) {

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
                    userMap.put("desc", desc);
                    userMap.put("web", web);
                    userMap.put("first", false);
                    userMap.put("op", true);

                    String userUID = mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("Users").document(userUID);
                    documentReference.set(userMap);

                    Intent intent
                            = new Intent(CreateProfileOp.this,
                            NavigationDrawer.class);
                    startActivity(intent);
                    finish();

                }
            }
        });


    }

    private void checkAndRequestForPermission() {
        if(ContextCompat.checkSelfPermission(CreateProfileOp.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateProfileOp.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(CreateProfileOp.this, "Debe aceptar los permisos", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(CreateProfileOp.this,
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