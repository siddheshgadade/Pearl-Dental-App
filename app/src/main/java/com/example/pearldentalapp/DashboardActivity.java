package com.example.pearldentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView welcomeTextView;
    private String userRole;
    private MaterialCardView appointmentsCardView, medicalRecordsCardView, notificationsCardView, paymentBillingSummaryCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        welcomeTextView = findViewById(R.id.welcomeTextView);

        appointmentsCardView = findViewById(R.id.appointmentsCardView);
        medicalRecordsCardView = findViewById(R.id.medicalRecordsCardView);
        notificationsCardView = findViewById(R.id.notificationsCardView);
        paymentBillingSummaryCardView = findViewById(R.id.paymentBillingSummaryCardView);

        // Appointments CardView Click Event
        appointmentsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DashboardActivity", "Navigating to appointments with role: " + userRole); // Log the navigation action
                Intent intent;
                if ("Patient".equals(userRole)) {
                    intent = new Intent(DashboardActivity.this, Patient_Appoiments.class);
                } else {
                    intent = new Intent(DashboardActivity.this, DoctorStaff_appoimentActivity.class);
                }
                startActivity(intent);
            }
        });

        // Medical Records CardView Click Event
        medicalRecordsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DashboardActivity", "Navigating to medical records with role: " + userRole);

                Intent intent;
                if ("Patient".equals(userRole)) {
                    intent = new Intent(DashboardActivity.this, activity_patient_medical_records.class);
                } else if ("Doctor".equals(userRole)) {
                    intent = new Intent(DashboardActivity.this, activity_doctor_medical_records.class);
                } else if ("Staff".equals(userRole)) {
                    intent = new Intent(DashboardActivity.this, activity_update_medical_records.class);
                } else {
                    Log.e("DashboardActivity", "Unknown role: " + userRole);
                    return;
                }

                startActivity(intent);
            }
        });

        // Notifications CardView Click Event
        notificationsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, Notificationsactivity.class);
                startActivity(intent);
            }
        });

        // Payment & Billing CardView Click Event
        paymentBillingSummaryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("DashboardActivity", "Navigating to Billing with role: " + userRole); // Log the navigation action
                Intent intent;
                if ("Patient".equals(userRole)) {
                    intent = new Intent(DashboardActivity.this, PatientBillingActivity.class);
                } else {
                    intent = new Intent(DashboardActivity.this, DoctorBillingActivity.class);
                }
                startActivity(intent);
            }
        });

        // Set up bottom navigation view (Updated)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    // Stay on Dashboard (Home)
                    return true;
                } else if (itemId == R.id.navigation_user) {
                    // Navigate to Profile
                    Intent intent = new Intent(DashboardActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Set up notification icon click listener


        loadUserName();
    }

    private void loadUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                userRole = documentSnapshot.getString("role"); // Get user role
                                welcomeTextView.setText("Welcome, " + name + "!");
                                Log.d("DashboardActivity", "User role: " + userRole);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("DashboardActivity", "Error loading user role", e);
                        }
                    });
        }
    }
}
