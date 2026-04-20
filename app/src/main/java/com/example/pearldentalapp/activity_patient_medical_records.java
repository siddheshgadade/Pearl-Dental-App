package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_patient_medical_records extends AppCompatActivity {

    private TextView tvName, tvContactDetails, tvMedicalHistory, tvAllergies, tvMedications, tvLabResults, tvPrescriptions;
    private FirebaseFirestore db;
    private String userEmail; // Patient's email from authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_medical_records);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(activity_patient_medical_records.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize UI components
        tvName = findViewById(R.id.tvName);
        tvContactDetails = findViewById(R.id.tvContactDetails);
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvMedications = findViewById(R.id.tvMedications);
        tvLabResults = findViewById(R.id.tvLabResults);
        tvPrescriptions = findViewById(R.id.tvPrescriptions);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the authenticated user's email
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail != null) {
            fetchPatientMedicalRecords(userEmail);
        } else {
            Toast.makeText(this, "Error: No authenticated user found.", Toast.LENGTH_LONG).show();
        }
    }

    private void fetchPatientMedicalRecords(String email) {
        // Fetch patient records directly using email as document ID
        db.collection("Patients").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Debugging: Print document data
                        Toast.makeText(this, "Record Found: " + documentSnapshot.getData(), Toast.LENGTH_LONG).show();

                        // Update UI with medical record data
                        tvName.setText("Name: " + documentSnapshot.getString("name"));
                        tvContactDetails.setText("Contact: " + documentSnapshot.getString("contact"));
                        tvMedicalHistory.setText("Medical History: " + documentSnapshot.getString("medicalHistory"));
                        tvAllergies.setText("Allergies: " + documentSnapshot.getString("allergies"));
                        tvMedications.setText("Medications: " + documentSnapshot.getString("medications"));
                        tvLabResults.setText("Lab Results: " + documentSnapshot.getString("labResults"));
                        tvPrescriptions.setText("Prescriptions: " + documentSnapshot.getString("prescriptions"));
                    } else {
                        Toast.makeText(this, "No medical record found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading medical record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
