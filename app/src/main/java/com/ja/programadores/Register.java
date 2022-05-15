package com.ja.programadores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText nameEt, emailEt, passwordEt, password2Et;
    private Button registerBt;
    private FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Instancia de FirebaseAuth y FirebaseSFirestore
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Inicializar vistas
        nameEt = findViewById(R.id.nameEt);
        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        password2Et = findViewById(R.id.password2Et);
        registerBt = findViewById(R.id.registerBt);

        //Onclick Listener en Botón de registro
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {
        //Tomar los valores de los EditText como String
        String name, email, password, password2;
        name = nameEt.getText().toString();
        email = emailEt.getText().toString();
        password = passwordEt.getText().toString();
        password2 = password2Et.getText().toString();

        //Validación de email, contraseña y usuario
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

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(),
                            "Por favor, inserte su nombre",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(getApplicationContext(),
                            "Las contraseñas no coinciden",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        //Crear un nuevo usuario
        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                            "Usuario registrado",
                                            Toast.LENGTH_LONG)
                                    .show();

                            //Crear colección para usuario con UID y añadir nombre de usuario
                            userUID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userUID);
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            documentReference.set(userData);
                            //Enviar a login si el usuario fue creado
                            Intent intent
                                    = new Intent(Register.this,
                                    Login.class);
                            startActivity(intent);
                        } else {

                            // Registro fallido
                            Toast.makeText(
                                            getApplicationContext(),
                                            "No se ha creado el usuario, intente nuevamente",
                                            Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }

}
