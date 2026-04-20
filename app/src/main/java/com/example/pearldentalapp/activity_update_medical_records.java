package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class activity_update_medical_records extends AppCompatActivity {

    private SearchView searchView;
    private TextView tvPatientName, tvPatientEmail;
    private EditText etMedicalHistory, etAllergies, etMedications, etLabResults, etPrescriptions;
    private Button btnUpdateRecords;
    private FirebaseFirestore db;
    private String selectedPatientId = null;
    private String selectedPatientName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_medical_records);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(activity_update_medical_records.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize UI components
        searchView = findViewById(R.id.searchView);
        tvPatientName = findViewById(R.id.tvPatientName);
        tvPatientEmail = findViewById(R.id.tvPatientEmail);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        etAllergies = findViewById(R.id.etAllergies);
        etMedications = findViewById(R.id.etMedications);
        etLabResults = findViewById(R.id.etLabResults);
        etPrescriptions = findViewById(R.id.etPrescriptions);
        btnUpdateRecords = findViewById(R.id.btnUpdateRecords);

        db = FirebaseFirestore.getInstance();

        // Search patient by email
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPatientByEmail(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Update records on button click
        btnUpdateRecords.setOnClickListener(v -> updateMedicalRecords());
    }

    // 🔍 Search for patient in 'users' collection
    private void searchPatientByEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            selectedPatientName = document.getString("name");
                            tvPatientName.setText(selectedPatientName);
                            tvPatientEmail.setText(email);
                            searchMedicalRecords(selectedPatientName, email);
                            return;
                        }
                    } else {
                        Toast.makeText(this, "Patient not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching patient", Toast.LENGTH_SHORT).show());
    }

    // 🔍 Search for medical records
    private void searchMedicalRecords(String patientName, String email) {
        db.collection("Patients")
                .whereEqualTo("name", patientName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // ✅ If record exists, load the data
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            selectedPatientId = document.getId();
                            etMedicalHistory.setText(document.getString("medicalHistory"));
                            etAllergies.setText(document.getString("allergies"));
                            etMedications.setText(document.getString("medications"));
                            etLabResults.setText(document.getString("labResults"));
                            etPrescriptions.setText(document.getString("prescriptions"));
                            return;
                        }
                    } else {
                        // ❌ No record found → Create a new one
                        createNewMedicalRecord(patientName, email);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching records", Toast.LENGTH_SHORT).show());
    }

    // 🆕 Create a new medical record if it doesn't exist
    private void createNewMedicalRecord(String patientName, String email) {
        selectedPatientId = email; // Use email as unique ID for the document

        Map<String, Object> newRecord = new HashMap<>();
        newRecord.put("name", patientName);
        newRecord.put("email", email);
        newRecord.put("medicalHistory", "");
        newRecord.put("allergies", "");
        newRecord.put("medications", "");
        newRecord.put("labResults", "");
        newRecord.put("prescriptions", "");

        db.collection("Patients").document(selectedPatientId)
                .set(newRecord)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "New medical record created!", Toast.LENGTH_SHORT).show();
                    etMedicalHistory.setText("");
                    etAllergies.setText("");
                    etMedications.setText("");
                    etLabResults.setText("");
                    etPrescriptions.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error creating medical record", Toast.LENGTH_SHORT).show());
    }

    // ✏️ Update medical records
    private void updateMedicalRecords() {
        if (selectedPatientId == null) {
            Toast.makeText(this, "Please select a patient first", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = db.collection("Patients").document(selectedPatientId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("medicalHistory", etMedicalHistory.getText().toString());
        updates.put("allergies", etAllergies.getText().toString());
        updates.put("medications", etMedications.getText().toString());
        updates.put("labResults", etLabResults.getText().toString());
        updates.put("prescriptions", etPrescriptions.getText().toString());

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(activity_update_medical_records.this, "Records updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity_update_medical_records.this, "Failed to update records", Toast.LENGTH_SHORT).show());
    }
}
