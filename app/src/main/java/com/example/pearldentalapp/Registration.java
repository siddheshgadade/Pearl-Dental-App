package com.example.pearldentalapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {


    private static final String TAG = "RegistrationActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText etName, etEmail, etPassword;
    private RadioGroup rgRoles;
    private Button btnRegister;
    private ImageView ivTogglePassword;
    private ProgressBar spinner;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // Bind UI elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        rgRoles = findViewById(R.id.rgRoles);
        btnRegister = findViewById(R.id.btnRegister);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        spinner = findViewById(R.id.spinner);


        ivTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.key_off);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivTogglePassword.setImageResource(R.drawable.key_on);
                }
                etPassword.setSelection(etPassword.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });


        // Set onClick listener for the register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                registerUser();
            }
        });
    }


    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        int selectedRoleId = rgRoles.getCheckedRadioButtonId();
        RadioButton selectedRoleButton = findViewById(selectedRoleId);
        String role = selectedRoleButton.getText().toString();


        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
            Toast.makeText(Registration.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.GONE);
            return;
        }


        // Firebase authentication: create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, save user data to Firestore and navigate to login page
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUserData(user, name, email, role);
                            Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Registration.this, loginactivity.class));
                            finish();
                        } else {
                            // If registration fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            spinner.setVisibility(View.GONE);
                        }
                    }
                });
    }




    private void saveUserData(FirebaseUser user, String name, String email, String role) {
        DocumentReference docRef = db.collection("users").document(user.getUid());


        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", role);


        docRef.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User data saved successfully");
                } else {
                    Log.w(TAG, "Failed to save user data", task.getException());
                    Toast.makeText(Registration.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }




}
