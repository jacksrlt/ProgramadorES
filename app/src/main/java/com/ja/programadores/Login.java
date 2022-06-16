package com.ja.programadores;

import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ja.programadores.Drawers.NavigationDrawer;

public class Login extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText emailEt, passwordEt;
    private Button loginBt;
    private Button registerBt;
    private FirebaseAuth mAuth;
    CheckBox remember;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instancia de FirebaseAuth y Firestore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Inicializar vistas
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBt = findViewById(R.id.loginBt);
        registerBt = findViewById(R.id.registerBt);
        progressBar = findViewById(R.id.progressBar);
        remember = findViewById(R.id.remember);

        progressBar.setVisibility(View.INVISIBLE);

        //Onclick Listener para pantalla de registro
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRegister = new Intent(Login.this, Register.class);
                startActivity(goToRegister);
            }
        });

        //Onclick Listener en Botón de inicio de sesión
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserAccount();
                progressBar.setVisibility(VISIBLE);
            }
        });

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");

        if (checkbox.equals("true")) {

            Intent intent = new Intent(Login.this, NavigationDrawer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else if (checkbox.equals("false")) {

            Toast.makeText(this, "Por favor, inicia sesion", Toast.LENGTH_SHORT).show();

        }


        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (compoundButton.isChecked()) {

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(Login.this, "Ahora iniciarás sesión automáticamente", Toast.LENGTH_LONG).show();

                } else if (!compoundButton.isChecked()) {

                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "flase");
                    editor.apply();
                    Toast.makeText(Login.this, "Ahora necesitarás iniciar sesión la próxima vez", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void loginUserAccount() {
        //Tomar los valores de los EditText como String
        String email, password;
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();

        //Validación de email y contraseña
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Por favor, inserte una dirección de correo electrónico",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Por favor, inserte una contraseña",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        //Iniciar sesión de usuario existente
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                                    "Se ha iniciado sesión",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    //Si se inicia sesión
                                    DocumentReference docRef = fStore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    if (document.getBoolean("first") == true) {
                                                        //Intent a activity CreateProfile
                                                        if (document.getBoolean("op") != true) {
                                                            Intent intent
                                                                    = new Intent(Login.this,
                                                                    CreateProfile.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Intent intent
                                                                    = new Intent(Login.this,
                                                                    CreateProfileOp.class);
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } else if (document.getBoolean("first") != true) {
                                                        //Intent a NavigationDrawer
                                                        Intent intent
                                                                = new Intent(Login.this,
                                                                NavigationDrawer.class);
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            }
                                        }
                                    });
                                } else {

                                    //Inicio de sesión fallido
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),
                                                    "No se ha podido iniciar sesión",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });

    }

}
