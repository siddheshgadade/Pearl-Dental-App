package com.example.pearldentalapp;

public class Payment {
    private String id;
    private String patientEmail;
    private int amount;
    private String status;

    private long timestamp;

    // ✅ Required No-Argument Constructor for Firestore
    public Payment() {
    }

    // ✅ Constructor with parameters
    public Payment(String id, String patientEmail, int amount, String status) {
        this.id = id;
        this.patientEmail = patientEmail;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    // ✅ Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public long setTimestamp() {
        return timestamp;
    }
    public long getTimestamp() {
        return timestamp;
    }
}
