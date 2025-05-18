package com.example.splashscore;

import android.Manifest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;  // <- ide kell
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final int SECRET_KEY = 544;
    private static final int RC_NOTIFY = 123;
    private ListenerRegistration newsListener;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Secret key ellenőrzése
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if (secret_key != SECRET_KEY) {
            finish();
            return;
        }
        requestNotificationPermissionIfNeeded();

        newsListener = FirebaseFirestore.getInstance()
                .collection("news")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null || snap.isEmpty()) return;
                    // 2) Ha új dokumentum érkezett:
                    DocumentSnapshot doc = snap.getDocuments().get(0);
                    String title = doc.getString("title");
                    String summary = doc.getString("summary");
                    sendLocalNotification(title, summary);
                });


        // FirebaseAuth init
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // UI init
        //currentUsTextView = findViewById(R.id.currentUsTextView);
        //logoutButton       = findViewById(R.id.logoutButton);

        // Bottom nav & alapértelmezett fragment
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, new EredmenyekFragment())
                    .commit();
        }
        // Navigáció kezelése if–else-sel
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment selected = null;
                int id = item.getItemId();
                if (id == R.id.nav_eredmenyek) {
                    selected = new EredmenyekFragment();
                } else if (id == R.id.nav_hirek) {
                    selected = new HirekFragment();
                } else if (id == R.id.nav_statisztikak) {
                    selected = new StatisztikakFragment();
                } else if (id == R.id.nav_profil) {
                    selected = new ProfilFragment();
                }

                if (selected != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.nav_host_fragment, selected)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    private void requestNotificationPermissionIfNeeded() {
        // Csak Android 13-tól kell, alatta automatikusan engedélyezve van
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Ha indoklás kellene (pl. már korábban elutasította)
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Értesítések engedélyezése")
                            .setMessage("Az app push-értesítéseket küldhet, ha engedélyezed.")
                            .setPositiveButton("Rendben", (d, w) -> {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                                        RC_NOTIFY);
                            })
                            .setNegativeButton("Mégse", null)
                            .show();
                } else {
                    // Közvetlenül kérjük
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                            RC_NOTIFY);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_NOTIFY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Értesítések engedélyezve", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Értesítések tiltva", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsListener != null) newsListener.remove();
    }

    private void sendLocalNotification(String title, String body) {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        String CHANNEL_ID = "news_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "Hírek", NotificationManager.IMPORTANCE_HIGH)
            );
        }
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_news)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(PendingIntent.getActivity(
                        this, 0, new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_IMMUTABLE
                ));
        nm.notify((int)System.currentTimeMillis(), nb.build());
    }
}
