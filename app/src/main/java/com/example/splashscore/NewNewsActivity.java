package com.example.splashscore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewNewsActivity extends AppCompatActivity {
    private EditText titleInput, summaryInput;
    private Button publishButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_news);

        titleInput   = findViewById(R.id.titleInput);
        summaryInput = findViewById(R.id.summaryInput);
        publishButton= findViewById(R.id.publishButton);

        publishButton.setOnClickListener(v -> publishNews());

        findViewById(R.id.backButton)
                .setOnClickListener(v -> finish());
    }

    private void publishNews() {
        String title   = titleInput.getText().toString().trim();
        String summary = summaryInput.getText().toString().trim();
        if (title.isEmpty() || summary.isEmpty()) {
            Toast.makeText(this, "Töltsd ki mindkét mezőt", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Nem vagy bejelentkezve", Toast.LENGTH_SHORT).show();
            return;
        }

        String uploaderEmail = user.getEmail();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // új dokumentum ID előállítása
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String docId = db.collection("news").document().getId();

        Map<String,Object> data = new HashMap<>();
        data.put("id", docId);
        data.put("title", title);
        data.put("summary", summary);
        data.put("date", date);
        data.put("uploader_email", uploaderEmail);

        db.collection("news")
                .document(docId)
                .set(data)
                .addOnSuccessListener(__ -> {
                    Toast.makeText(this, "Hír közzétéve", Toast.LENGTH_SHORT).show();
                    finish();  // vissza a HirekFragment-re
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
