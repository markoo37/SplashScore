package com.example.splashscore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    //REGISTRATION WITH FIREBASE AUTHENTICATION
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final int SECRET_KEY = 544;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    EditText emailEditText, passwordEditText, passwordAgainEditText;
    Button registerButton;
    Button backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 544){
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordAgainEditText = findViewById(R.id.passwordAgainEditText);
        registerButton = findViewById(R.id.registerButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class).putExtra("SECRET_KEY", SECRET_KEY);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        });

        registerButton.setOnClickListener(v -> registerUser());

    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            passwordEditText.setText("");
            passwordAgainEditText.setText("");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = authResult.getUser();
                    if (user == null) { return; }

                    Map<String, Object> profile = new HashMap<>();
                    profile.put("uid",       user.getUid());
                    profile.put("email",     user.getEmail());
                    profile.put("createdAt", FieldValue.serverTimestamp());
                    profile.put("avatarUrl", null);
                    db.collection("profiles")
                            .document(user.getUid())
                            .set(profile)
                            .addOnSuccessListener(aVoid -> {
                                // Profil sikeresen mentve
                                Toast.makeText(this, "Regisztráció kész!", Toast.LENGTH_SHORT).show();
                                // Tovább a főoldalra
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class)
                                        .putExtra("SECRET_KEY", SECRET_KEY));
                                overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Hiba profil mentésekor: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    emailEditText.setText("");
                    passwordEditText.setText("");
                    passwordAgainEditText.setText("");
                });
    }

}