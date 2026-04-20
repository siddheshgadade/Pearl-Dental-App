package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.Nullable;

public class DoctorStaff_appoimentActivity extends AppCompatActivity implements DoctorStaffAppointmentAdapter.OnAppointmentActionListener {

    private RecyclerView rvAppointmentRequests, rvUpcomingAppointments, rvCompletedAppointments;
    private DoctorStaffAppointmentAdapter appointmentRequestAdapter, upcomingAppointmentAdapter, completedAppointmentAdapter;
    private List<Appointment> appointmentRequestList, upcomingAppointmentList, completedAppointmentList;
    private FirebaseFirestore db;
    private CollectionReference appointmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_staff_appoiment);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(DoctorStaff_appoimentActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        rvAppointmentRequests = findViewById(R.id.rvAppointmentRequests);
        rvUpcomingAppointments = findViewById(R.id.rvUpcomingAppointments);
        rvCompletedAppointments = findViewById(R.id.rvCompletedAppointments);

        rvAppointmentRequests.setLayoutManager(new LinearLayoutManager(this));
        rvUpcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        rvCompletedAppointments.setLayoutManager(new LinearLayoutManager(this));

        appointmentRequestList = new ArrayList<>();
        upcomingAppointmentList = new ArrayList<>();
        completedAppointmentList = new ArrayList<>();

        appointmentRequestAdapter = new DoctorStaffAppointmentAdapter(appointmentRequestList, this);
        upcomingAppointmentAdapter = new DoctorStaffAppointmentAdapter(upcomingAppointmentList, this);
        completedAppointmentAdapter = new DoctorStaffAppointmentAdapter(completedAppointmentList, this);

        rvAppointmentRequests.setAdapter(appointmentRequestAdapter);
        rvUpcomingAppointments.setAdapter(upcomingAppointmentAdapter);
        rvCompletedAppointments.setAdapter(completedAppointmentAdapter);

        db = FirebaseFirestore.getInstance();
        appointmentRef = db.collection("Appointments");

        loadAppointmentRequests();
        loadUpcomingAppointments();
        loadCompletedAppointments();
        scheduleAppointmentRemoval();
    }

    @Override
    public void onApproveClick(Appointment appointment) {
        db.collection("Appointments").document(appointment.getId())
                .update("approved", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DoctorStaff_appoimentActivity.this, "Appointment approved", Toast.LENGTH_SHORT).show();
                    loadAppointmentRequests(); // Refresh the list of appointment requests
                    loadUpcomingAppointments(); // Refresh the list of upcoming appointments
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to approve appointment", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onCompleteClick(Appointment appointment) {
        db.collection("Appointments").document(appointment.getId())
                .update("completed", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DoctorStaff_appoimentActivity.this, "Appointment completed", Toast.LENGTH_SHORT).show();
                    loadUpcomingAppointments(); // Refresh the list of upcoming appointments
                    loadCompletedAppointments(); // Refresh the list of completed appointments
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to complete appointment", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAppointmentRequests() {
        Query query = appointmentRef.whereEqualTo("approved", false);
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to load appointment requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                appointmentRequestList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Appointment appointment = doc.toObject(Appointment.class);
                    appointmentRequestList.add(appointment);
                }
                appointmentRequestAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadUpcomingAppointments() {
        Query query = appointmentRef.whereEqualTo("approved", true).whereEqualTo("completed", false);
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to load upcoming appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                upcomingAppointmentList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Appointment appointment = doc.toObject(Appointment.class);
                    upcomingAppointmentList.add(appointment);
                }
                upcomingAppointmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadCompletedAppointments() {
        Query query = appointmentRef.whereEqualTo("completed", true);
        query.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to load completed appointments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (snapshots != null) {
                completedAppointmentList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Appointment appointment = doc.toObject(Appointment.class);
                    completedAppointmentList.add(appointment);
                }
                completedAppointmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void scheduleAppointmentRemoval() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                removeCompletedAppointments();
                handler.postDelayed(this, 24 * 60 * 60 * 1000); // Run daily
            }
        };

        // Calculate delay until next midnight
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillis = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long midnightMillis = calendar.getTimeInMillis();
        long delayMillis = midnightMillis - currentTimeMillis;

        handler.postDelayed(runnable, delayMillis);
    }

    private void removeCompletedAppointments() {
        Query query = appointmentRef.whereEqualTo("completed", true);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    doc.getReference().delete();
                }
            } else {
                Toast.makeText(DoctorStaff_appoimentActivity.this, "Failed to remove completed appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
