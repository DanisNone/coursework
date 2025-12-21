package com.example.events.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.events.R;

public class ProfileActivity extends AppCompatActivity {
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBtnCreateEvent();
    }

    private void initViews() {
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        if (btnCreateEvent == null) {
            Toast.makeText(this, "UI error: button not found", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setupBtnCreateEvent() {
        btnCreateEvent.setOnClickListener(v -> openAddEventActivity());
    }

    private void openAddEventActivity() {
        try {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot open event creation", Toast.LENGTH_SHORT).show();
        }
    }
}