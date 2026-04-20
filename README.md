# 🦷 Pearl Dental Clinic App

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?logo=android)](https://developer.android.com/studio)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28.svg?logo=firebase)](https://firebase.google.com/)
[![Razorpay](https://img.shields.io/badge/Payments-Razorpay-02042B.svg?logo=razorpay)](https://razorpay.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

**Pearl Dental Clinic App** is a comprehensive, native Android application designed to digitize and streamline dental clinic operations. Developed to bridge the gap between fragmented booking systems and manual record-keeping, this platform integrates patient management, real-time appointment scheduling, secure digital payments, and interactive data analytics into a single, unified interface.

*This project was developed as an M.Sc. Computer Science Field Project at Ramnarain Ruia Autonomous College.*

---

## ✨ Key Modules & Features

The application utilizes Role-Based Access Control (RBAC) to provide tailored experiences for **Patients**, **Doctors**, and **Administrators/Staff**.

### 1. 🔐 User Authentication & RBAC
- Secure login and registration powered by **Firebase Authentication**.
- Role-specific dashboards ensuring data privacy and operational efficiency.
- Secure session management and hashed credential storage.

### 2. 📅 Appointment Scheduling
- **Patients:** Real-time booking, rescheduling, and cancellation based on doctor availability.
- **Doctors/Staff:** Automated scheduling system to prevent overlaps and double-bookings. Pending request approvals and completed appointment tracking.

### 3. 📂 Medical Record Management
- Secure cloud storage of patient history, allergies, medications, lab results, and prescriptions.
- Doctors and staff can instantly retrieve and update records using the patient's email ID, ensuring continuity of care.

### 4. 💳 Secure Payment Integration (Razorpay)
- Staff can generate and send digital payment requests directly to patients.
- Patients can fulfill payments securely via Razorpay (UPI, Cards, Net Banking).
- Automated digital receipt generation and transaction status tracking (Pending vs. Completed).

### 5. 📊 Data Analysis & Reports
- **Doctor's Dashboard:** Integrated **MPAndroidChart** provides visual insights into clinic operations.
- **Bar Charts:** Tracks the number of appointments handled per doctor.
- **Donut/Pie Charts:** Visualizes financial health by comparing pending vs. completed payments.

---

## 🏗️ Technology Stack

- **Frontend:** Android SDK (Java/Kotlin), XML, Material Design
- **Backend & Database:** Firebase Authentication, Cloud Firestore (Structured Data), Firebase Realtime Database (Instant Syncing)
- **Payment Gateway:** Razorpay SDK
- **Data Visualization:** MPAndroidChart
- **Security:** AES-256 Encryption standards for medical/payment data handling.

---

## 📸 System Walkthrough

| Module | Patient View | Doctor/Staff View |
| :--- | :---: | :---: |
| **Authentication** |<img width="429" height="914" alt="image" src="https://github.com/user-attachments/assets/4106a698-4121-494a-875b-dd83866be9df" />| <img width="428" height="913" alt="image" src="https://github.com/user-attachments/assets/9dcf951f-67a1-473a-b8da-79542dbf7ca2" /> |
| **Dashboard** |<img width="695" height="1262" alt="image" src="https://github.com/user-attachments/assets/0735ca2f-2f53-4831-b9fa-070fc8ac2e08" />| <img width="695" height="1262" alt="image" src="https://github.com/user-attachments/assets/342a9f13-c5a3-4189-8787-7deb86b22c9e" /> |
| **Appointments** |<img width="495" height="888" alt="image" src="https://github.com/user-attachments/assets/fd76aa5c-08d6-47ce-a648-af06c82ae2bb" />| <img width="518" height="927" alt="image" src="https://github.com/user-attachments/assets/7480374e-c1a3-43ae-b08f-f074c13aa01f" /> |
| **Medical Records** |<img width="544" height="866" alt="image" src="https://github.com/user-attachments/assets/3d7ba3ec-bd3b-434e-8b69-f4da009b8dc5" />| <img width="538" height="966" alt="image" src="https://github.com/user-attachments/assets/2718eed5-66b7-4c3c-a417-2d063c0e2783" /> |
| **Payments** |<img width="467" height="975" alt="image" src="https://github.com/user-attachments/assets/bae20e8a-92b3-4ead-8a64-a2c9730caf70" />| <img width="461" height="846" alt="image" src="https://github.com/user-attachments/assets/a72cf113-a387-457b-8232-02bba771491f" /> |
| **Analytics** | N/A | <img width="637" height="1328" alt="image" src="https://github.com/user-attachments/assets/f90a6158-6580-445a-8423-4f65d9c4918a" /> |

---

## 🚀 Quick Start & Installation

### Prerequisites
- [Android Studio](https://developer.android.com/studio) (Oreo 8.0+ targeting)
- A Firebase Project with Authentication, Firestore, and Realtime Database enabled.
- A Razorpay Test/Live API Key.

### Setup Instructions
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/YOUR-USERNAME/Pearl-Dental-Clinic.git](https://github.com/YOUR-USERNAME/Pearl-Dental-Clinic.git)
