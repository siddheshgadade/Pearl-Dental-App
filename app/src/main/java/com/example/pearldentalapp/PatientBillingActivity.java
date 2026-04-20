package com.example.pearldentalapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PatientBillingActivity extends AppCompatActivity implements PaymentResultListener {

    private RecyclerView recyclerView;
    private PaymentAdapter paymentAdapter;
    private List<Payment> paymentList = new ArrayList<>();
    private FirebaseFirestore db;
    private String currentUserEmail;
    private Payment selectedPayment; // Store selected payment for status update

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_billing);

        findViewById(R.id.btnBack).setOnClickListener(view -> {
            Intent intent = new Intent(PatientBillingActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewPayment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get Current User's Email
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return; // Exit if no user is logged in
        }

        // Initialize Payment Adapter with Click Listener
        paymentAdapter = new PaymentAdapter(paymentList, new PaymentAdapter.OnPaymentClickListener() {
            @Override
            public void onPayClick(Payment payment) {
                startPayment(payment);
            }
        },true);

        // Set Adapter to RecyclerView
        recyclerView.setAdapter(paymentAdapter);

        // Load Pending Payments
        loadPendingPayments();

        Button btnGenerateReceipt = findViewById(R.id.btnGenerateInvoice);
        btnGenerateReceipt.setOnClickListener(v -> generateReceipt());
    }

    private void loadPendingPayments() {
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: No email found!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("payments")
                .whereEqualTo("patientEmail", currentUserEmail)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading payments: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No pending payments found.", Toast.LENGTH_SHORT).show();
                        paymentList.clear();
                        paymentAdapter.notifyDataSetChanged();
                        return;
                    }

                    paymentList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Payment payment = doc.toObject(Payment.class);
                        if (payment != null) {
                            paymentList.add(payment);
                        }
                    }
                    paymentAdapter.notifyDataSetChanged();
                });
    }


    private void startPayment(Payment payment) {
        selectedPayment = payment; // Store selected payment for later use

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_CWwdvf6ARQf1us"); // Replace with your Razorpay Test Key

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Pearl Dental Clinic");
            options.put("description", "Payment for services");
            options.put("currency", "INR");
            options.put("amount", payment.getAmount() * 100); // Convert to Paisa

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // ✅ ADD THIS FUNCTION TO HANDLE SUCCESSFUL PAYMENT
    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

        // ✅ Store transaction ID and update status in Firestore
        db.collection("payments").document(selectedPayment.getId())
                .update("status", "completed", "transactionId", razorpayPaymentId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Payment status updated", Toast.LENGTH_SHORT).show();
                    loadPendingPayments(); // Refresh list
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update payment", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_SHORT).show();
    }

    private void generateReceipt() {
        Intent intent = new Intent(PatientBillingActivity.this, ReceiptActivity.class);
        intent.putExtra("patientEmail", currentUserEmail);
        startActivity(intent);
    }
}

