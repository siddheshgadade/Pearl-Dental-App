package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class activity_doctor_medical_records extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView rvPatientRecords;
    private PatientRecordsAdapter adapter;
    private List<PatientRecord> patientRecordsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_medical_records);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(activity_doctor_medical_records.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize UI components
        searchView = findViewById(R.id.searchView);
        rvPatientRecords = findViewById(R.id.rvPatientRecords);

        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        rvPatientRecords.setLayoutManager(new LinearLayoutManager(this));
        patientRecordsList = new ArrayList<>();
        adapter = new PatientRecordsAdapter(patientRecordsList);
        rvPatientRecords.setAdapter(adapter);

        // Set search listener
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
    }

    // Search patient records by email
    private void searchPatientByEmail(String email) {
        db.collection("Patients")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        patientRecordsList.clear();

                        // Create a new PatientRecord object
                        PatientRecord patient = new PatientRecord(
                                documentSnapshot.getString("name"),
                                documentSnapshot.getString("contact"),
                                documentSnapshot.getString("medicalHistory"),
                                documentSnapshot.getString("allergies"),
                                documentSnapshot.getString("medications"),
                                documentSnapshot.getString("labResults"),
                                documentSnapshot.getString("prescriptions")
                        );

                        patientRecordsList.add(patient);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No medical record found for this patient.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error fetching records: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
