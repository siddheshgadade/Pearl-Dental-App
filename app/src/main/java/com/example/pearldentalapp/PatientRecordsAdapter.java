package com.example.pearldentalapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PatientRecordsAdapter extends RecyclerView.Adapter<PatientRecordsAdapter.ViewHolder> {

    private List<PatientRecord> patientRecordsList;

    public PatientRecordsAdapter(List<PatientRecord> patientRecordsList) {
        this.patientRecordsList = patientRecordsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PatientRecord patientRecord = patientRecordsList.get(position);
        holder.tvName.setText("Name: " + patientRecord.getName());
        holder.tvPatientId.setText("Patient ID: " + patientRecord.getPatientId());
        holder.tvMedicalHistory.setText("Medical History: " + patientRecord.getMedicalHistory());
        holder.tvAllergies.setText("Allergies: " + patientRecord.getAllergies());
        holder.tvMedications.setText("Medications: " + patientRecord.getMedications());
        holder.tvLabResults.setText("Lab Results: " + patientRecord.getLabResults());
        holder.tvPrescriptions.setText("Prescriptions: " + patientRecord.getPrescriptions());
    }

    @Override
    public int getItemCount() {
        return patientRecordsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPatientId, tvMedicalHistory, tvAllergies, tvMedications, tvLabResults, tvPrescriptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPatientId = itemView.findViewById(R.id.tvPatientId);
            tvMedicalHistory = itemView.findViewById(R.id.tvMedicalHistory);
            tvAllergies = itemView.findViewById(R.id.tvAllergies);
            tvMedications = itemView.findViewById(R.id.tvMedications);
            tvLabResults = itemView.findViewById(R.id.tvLabResults);
            tvPrescriptions = itemView.findViewById(R.id.tvPrescriptions);
        }
    }
}
