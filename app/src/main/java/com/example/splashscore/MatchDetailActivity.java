package com.example.splashscore;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MatchDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MATCH_ID = "matchId";

    private TextView tvResult;
    private RecyclerView rvHomeScorers, rvAwayScorers;
    private FirebaseFirestore db;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_detail);

        // View-k inicializálása
        tvResult      = findViewById(R.id.tvResult);
        rvHomeScorers = findViewById(R.id.rvHomeScorers);
        rvAwayScorers = findViewById(R.id.rvAwayScorers);
        toolbar = findViewById(R.id.toolbar);

        // RecyclerView-ok beállítása
        rvHomeScorers.setLayoutManager(new LinearLayoutManager(this));
        rvAwayScorers.setLayoutManager(new LinearLayoutManager(this));

        // Firestore példány
        db = FirebaseFirestore.getInstance();

        // Intentből match ID kinyerése
        String matchId = getIntent().getStringExtra(EXTRA_MATCH_ID);
        if (matchId != null && !matchId.isEmpty()) {
            loadMatchDetails(matchId);
        } else {
            tvResult.setText("Nem találom a meccs azonosítóját.");
        }

        toolbar.setNavigationOnClickListener(v -> {finish();});
    }

    // A meccs részleteinek lekérése Firestore-ból
    private void loadMatchDetails(String matchId) {
        db.collection("matches")
                .document(matchId)
                .get()
                .addOnSuccessListener(this::populateUi)
                .addOnFailureListener(e -> {
                    Log.e("MatchDetailActivity", "Hiba a meccs lekérdezésekor", e);
                    tvResult.setText("Hiba történt az adatok betöltésekor.");
                });
    }

    @SuppressWarnings("unchecked")
    private void populateUi(DocumentSnapshot doc) {
        if (!doc.exists()) {
            tvResult.setText("Nem találom a meccs adatát.");
            return;
        }

        // Eredmény kirakása
        String home  = doc.getString("homeTeamName");
        String away  = doc.getString("awayTeamName");
        String score = doc.getString("score");
        tvResult.setText(String.format("%s – %s   %s", home, away, score));

        // Gólszerzők szétválogatása
        List<Scorer> homeList = new ArrayList<>();
        List<Scorer> awayList = new ArrayList<>();

        List<Map<String,Object>> raw =
                (List<Map<String,Object>>) doc.get("golszerzok");
        if (raw != null) {
            for (Map<String,Object> m : raw) {
                String team  = (String) m.get("team");
                String name  = (String) m.get("name");
                int    goals = ((Long) m.get("goals")).intValue();
                String pid   = (String) m.get("pid");

                Scorer s = new Scorer(name, goals, pid, team);
                if (team.equals(home)) {
                    homeList.add(s);
                } else if (team.equals(away)) {
                    awayList.add(s);
                }
            }
        }

        homeList.sort((s1, s2) -> Integer.compare(s2.getGoals(), s1.getGoals()));
        awayList.sort((s1, s2) -> Integer.compare(s2.getGoals(), s1.getGoals()));

        // Adapterek beállítása
        rvHomeScorers.setAdapter(new ScorerAdapter(homeList));
        rvAwayScorers.setAdapter(new ScorerAdapter(awayList));
    }
}
