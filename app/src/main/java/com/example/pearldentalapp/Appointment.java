package com.example.pearldentalapp;

public class Appointment {
    private String id;
    private String patientName;
    private String condition;
    private String date;
    private String details;
    private boolean approved;
    private boolean completed; // Add the completed field

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {
    }

    public Appointment(String id, String patientName, String condition, String date, String details, boolean approved, boolean completed) {
        this.id = id;
        this.patientName = patientName;
        this.condition = condition;
        this.date = date;
        this.details = details;
        this.approved = approved;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
