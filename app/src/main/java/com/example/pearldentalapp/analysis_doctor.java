package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class analysis_doctor extends AppCompatActivity {
    private FirebaseFirestore db;
    private BarChart barChart;
    private PieChart pieChart;
    private TextView barChartDescription, pieChartDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_doctor);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(analysis_doctor.this,UserProfileActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize UI components
        db = FirebaseFirestore.getInstance();
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        barChartDescription = findViewById(R.id.barChartDescription);
        pieChartDescription = findViewById(R.id.pieChartDescription);

        loadAppointmentData();
        loadPaymentData();
    }

    private void loadAppointmentData() {
        db.collection("Appointments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                HashMap<String, Integer> doctorAppointments = new HashMap<>();

                for (DocumentSnapshot doc : documents) {
                    String doctorId = doc.getString("doctor_id");
                    doctorAppointments.put(doctorId, doctorAppointments.getOrDefault(doctorId, 0) + 1);
                }

                showBarChart(doctorAppointments);
            } else {
                Log.e("Firestore", "Error fetching appointments", task.getException());
            }
        });
    }

    private void loadPaymentData() {
        db.collection("payments").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot documents = task.getResult();
                int completed = 0, pending = 0;

                for (DocumentSnapshot doc : documents) {
                    String status = doc.getString("status");
                    if (status != null && status.equalsIgnoreCase("completed")) {
                        completed++;
                    } else {
                        pending++;
                    }
                }

                Log.d("Firestore", "Completed Payments: " + completed + ", Pending Payments: " + pending);
                showPieChart(completed, pending);
            } else {
                Log.e("Firestore", "Error fetching payments", task.getException());
            }
        });
    }

    private void showBarChart(HashMap<String, Integer> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            entries.add(new BarEntry(index++, entry.getValue()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Appointments per Doctor");
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        Description description = new Description();
        description.setText("Doctor Workload Analysis");
        barChart.setDescription(description);
        barChart.invalidate();

        // Update Explanation Text
        barChartDescription.setText("This chart represents the number of appointments handled by each doctor.");
    }

    private void showPieChart(int completed, int pending) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed Payments"));
        entries.add(new PieEntry(pending, "Pending Payments"));

        PieDataSet dataSet = new PieDataSet(entries, "Payment Status");
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        Description description = new Description();
        description.setText("Payment Status Analysis");
        pieChart.setDescription(description);
        pieChart.invalidate();

        // Update Explanation Text
        pieChartDescription.setText("This chart shows the ratio of completed vs. pending payments.");
    }
}
