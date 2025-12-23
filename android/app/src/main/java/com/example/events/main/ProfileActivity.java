package com.example.events.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.events.R;
import com.example.events.model.ProfileRepository;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupBtnLogout();
        setupBtnCreateEvent();
    }

    private void initViews() {
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnLogout = findViewById(R.id.btnLogout);
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
    private void setupBtnLogout() {
        btnLogout.setOnClickListener(v->{
            new AlertDialog.Builder(this)
                    .setTitle("Подтверждение")
                    .setMessage("Вы действительно хотите выйти?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        ProfileRepository profile = ProfileRepository.getInstance(getApplication());
                        profile.logout();
                        finish();
                    })
                    .setNegativeButton("Нет", (dialog, which) -> {
                        dialog.dismiss(); // Закрываем диалог
                    })
                    .show();
        });
    }
}