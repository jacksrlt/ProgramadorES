package com.ja.programadores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBt;
    private Button registerBt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Instancia de FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //Inicializar vistas
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBt = findViewById(R.id.loginBt);
        registerBt = findViewById(R.id.registerBt);

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
                                                    "Bienvenid@",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    //Si se inicia sesión
                                    //Intent a activity home
                                    Intent intent
                                            = new Intent(Login.this,
                                            NavigationDrawer.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    //Inicio de sesión fallido
                                    Toast.makeText(getApplicationContext(),
                                                    "No se ha podido iniciar sesión",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        });

    }

}
