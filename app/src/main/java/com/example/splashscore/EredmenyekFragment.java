package com.example.splashscore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EredmenyekFragment extends Fragment {
    private MatchScores_RecyclerViewAdapter adapter;
    private ArrayList<MatchScoreModel> matchScoreModels = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate a fragment layout-ot
        return inflater.inflate(R.layout.fragment_eredmenyek, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.scoresRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MatchScores_RecyclerViewAdapter(matchScoreModels);

        adapter.setOnItemClickListener(match -> {
            Intent i = new Intent(requireContext(), MatchDetailActivity.class);
            i.putExtra(MatchDetailActivity.EXTRA_MATCH_ID, match.getId());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);

        fetchMatches();
    }

    private void fetchMatches() {
        db.collection("matches")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    matchScoreModels.clear();            // ne duplikálódjon
                    for (QueryDocumentSnapshot doc : snapshots) {
                        String id  = doc.getId();
                        String h  = doc.getString("homeTeamName");
                        String a  = doc.getString("awayTeamName");
                        String q  = doc.getString("quarters");
                        String s  = doc.getString("score");
                        if (h!=null && a!=null && q!=null && s!=null) {
                            matchScoreModels.add(
                                    new MatchScoreModel(id, h, a, q, s)
                            );
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("EredmenyekFragment", "Error fetching matches", e);
                });
    }
}
