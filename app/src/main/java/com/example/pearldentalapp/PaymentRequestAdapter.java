package com.example.pearldentalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentRequestAdapter extends RecyclerView.Adapter<PaymentRequestAdapter.ViewHolder> {

    private List<Payment> paymentList;
    private OnPaymentClickListener listener;

    public interface OnPaymentClickListener {
        void onPayClicked(Payment payment);
    }

    public PaymentRequestAdapter(List<Payment> paymentList, OnPaymentClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.tvPatientName.setText(payment.getPatientEmail());
        holder.tvAmount.setText("₹" + payment.getAmount());
        holder.tvStatus.setText(payment.getStatus());

        holder.btnPayNow.setVisibility(payment.getStatus().equals("Pending") ? View.VISIBLE : View.GONE);

        holder.btnPayNow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPayClicked(payment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvAmount, tvStatus;
        Button btnPayNow;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientEmail);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnPayNow = itemView.findViewById(R.id.btnPay);
        }
    }
}
