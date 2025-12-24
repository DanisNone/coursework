package com.example.events.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.events.R;
import com.example.events.model.ProfileRepository;
import com.example.events.network.ApiClient;
import com.example.events.network.ApiConfig;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etSurname;
    private EditText etLogin;
    private EditText etPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_registration_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        etName = findViewById(R.id.etName);
        etSurname = findViewById(R.id.etSurname);
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        setupRegisterButton();
    }

    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            String name = etSurname.getText().toString();
            String surname = etName.getText().toString();
            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();

            ProfileRepository profile = ProfileRepository.getInstance(getApplication());
            Map<String, String> map = new HashMap<>();
            // Добавить проверки
            map.put("login", login);
            map.put("password", password);
            map.put("name", name);
            map.put("surname", surname);

            ApiClient.postJsonAsync(
                    ApiConfig.BASE_URL + "registr",
                    new Gson().toJson(map),
                    new ApiClient.PostCallback() {
                        @Override
                        public void onSuccess(String response) {
                            profile.setLogin(login);
                            profile.setPassword(password);
                            setResult(Activity.RESULT_OK);
                            Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            Snackbar.make(v, "Error: " + e.toString(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
            );

        });
    }
}
