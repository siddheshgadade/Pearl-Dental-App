package com.example.pearldentalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MedicalRecordsAdapter extends RecyclerView.Adapter<MedicalRecordsAdapter.MedicalRecordViewHolder> {

    private List<PatientRecord> patientRecordList;

    public MedicalRecordsAdapter(List<PatientRecord> patientRecordList) {
        this.patientRecordList = patientRecordList;
    }

    @NonNull
    @Override
    public MedicalRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_record, parent, false);
        return new MedicalRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalRecordViewHolder holder, int position) {
        PatientRecord patientRecord = patientRecordList.get(position);
        holder.tvPatientName.setText(patientRecord.getPatientId()); // Assuming patient name is part of patientId in Records
        holder.tvMedicalHistory.setText(patientRecord.getMedicalHistory());
        holder.tvAllergies.setText(patientRecord.getAllergies());
        holder.tvMedications.setText(patientRecord.getMedications());
        holder.tvLabResults.setText(patientRecord.getLabResults());
        holder.tvPrescriptions.setText(patientRecord.getPrescriptions());
    }

    @Override
    public int getItemCount() {
        return patientRecordList.size();
    }

    public static class MedicalRecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientName, tvMedicalHistory, tvAllergies, tvMedications, tvLabResults, tvPrescriptions;

        public MedicalRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientName = itemView.findViewById(R.id.tvPatientName);
            tvMedicalHistory = itemView.findViewById(R.id.tvMedicalHistory);
            tvAllergies = itemView.findViewById(R.id.tvAllergies);
            tvMedications = itemView.findViewById(R.id.tvMedications);
            tvLabResults = itemView.findViewById(R.id.tvLabResults);
            tvPrescriptions = itemView.findViewById(R.id.tvPrescriptions);
        }
    }
}
