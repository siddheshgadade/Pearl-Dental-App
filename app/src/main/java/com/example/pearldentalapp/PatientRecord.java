package com.example.pearldentalapp;

public class PatientRecord {
    private String name;
    private String patientId;
    private String medicalHistory;
    private String allergies;
    private String medications;
    private String labResults;
    private String prescriptions;

    // Constructor with all fields
    public PatientRecord(String name, String patientId, String medicalHistory, String allergies, String medications, String labResults, String prescriptions) {
        this.name = name;
        this.patientId = patientId;
        this.medicalHistory = medicalHistory;
        this.allergies = allergies;
        this.medications = medications;
        this.labResults = labResults;
        this.prescriptions = prescriptions;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public String getAllergies() {
        return allergies;
    }

    public String getMedications() {
        return medications;
    }

    public String getLabResults() {
        return labResults;
    }

    public String getPrescriptions() {
        return prescriptions;
    }
}
