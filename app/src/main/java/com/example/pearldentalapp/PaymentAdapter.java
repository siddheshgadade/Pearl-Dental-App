package com.example.pearldentalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private List<Payment> paymentList;
    private OnPaymentClickListener listener;
    private boolean isPatientSide; // ✅ New variable to check if patient side

    // ✅ Updated constructor
    public PaymentAdapter(List<Payment> paymentList, OnPaymentClickListener listener, boolean isPatientSide) {
        this.paymentList = paymentList;
        this.listener = listener;
        this.isPatientSide = isPatientSide;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = paymentList.get(position);

        holder.tvPatientEmail.setText(payment.getPatientEmail());
        holder.tvTransactionId.setText("Transaction ID: " + payment.getId());
        holder.tvAmount.setText("₹" + payment.getAmount());
        holder.tvStatus.setText(payment.getStatus());

        // ✅ Show Pay Button only if on Patient Side
        if (isPatientSide && payment.getStatus().equals("pending")) {
            holder.btnPay.setVisibility(View.VISIBLE);
        } else {
            holder.btnPay.setVisibility(View.GONE);
        }

        // ✅ Handle Pay Button Click
        holder.btnPay.setOnClickListener(view -> {
            if (listener != null) {
                listener.onPayClick(payment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvStatus, tvPatientEmail, tvTransactionId;
        Button btnPay;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPatientEmail = itemView.findViewById(R.id.tvPatientEmail);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            btnPay = itemView.findViewById(R.id.btnPay);
        }
    }

    public interface OnPaymentClickListener {
        void onPayClick(Payment payment);
    }
}
