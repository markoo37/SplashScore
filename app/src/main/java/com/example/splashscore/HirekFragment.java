package com.example.splashscore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HirekFragment extends Fragment {

    private Hirek_RecyclerViewAdapter adapter;
    private ArrayList<HirekModel> hirekModels = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration newsListener;
    private String currentEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate a fragment layout-ot
        return inflater.inflate(R.layout.fragment_hirek, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // 1) RecyclerView init
        RecyclerView recyclerView = view.findViewById(R.id.newsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new Hirek_RecyclerViewAdapter(hirekModels, currentEmail, docId -> db.collection("news").document(docId).delete());
        recyclerView.setAdapter(adapter);

        FloatingActionButton addFab = view.findViewById(R.id.addNewsFab);

        // 2) SearchView hookup
        SearchView searchView = view.findViewById(R.id.newsSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        addFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), NewNewsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // 3) Firestore real-time listener rendezve dátum szerint
        newsListener = db.collection("news")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snaps,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("HirekFragment", "News listener hiba", e);
                            return;
                        }
                        List<HirekModel> tmpList = new ArrayList<>();
                        for (DocumentSnapshot doc : snaps.getDocuments()) {
                            String id            = doc.getString("id");
                            String title         = doc.getString("title");
                            String summary       = doc.getString("summary");
                            String date          = doc.getString("date");
                            String uploaderEmail = doc.getString("uploader_email");
                            if (id != null
                                    && title != null
                                    && summary != null
                                    && date != null
                                    && uploaderEmail != null) {
                                tmpList.add(new HirekModel(
                                        id, title, summary, date, uploaderEmail
                                ));
                            }
                        }
                        // Adapter belső listáinak frissítése + újra szűrés
                        adapter.updateData(tmpList);
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        // 4) Ne hagyjuk futni, ha nem látjuk
        if (newsListener != null) {
            newsListener.remove();
            newsListener = null;
        }
    }
}
