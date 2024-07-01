package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText  mPassword, mEmail, mConfirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up the AuthStateListener
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegisterActivity.this, SelfInfoActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        // Initialize UI elements
        mRegister = findViewById(R.id.btnCreateAccount);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mConfirmPassword = findViewById(R.id.etConfirmPassword);


        // Set OnClickListener for the Register button
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = mEmail.getText().toString().trim();
                final String password = mPassword.getText().toString().trim();
                final String confirmpassword = mConfirmPassword.getText().toString().trim();


                // Validate email input
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                // Validate password input
                if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Password is required");
                    return;
                }

                // Validate password length
                if (password.length() < 3) {
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }

                // Validate password confirmation
                if (!password.equals(confirmpassword)) {
                    mConfirmPassword.setError("Passwords do not match");
                    return;
                }

                // Create user with email and password
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Sign Up Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener); // Add the listener when the activity starts
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthStateListener != null) {
            mAuth.removeAuthStateListener(firebaseAuthStateListener); // Remove the listener when the activity stops
        }
    }
}
