package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";

    private TextView tvRole, tvChangePhoto, tvProfileTitle;
    private EditText etName, etEmail;
    private ImageView ivProfilePicture;
    private Button btnUpdateProfile, btnLogout, btnDoctorFeature;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI components
        tvProfileTitle = findViewById(R.id.tvProfileTitle);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        tvRole = findViewById(R.id.tvRole);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);
        btnDoctorFeature = findViewById(R.id.btnDoctorFeature); // 🔹 New button

        // Initially hide doctor button
        btnDoctorFeature.setVisibility(View.GONE);

        // Load user data
        loadUserProfile();

        // Logout button
        btnLogout.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(UserProfileActivity.this, loginactivity.class));
            finish();
        });

        // Update Profile
        btnUpdateProfile.setOnClickListener(view -> updateUserProfile());

        // Change Profile Picture (Not Implemented)
        tvChangePhoto.setOnClickListener(view -> {
            Toast.makeText(this, "Feature not implemented yet!", Toast.LENGTH_SHORT).show();
        });

        // Doctor Button Action
        btnDoctorFeature.setOnClickListener(view -> {
            Toast.makeText(this, "Doctor Feature Clicked!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, analysis_doctor.class)); // Replace with actual activity
        });
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e(TAG, "User not authenticated");
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, loginactivity.class));
            finish();
            return;
        }

        String userId = user.getUid();
        Log.d(TAG, "Fetching user profile for: " + userId);

        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "User profile data found!");

                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role"); // 🔹 Fetch role
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");

                        if (name != null) etName.setText(name);
                        if (email != null) etEmail.setText(email);

                        if (role != null) {
                            Log.d(TAG, "User Role: " + role);
                            tvRole.setText(role);

                            // Show doctor button if the role is "doctor"
                            if (role.equalsIgnoreCase("doctor")) {
                                btnDoctorFeature.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.e(TAG, "Role field is missing in Firestore!");
                            tvRole.setText("Role not found");
                        }

                        // Load profile picture
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Picasso.get().load(profilePictureUrl).placeholder(R.drawable.user).into(ivProfilePicture);
                        }

                    } else {
                        Log.e(TAG, "User data not found in Firestore");
                        Toast.makeText(UserProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error loading profile", e);
                    Toast.makeText(UserProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUserProfile() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String updatedName = etName.getText().toString().trim();
        String updatedEmail = etEmail.getText().toString().trim();
        String currentEmail = user.getEmail();

        if (updatedName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (updatedEmail.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (!updatedEmail.equals(currentEmail)) {
            Log.d(TAG, "Email change detected. Re-authenticating...");

            AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, "user_password");  // Replace with actual password logic

            user.reauthenticate(credential)
                    .addOnSuccessListener(unused -> {
                        user.updateEmail(updatedEmail)
                                .addOnSuccessListener(unused1 -> {
                                    Log.d(TAG, "Email updated in Firebase Auth");
                                    updateFirestoreData(userId, updatedName, updatedEmail);
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Log.e(TAG, "Failed to update email in Firebase Auth", e);
                                    Toast.makeText(UserProfileActivity.this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Re-authentication failed", e);
                        Toast.makeText(UserProfileActivity.this, "Re-authentication failed. Please log in again.", Toast.LENGTH_SHORT).show();
                    });

        } else {
            updateFirestoreData(userId, updatedName, updatedEmail);
        }
    }

    private void updateFirestoreData(String userId, String updatedName, String updatedEmail) {
        db.collection("users").document(userId)
                .update("name", updatedName, "email", updatedEmail)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Failed to update Firestore", e);
                    Toast.makeText(UserProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
