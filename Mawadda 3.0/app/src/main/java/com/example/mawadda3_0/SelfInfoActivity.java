package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SelfInfoActivity extends AppCompatActivity {

    private EditText mName,firstNameEditText, lastNameEditText, ageEditText;
    private Button confirmBtn;
    private String hobby, education, age,name;
    private FirebaseAuth mAuth;
    private RadioGroup mRadioGroupPlan, mRadioGroup;

    private RadioButton radioButtonPlan,radioButton;
    private FirebaseDatabase db;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_info);



        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        mName = findViewById(R.id.etName);
        ageEditText = findViewById(R.id.age);
        firstNameEditText = findViewById(R.id.hobby);
        lastNameEditText = findViewById(R.id.education);
        confirmBtn = findViewById(R.id.btnConfirm);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRadioGroupPlan = findViewById(R.id.marriagePlanRadioGroup);

        // Set OnClickListener for the Confirm button
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve text from EditText fields
                hobby = firstNameEditText.getText().toString().trim();
                education = lastNameEditText.getText().toString().trim();
                age = ageEditText.getText().toString().trim();
                name = mName.getText().toString().trim();

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectId); // Initialize radioButton here

                if (radioButton == null || radioButton.getText() == null) {
                    Toast.makeText(SelfInfoActivity.this, "Select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectIdPlan = mRadioGroupPlan.getCheckedRadioButtonId();
                radioButtonPlan = findViewById(selectIdPlan); // Initialize radioButton here

                if (radioButtonPlan == null || radioButtonPlan.getText() == null) {
                    Toast.makeText(SelfInfoActivity.this, "Pick a Plan", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate input fields
                if (hobby.isEmpty() || education.isEmpty() || age.isEmpty()|| name.isEmpty()) {
                    Toast.makeText(SelfInfoActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create Users object with user data
                Users users = new Users(hobby, education, age, name);

                // Initialize Firebase Database reference
                db = FirebaseDatabase.getInstance();
                reference = db.getReference("Users").child(radioButton.getText().toString());
                 // Reference to "Users/Female" or "Users/Male"

                // Store user data in Firebase under selected category
                reference.child(mAuth.getCurrentUser().getUid()).setValue(users);

                String userId = mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId);
                Map userInfo = new HashMap<>();
                userInfo.put("profileImageUrl", "default");
                currentUserDb.updateChildren(userInfo);

                reference.child(mAuth.getCurrentUser().getUid()).child("plan").setValue(radioButtonPlan.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Clear EditText fields
                            firstNameEditText.setText("");
                            lastNameEditText.setText("");
                            ageEditText.setText("");
                            mName.setText("");
                            Toast.makeText(SelfInfoActivity.this, "Personal Information Updated", Toast.LENGTH_SHORT).show();

                            // Navigate to HomeActivity
                            Intent intent = new Intent(SelfInfoActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SelfInfoActivity.this, "Error updating info", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
