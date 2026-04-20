package com.example.pearldentalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DoctorStaffAppointmentAdapter extends RecyclerView.Adapter<DoctorStaffAppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private OnAppointmentActionListener actionListener;

    public interface OnAppointmentActionListener {
        void onApproveClick(Appointment appointment);
        void onCompleteClick(Appointment appointment);
    }

    public DoctorStaffAppointmentAdapter(List<Appointment> appointmentList, OnAppointmentActionListener actionListener) {
        this.appointmentList = appointmentList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvAppointmentDate.setText(appointment.getDate()); // Line 36: Ensure this ID matches your XML layout
        holder.tvAppointmentDetails.setText(appointment.getDetails()); // Line 37: Ensure this ID matches your XML layout

        holder.btnApprove.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onApproveClick(appointment);
            }
        });

        holder.btnComplete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onCompleteClick(appointment);
            }
        });

        holder.btnApprove.setVisibility(appointment.isApproved() ? View.GONE : View.VISIBLE);
        holder.btnComplete.setVisibility(appointment.isApproved() && !appointment.isCompleted() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentDate, tvAppointmentDetails;
        Button btnApprove, btnComplete;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentDetails = itemView.findViewById(R.id.tvAppointmentDetails);
            btnApprove = itemView.findViewById(R.id.btnApprove); // Ensure this ID matches your XML layout
            btnComplete = itemView.findViewById(R.id.btnComplete); // Ensure this ID matches your XML layout
        }
    }
}
