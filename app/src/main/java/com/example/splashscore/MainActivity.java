package com.example.splashscore;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;  // <- ide kell
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final int SECRET_KEY = 544;
    private static final int REQ_NOTIF = 100;

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
}
