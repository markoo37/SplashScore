package com.example.splashscore;

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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisztikakFragment extends Fragment {
    private FirebaseFirestore db;
    private ListenerRegistration standingsListener;

    private List<TeamStat> standings      = new ArrayList<>();
    private List<PlayerStat> topScorers   = new ArrayList<>();

    // az új listák
    private List<PlayerStat> youngest     = new ArrayList<>();
    private List<PlayerStat> oldest       = new ArrayList<>();
    private List<PlayerStat> heaviest     = new ArrayList<>();

    // RecyclerView + Adapter mezők…
    private RecyclerView standingsRv;
    private RecyclerView scorersRv;
    private RecyclerView youngestRv;
    private RecyclerView oldestRv;
    private RecyclerView heaviestRv;

    private StandingsAdapter standingsAdapter;
    private TopScorersAdapter scorersAdapter;
    private TopScorersAdapter youngestAdapter;
    private TopScorersAdapter oldestAdapter;
    private WeightScorersAdapter heaviestAdapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statisztikak, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // Standings RecyclerView init
        standingsRv = v.findViewById(R.id.standingsRecyclerView);
        standingsRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        standingsAdapter = new StandingsAdapter(standings);
        standingsRv.setAdapter(standingsAdapter);

        // TopScorers RecyclerView init
        scorersRv = v.findViewById(R.id.topScorersRecyclerView);
        scorersRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        scorersAdapter = new TopScorersAdapter(topScorers);
        scorersRv.setAdapter(scorersAdapter);

        // --- új lista init ---
        youngestRv = v.findViewById(R.id.rvYoungest);
        youngestRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        youngestAdapter = new TopScorersAdapter(youngest);
        youngestRv.setAdapter(youngestAdapter);

        oldestRv = v.findViewById(R.id.rvOldest);
        oldestRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        oldestAdapter = new TopScorersAdapter(oldest);
        oldestRv.setAdapter(oldestAdapter);

        heaviestRv = v.findViewById(R.id.rvHeaviest);
        heaviestRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        heaviestAdapter = new WeightScorersAdapter(heaviest);
        heaviestRv.setAdapter(heaviestAdapter);

        loadStandings();
        loadTopScorersFromPlayers();
        loadYoungest();
        loadOldest();
        loadHeaviest();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (standingsListener != null) {
            standingsListener.remove();
            standingsListener = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        standingsListener = db.collection("teams")
                .addSnapshotListener((snaps, e) -> {
                    if (e != null) {
                        Log.e("StatsFrag", "Standings listener hiba", e);
                        return;
                    }
                    standings.clear();
                    for (DocumentSnapshot doc : snaps.getDocuments()) {
                        String id    = doc.getId();
                        String name  = doc.getString("name");
                        int w        = doc.getLong("wins").intValue();
                        int d        = doc.getLong("draws").intValue();
                        int l        = doc.getLong("losses").intValue();
                        int p        = doc.getLong("points").intValue();
                        standings.add(new TeamStat(id, name, w, d, l, p));
                    }
                    // pont szerint csökkenő
                    Collections.sort(standings,
                            (a, b) -> Integer.compare(b.getPoints(), a.getPoints()));
                    standingsAdapter.notifyDataSetChanged();
                });
    }

    private void loadStandings() {
        db.collection("teams")
                .get()
                .addOnSuccessListener(snaps -> {
                    standings.clear();
                    for (DocumentSnapshot doc : snaps) {
                        String teamId = doc.getId();
                        String name   = doc.getString("name");
                        int wins       = doc.getLong("wins").intValue();
                        int draws      = doc.getLong("draws").intValue();
                        int losses     = doc.getLong("losses").intValue();
                        int points     = doc.getLong("points").intValue();
                        standings.add(new TeamStat(teamId, name, wins, draws, losses, points));
                    }
                    // pont szerint csökkenő
                    Collections.sort(standings,
                            (a, b) -> Integer.compare(b.getPoints(), a.getPoints()));
                    standingsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("StatisztikakFragment", "Teams betöltése sikertelen", e));
    }

    private void loadTopScorersFromPlayers() {
        db.collection("teams")
                .get()
                .addOnSuccessListener(teamSnaps -> {
                    List<DocumentSnapshot> teams = teamSnaps.getDocuments();
                    List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                    // párhuzamos listában jegyezd a teamDoks-okat
                    for (DocumentSnapshot teamDoc : teams) {
                        tasks.add(db.collection("teams")
                                .document(teamDoc.getId())
                                .collection("players")
                                .get());
                    }
                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                topScorers.clear();
                                for (int i = 0; i < results.size(); i++) {
                                    QuerySnapshot qs = (QuerySnapshot) results.get(i);
                                    String  teamName = teams.get(i).getString("name");
                                    for (DocumentSnapshot doc : qs.getDocuments()) {
                                        String pid   = doc.getString("pid");
                                        String name  = doc.getString("name");
                                        int goals     = doc.getLong("goals").intValue();
                                        int age       = doc.getLong("age").intValue();
                                        int weight       = doc.getLong("weight").intValue();
                                        topScorers.add(new PlayerStat(pid, name, teamName, goals, age, weight));
                                    }
                                }
                                // sorrend és Top50
                                Collections.sort(topScorers,
                                        (a, b) -> Integer.compare(b.getGoals(), a.getGoals()));
                                if (topScorers.size() > 50) {
                                    List<PlayerStat> limited = new ArrayList<>(topScorers.subList(0, 50));
                                    topScorers.clear();
                                    topScorers.addAll(limited);
                                }
                                scorersAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("StatsFrag", "Players betöltése sikertelen", e));
                })
                .addOnFailureListener(e ->
                        Log.e("StatsFrag", "Teams betöltése sikertelen", e));
    }

    // 1) Legfiatalabbak (minimum 1 gól, kor szerint ↑, top 10)
    private void loadYoungest() {
        // 1) lekérdezzük a top 10 legalacsonyabb életkorú, legalább 1 gólos játékost
        db.collectionGroup("players")
                .whereGreaterThan("goals", 0)
                .orderBy("age",   Query.Direction.ASCENDING)
                .orderBy("goals", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(qs -> {
                    List<DocumentSnapshot> playerDocs = qs.getDocuments();

                    // 2) kigyűjtjük az egyedi teamRef-eket
                    Map<DocumentReference,Void> teamRefs = new HashMap<>();
                    for (DocumentSnapshot pd : playerDocs) {
                        // a playerDoc útvonala: teams/{teamId}/players/{playerId}
                        DocumentReference teamRef =
                                pd.getReference()           // → teams/{teamId}/players/{playerId}
                                        .getParent()             // → teams/{teamId}/players
                                        .getParent();            // → teams/{teamId}
                        teamRefs.put(teamRef, null);
                    }

                    // 3) párhuzamosan lekérjük az összes csapat-doksi nevét
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference tref : teamRefs.keySet()) {
                        tasks.add(tref.get());
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(teamDocs -> {
                                // 4) map teamId → teamName
                                Map<String,String> idToName = new HashMap<>();
                                for (Object o : teamDocs) {
                                    DocumentSnapshot td = (DocumentSnapshot) o;
                                    idToName.put(td.getId(), td.getString("name"));
                                }

                                // 5) végül készítsük el a PlayerStat listát is csapatnévvel
                                youngest.clear();
                                for (DocumentSnapshot pd : playerDocs) {
                                    String teamId   = pd.getReference()
                                            .getParent()
                                            .getParent()
                                            .getId();
                                    String teamName = idToName.get(teamId);
                                    youngest.add(toPlayerStat(pd, teamName));
                                }
                                youngestAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("StatsFrag", "Team lekérés hiba", e));
                })
                .addOnFailureListener(e ->
                        Log.e("StatsFrag", "Youngest betöltés hiba", e));
    }

    // 2) Legidősebbek (minimum 1 gól, kor szerint ↓, top 10)
    private void loadOldest() {
        // 1) lekérdezés: legalább 1 gólosak, gólszám ASC (inequality + orderBy szabály),
        //    életkor DESC, limit 10
        db.collectionGroup("players")
                .whereGreaterThan("goals", 0)
                .orderBy("age",   Query.Direction.DESCENDING)
                .orderBy("goals", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(qs -> {
                    List<DocumentSnapshot> playerDocs = qs.getDocuments();

                    // 2) kigyűjtjük az egyedi teamRef-eket
                    Map<DocumentReference,Void> teamRefs = new HashMap<>();
                    for (DocumentSnapshot pd : playerDocs) {
                        DocumentReference teamRef =
                                pd.getReference().getParent().getParent();
                        teamRefs.put(teamRef, null);
                    }

                    // 3) párhuzamos teamDoc lekérések
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference tref : teamRefs.keySet()) {
                        tasks.add(tref.get());
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(teamDocs -> {
                                // 4) teamId → teamName map
                                Map<String,String> idToName = new HashMap<>();
                                for (Object o : teamDocs) {
                                    DocumentSnapshot td = (DocumentSnapshot) o;
                                    idToName.put(td.getId(), td.getString("name"));
                                }

                                // 5) feltöltjük az oldest listát
                                oldest.clear();
                                for (DocumentSnapshot pd : playerDocs) {
                                    String teamId   = pd.getReference()
                                            .getParent()
                                            .getParent()
                                            .getId();
                                    String teamName = idToName.get(teamId);
                                    oldest.add(toPlayerStat(pd, teamName));
                                }
                                oldestAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("StatsFrag", "Team lekérés hiba", e));
                })
                .addOnFailureListener(e ->
                        Log.e("StatsFrag", "Oldest betöltés hiba", e));
    }


    // 3) Legnehezebbek (minimum 1 gól, testsúly szerint ↓, top 10)
    private void loadHeaviest() {
        db.collectionGroup("players")
                .whereGreaterThan("goals", 0)
                .orderBy("weight", Query.Direction.DESCENDING)
                .orderBy("goals", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(qs -> {
                    List<DocumentSnapshot> playerDocs = qs.getDocuments();

                    // 2) Egyedi teamRef-ek kigyűjtése
                    Map<DocumentReference,Void> teamRefs = new HashMap<>();
                    for (DocumentSnapshot pd : playerDocs) {
                        DocumentReference teamRef = pd.getReference()   // → teams/{teamId}/players/{playerId}
                                .getParent()      // → teams/{teamId}/players
                                .getParent();     // → teams/{teamId}
                        teamRefs.put(teamRef, null);
                    }

                    // 3) Párhuzamos csapat‐dokumentum lekérések
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (DocumentReference tref : teamRefs.keySet()) {
                        tasks.add(tref.get());
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(teamDocs -> {
                                // 4) teamId → teamName map építése
                                Map<String,String> idToName = new HashMap<>();
                                for (Object o : teamDocs) {
                                    DocumentSnapshot td = (DocumentSnapshot) o;
                                    idToName.put(td.getId(), td.getString("name"));
                                }

                                // 5) PlayerStat lista feltöltése a csapatnévvel
                                heaviest.clear();
                                for (DocumentSnapshot pd : playerDocs) {
                                    String teamId   = pd.getReference()
                                            .getParent()
                                            .getParent()
                                            .getId();
                                    String teamName = idToName.get(teamId);
                                    heaviest.add(toPlayerStat(pd, teamName));
                                }
                                heaviestAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("StatsFrag", "Team lekérés hiba", e));
                })
                .addOnFailureListener(e ->
                        Log.e("StatsFrag", "Heaviest betöltés hiba", e));
    }


    /** Segédfüggvény: DocumentSnapshot → PlayerStat */
    private PlayerStat toPlayerStat(DocumentSnapshot d, String teamName) {
        String pid    = d.getString("pid");
        String name   = d.getString("name");
        int    goals  = d.getLong("goals").intValue();
        int    age    = d.getLong("age").intValue();
        int    weight = d.getLong("weight").intValue();
        return new PlayerStat(pid, name, teamName, goals, age, weight);
    }
}
