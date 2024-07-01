package com.example.mawadda3_0;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private TextView mNameField;
    private TextView mPhoneField, mAge;

    private ImageButton mBack;
    private Button mConfirm, mDeleteAccount; // Added delete account button

    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name, phone, age, profileImageUrl, userSex;

    private Uri resultUri;
    private Activity Activity;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting);

        // Retrieve userSex from intent
        userSex = getIntent().getExtras().getString("userSex");

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phoneNumber);
        mAge = (EditText) findViewById(R.id.editAge);

        mProfileImage = (ImageView) findViewById(R.id.profilePicture);

        mBack = (ImageButton) findViewById(R.id.backButton);
        mConfirm = (Button) findViewById(R.id.saveButton);
        mDeleteAccount = (Button) findViewById(R.id.deleteButton); // Initialize delete account button

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userSex).child(userId);
        getUserInfo();

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if (map.get("age") != null) {
                        age = map.get("age").toString();
                        mAge.setText(age);
                    }
                    Glide.with(getApplicationContext()).clear(mProfileImage);
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        switch (profileImageUrl) {
                            case "default":
                                Glide.with(getApplicationContext()).load(R.drawable.default_pfp).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplicationContext()).load(profileImageUrl).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    private void saveUserInformation() {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();
        age = mAge.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("age", age); // Add age to the map

        mUserDatabase.updateChildren(userInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SettingsActivity.this, "Information saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Failed to save information", Toast.LENGTH_SHORT).show();
            }
        });

        if (resultUri != null) {
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);

            uploadTask.addOnFailureListener(e -> finish());

            uploadTask.addOnSuccessListener(taskSnapshot ->
                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        Map<String, Object> userInfoWithImage = new HashMap<>();
                        userInfoWithImage.put("profileImageUrl", downloadUrl);
                        mUserDatabase.updateChildren(userInfoWithImage).addOnCompleteListener(imageTask -> {
                            if (imageTask.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        });

                    }).addOnFailureListener(e -> {
                        // Handle any errors
                        e.printStackTrace();
                        Toast.makeText(SettingsActivity.this, "Failed to get image download URL", Toast.LENGTH_SHORT).show();
                        finish();
                    })
            );
        } else {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(SettingsActivity.this, "Successful Sign Out", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(SettingsActivity.this, MatchesActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
    }

    public void goToChat(View view) {
        Intent intent = new Intent(SettingsActivity.this, MatchesActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        mUserDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this, "Failed to delete account data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
