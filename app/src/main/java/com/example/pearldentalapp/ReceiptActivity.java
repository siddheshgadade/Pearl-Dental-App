package com.example.pearldentalapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReceiptActivity extends AppCompatActivity {

    private TextView tvReceiptPatientEmail, tvReceiptAmount, tvReceiptStatus, tvReceiptTransactionId;
    private Button btnDownloadReceipt;
    private FirebaseFirestore firestore;
    private String patientEmail, amount, status, transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice); // Ensure this layout exists

        // Initialize UI elements
        tvReceiptPatientEmail = findViewById(R.id.tvReceiptPatientEmail);
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);
        tvReceiptStatus = findViewById(R.id.tvReceiptStatus);
        tvReceiptTransactionId = findViewById(R.id.tvReceiptTransactionId);
        btnDownloadReceipt = findViewById(R.id.btnDownloadReceipt);

        // Get Firestore instance
        firestore = FirebaseFirestore.getInstance();

        // Get patient email from intent
        patientEmail = getIntent().getStringExtra("patientEmail");

        // Fetch latest completed payment details
        fetchReceiptData();

        // Download PDF when button is clicked
        btnDownloadReceipt.setOnClickListener(v -> generatePDF());
    }

    private void fetchReceiptData() {
        CollectionReference paymentsRef = firestore.collection("payments");

        // Get the most recent completed payment for the patient
        paymentsRef
                .whereEqualTo("patientEmail", patientEmail)
                .whereEqualTo("status", "Completed")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order by latest
                .limit(1) // Get only the most recent one
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                        amount = "₹" + document.getString("amount");
                        status = document.getString("status");
                        transactionId = document.getString("id");

                        // Update UI
                        tvReceiptPatientEmail.setText("Patient Email: " + patientEmail);
                        tvReceiptAmount.setText("Amount Paid: " + amount);
                        tvReceiptStatus.setText("Payment Status: " + status);
                        tvReceiptTransactionId.setText("Transaction ID: " + transactionId);
                    } else {
                        Toast.makeText(this, "No completed payments found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FIRESTORE_ERROR", "Error fetching receipt", e);
                    Toast.makeText(this, "Error fetching receipt", Toast.LENGTH_SHORT).show();
                });
    }

    private void generatePDF() {
        // Request storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }

        // Create a new PDF document
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(500, 700, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw Header
        paint.setColor(Color.BLACK);
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText("Pearl Dental Clinic", 150, 50, paint);

        paint.setTextSize(18);
        paint.setFakeBoldText(false);
        canvas.drawText("Payment Receipt", 180, 80, paint);

        // Separator Line
        paint.setTextSize(14);
        canvas.drawLine(50, 100, 450, 100, paint);

        // Receipt Details
        paint.setTextSize(14);
        canvas.drawText("Patient Email: " + patientEmail, 50, 130, paint);
        canvas.drawText("Amount Paid: " + amount, 50, 160, paint);
        canvas.drawText("Payment Status: " + status, 50, 190, paint);
        canvas.drawText("Transaction ID: " + (transactionId != null ? transactionId : "N/A"), 50, 220, paint);

        // Footer
        paint.setTextSize(12);
        canvas.drawText("------------------------------", 50, 260, paint);
        canvas.drawText("Thank you for your payment!", 140, 290, paint);
        canvas.drawText("Pearl Dental Clinic", 180, 320, paint);

        // Finish PDF
        pdfDocument.finishPage(page);

        // Save the PDF
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Receipt_" + System.currentTimeMillis() + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Receipt downloaded!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("PDF_ERROR", "Error saving PDF", e);
            Toast.makeText(this, "Error saving receipt!", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generatePDF();
            } else {
                Toast.makeText(this, "Storage permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
