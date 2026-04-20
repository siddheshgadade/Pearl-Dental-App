package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Patient_Appoiments extends AppCompatActivity {

    private RecyclerView rvApprovedAppointments;
    private PatientAppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;
    private EditText etPatientName, etCondition, etAppointmentDetails;
    private CalendarView cvAppointmentDate;
    private Button btnApplyForAppointment;
    private FirebaseFirestore db;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appoiments);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(Patient_Appoiments.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        rvApprovedAppointments = findViewById(R.id.rvApprovedAppointments);
        etPatientName = findViewById(R.id.etPatientName);
        etCondition = findViewById(R.id.etCondition);
        etAppointmentDetails = findViewById(R.id.etAppointmentDetails);
        cvAppointmentDate = findViewById(R.id.cvAppointmentDate);
        btnApplyForAppointment = findViewById(R.id.btnApplyForAppointment);

        rvApprovedAppointments.setLayoutManager(new LinearLayoutManager(this));
        appointmentList = new ArrayList<>();
        appointmentAdapter = new PatientAppointmentAdapter(appointmentList);
        rvApprovedAppointments.setAdapter(appointmentAdapter);

        db = FirebaseFirestore.getInstance();

        // Initialize selectedDate with the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());

        // CalendarView date selection
        cvAppointmentDate.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        loadApprovedAppointments();
        setupAppointmentApplication();
    }

    private void loadApprovedAppointments() {
        db.collection("Appointments")
                .whereEqualTo("approved", true)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(Patient_Appoiments.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    appointmentList.clear();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot document : snapshots) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointmentList.add(appointment);
                        }
                    }
                    appointmentAdapter.notifyDataSetChanged();
                });
    }

    private void setupAppointmentApplication() {
        btnApplyForAppointment.setOnClickListener(v -> applyForAppointment());
    }

    private void applyForAppointment() {
        String patientName = etPatientName.getText().toString().trim();
        String condition = etCondition.getText().toString().trim();
        String details = etAppointmentDetails.getText().toString().trim();

        if (patientName.isEmpty() || condition.isEmpty() || details.isEmpty()) {
            Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            return;
        }

        String appointmentId = db.collection("Appointments").document().getId();
        Appointment appointment = new Appointment(appointmentId, patientName, condition, selectedDate, details, false, false);
        Log.d("Patient_Appoiments", "Applying for appointment with ID: " + appointmentId);

        db.collection("Appointments").document(appointmentId).set(appointment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Patient_Appoiments", "Appointment applied successfully");
                        Toast.makeText(Patient_Appoiments.this, "Appointment applied successfully", Toast.LENGTH_SHORT).show();
                        clearForm();
                    } else {
                        Log.e("Patient_Appoiments", "Failed to apply for appointment", task.getException());
                        Toast.makeText(Patient_Appoiments.this, "Failed to apply for appointment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearForm() {
        etPatientName.setText("");
        etCondition.setText("");
        etAppointmentDetails.setText("");

        // Reset selectedDate to the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date());
        cvAppointmentDate.setDate(new Date().getTime());
    }
}
