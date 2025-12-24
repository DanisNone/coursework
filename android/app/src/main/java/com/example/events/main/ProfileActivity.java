package com.example.events.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.events.R;
import com.example.events.model.ProfileRepository;
import com.example.events.model.PublicUser;
import com.example.events.network.ApiClient;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName;
    private TextView tvSurname;
    private Button btnLogout;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ProfileRepository profile = ProfileRepository.getInstance(getApplication());
        if (!profile.isLogged()) {
            Intent intent = new Intent(ProfileActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        initViews();
        setupNames();
        setupBtnLogout();
        setupBtnCreateEvent();
    }

    private void initViews() {
        tvName = findViewById(R.id.tvName);
        tvSurname = findViewById(R.id.tvSurname);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupNames() {
        ProfileRepository profile = ProfileRepository.getInstance(getApplication());
        ApiClient.getUserAsync(profile.getLogin(), new ApiClient.UserCallback() {
            @Override
            public void onSuccess(PublicUser user) {
                tvName.setText("Имя: " + user.getName());
                tvSurname.setText("Фамилия: " + user.getSurname());
            }

            @Override
            public void onError(Exception e) {
                // TODO: добавить информацию
            }
        });
    }
    private void setupBtnCreateEvent() {
        btnCreateEvent.setOnClickListener(v -> openAddEventActivity());
    }

    private void openAddEventActivity() {
        try {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        } catch (Exception e) {
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