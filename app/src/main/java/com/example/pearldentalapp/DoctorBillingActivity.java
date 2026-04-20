package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorBillingActivity extends AppCompatActivity {
    private EditText etPatientEmail, etAmount;
    private Button btnSendRequest;
    private RecyclerView recyclerView;
    private PaymentAdapter paymentAdapter;
    private List<Payment> paymentList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_biling);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(DoctorBillingActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        db = FirebaseFirestore.getInstance();

        etPatientEmail = findViewById(R.id.etPatientEmail);
        etAmount = findViewById(R.id.etAmount);
        btnSendRequest = findViewById(R.id.btnSendPaymentRequest);
        recyclerView = findViewById(R.id.recyclerViewCompletedPayments);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        paymentAdapter = new PaymentAdapter(paymentList, payment -> {},false); // Empty listener for doctors
        recyclerView.setAdapter(paymentAdapter);

        btnSendRequest.setOnClickListener(view -> sendPaymentRequest());

        loadCompletedPayments();
    }

    private void sendPaymentRequest() {
        String patientEmail = etPatientEmail.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(patientEmail) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(this, "Enter patient email and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount = Integer.parseInt(amountStr);
        String paymentId = db.collection("payments").document().getId();

        // ✅ Fetch patient name (assuming it's stored in Firestore)
        db.collection("users").whereEqualTo("email", patientEmail).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String patientName = "Unknown"; // Default
                    if (!queryDocumentSnapshots.isEmpty()) {
                        patientName = queryDocumentSnapshots.getDocuments().get(0).getString("name");
                    }

                    // ✅ Create Payment object with correct parameters
                    Payment payment = new Payment(paymentId, patientEmail, amount, "pending");

                    // ✅ Save to Firestore
                    db.collection("payments").document(paymentId).set(payment)
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Payment request sent!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to send request", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch patient name", Toast.LENGTH_SHORT).show());
    }

    private void loadCompletedPayments() {
        db.collection("payments")
                .whereEqualTo("status", "completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    paymentList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");  // ✅ Payment ID
                        String patientEmail = document.getString("patientEmail");  // ✅ Email
                        int amount = document.getLong("amount").intValue();  // ✅ Convert amount to int
                        String status = document.getString("status");  // ✅ Status

                        // ✅ Create Payment object with correct constructor
                        Payment payment = new Payment(id, patientEmail, amount, status);
                        paymentList.add(payment);
                    }
                    paymentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load payments", Toast.LENGTH_SHORT).show());
    }
}
