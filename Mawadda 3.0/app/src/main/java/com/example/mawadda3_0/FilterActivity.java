package com.example.mawadda3_0;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FilterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_popup);

        // Initialize the back button
        ImageButton backButton = findViewById(R.id.backButton);

        // Set an OnClickListener to finish the activity when the back button is clicked
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void goToHome(View view) {
        Intent intent = new Intent(FilterActivity.this, HomeActivity.class);
        startActivity(intent);
        return;
    }
}
