package com.example.splashscore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfilFragment extends Fragment {

    private static final int REQ_STORAGE = 200;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView profileAvatar;
    private TextView profileEmailText, profileNameText;
    private MaterialButton signOutButton;

    // Ez kell a korszerű ActivityResult API-hoz
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        profileAvatar    = view.findViewById(R.id.profileAvatar);
        profileEmailText = view.findViewById(R.id.profileEmailText);
        profileNameText  = view.findViewById(R.id.profileNameText);
        signOutButton    = view.findViewById(R.id.signOutButton);

        // 2) ActivityResultLauncher regisztrálása
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null
                            && result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        uploadAvatar(uri);
                    }
                }
        );

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            profileEmailText.setText(user.getEmail());
            // Firestore-ból olvassuk a “displayName” és “avatarUrl” mezőket
            db.collection("profiles")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String email = doc.getString("email");
                            String avatarUrl = doc.getString("avatarUrl");
                            if (email != null)    profileNameText.setText(email);
                            if (avatarUrl != null) {
                                // pl. Glide vagy Picasso használatával
                                Glide.with(this)
                                        .load(avatarUrl)
                                        .circleCrop()
                                        .into(profileAvatar);
                            }
                        }
                    });
        }

        // 4) Klikk az avatáron: indítjuk a választót
        profileAvatar.setOnClickListener(v -> {
            openImagePicker();
        });

        // 5) Kijelentkezés
        signOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(requireActivity(), LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            requireActivity().finish();
        });

        view.findViewById(R.id.btnHotline)
                .setOnClickListener(v -> makeCall());
    }

    private void openImagePicker() {
        // galéria
        Intent pick = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pick.setType("image/*");
        pickImageLauncher.launch(pick);
    }

    private void uploadAvatar(Uri uri) {
        // Storage ref: avatars/{uid}.jpg
        String uid = mAuth.getCurrentUser().getUid();
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("avatars/" + uid + ".jpg");

        // small placeholder betöltés
        Glide.with(this).load(uri).circleCrop().into(profileAvatar);

        // upload
        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // Firestore update
                    db.collection("profiles")
                            .document(uid)
                            .update("avatarUrl", downloadUri.toString())
                            .addOnSuccessListener(__ -> {
                                Toast.makeText(requireContext(),
                                        "Profilkép frissítve", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                            "Feltöltés sikertelen: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private static final int RC_CALL = 102;

    private void makeCall() {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:+3612345678"));
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RC_CALL);
            return;
        }
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] res) {
        if (req==RC_CALL && res.length>0 && res[0]==PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            Toast.makeText(requireContext(), "Call permission kell a funkcióhoz", Toast.LENGTH_SHORT).show();
        }
    }

}
