package com.example.splashscore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final int SECRET_KEY = 544;
    MatchScores_RecyclerViewAdapter adapter;
    ArrayList<MatchScoreModel> matchScoreModels = new ArrayList<>();

    FirebaseAuth mAuth;

    TextView currentUsTextView;
    Button logoutButton;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 544){
            finish();
        }

        db = FirebaseFirestore.getInstance();
        String email = getIntent().getStringExtra("email");

        logoutButton = findViewById(R.id.logoutButton);
        currentUsTextView = findViewById(R.id.currentUsTextView);
        if (email != null) {
            String cur = "Current user: " + email.split("@")[0];
            currentUsTextView.setText(cur);
        }

        setUpMatchScoreModels();
        Log.v("LIST_TEST", String.valueOf(matchScoreModels.size()));

        //teamname = findViewById(R.id.teamNameText);
        RecyclerView recyclerView = findViewById(R.id.scoresRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MatchScores_RecyclerViewAdapter(matchScoreModels);
        recyclerView.setAdapter(adapter);

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

    }

    private void setUpMatchScoreModels(){
        ArrayList<String> hTeamNames = new ArrayList<>();
        ArrayList<String> aTeamNames = new ArrayList<>();
        ArrayList<String> quartersS = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();

        db.collection("matches")  // Change this to your actual Firestore collection
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String htn = document.getString("homeTeamName"); // Replace "name" with your Firestore field
                        String atn = document.getString("awayTeamName"); // Replace "name" with your Firestore field
                        String q = document.getString("quarters"); // Replace "name" with your Firestore field
                        String s = document.getString("score"); // Replace "name" with your Firestore field
                        if (htn != null && atn != null && q != null && s != null) {
                            hTeamNames.add(htn);
                            aTeamNames.add(atn);
                            quartersS.add(q);
                            scores.add(s);
                        }
                    }

                    for (int i = 0; i < hTeamNames.size(); i++) {
                        Log.i("MATCH_MODEL_INFO", "homeName: " + hTeamNames.get(i));
                        Log.i("MATCH_MODEL_INFO", "awayName: " + aTeamNames.get(i));
                        Log.i("MATCH_MODEL_INFO", "quarters: " + quartersS.get(i));
                        Log.i("MATCH_MODEL_INFO", "score: " + scores.get(i));

                        MatchScoreModel matchScoreModel = new MatchScoreModel(hTeamNames.get(i), aTeamNames.get(i), quartersS.get(i), scores.get(i));
                        matchScoreModels.add(matchScoreModel);
                    }
                    adapter.notifyDataSetChanged();
                    //Log.i("MODELS_TEST", "awayteamname: " + matchScoreModels.get(0).getaTeamName());

                    // Use teamNames list here (e.g. populate UI, adapter, etc.)
                    Log.d("TEAM_LIST", "Fetched home team names: " + hTeamNames);
                    Log.d("TEAM_LIST", "Fetched away team names: " + aTeamNames);
                    Log.d("TEAM_LIST", "Fetched quarters: " + quartersS);
                    Log.d("TEAM_LIST", "Fetched scores: " + scores);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_ERROR", "Error fetching teams", e);
                });
    }
}