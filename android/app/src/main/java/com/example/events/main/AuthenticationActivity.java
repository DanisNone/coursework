package com.example.events.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.events.R;
import com.example.events.network.ApiClient;
import com.example.events.network.ApiConfig;
import com.example.events.model.ProfileRepository;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {

    private EditText etLogin;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnToRegister;
    private final ActivityResultLauncher<Intent> registrationLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    finish();
                }
            }
    );
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_authentication_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);

        setupLoginButton();
        setupToRegisterButton();
    }
    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();

            ProfileRepository profile = ProfileRepository.getInstance(getApplication());
            Map<String, String> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);

            ApiClient.postJsonAsync(
                    ApiConfig.BASE_URL + "auth",
                    new Gson().toJson(map),
                    new ApiClient.PostCallback() {
                        @Override
                        public void onSuccess(String response) {
                            profile.setLogin(login);
                            profile.setPassword(password);
                            Intent intent = new Intent(AuthenticationActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(Exception e) {
                            Snackbar.make(v, "Invalid login or password", Snackbar.LENGTH_SHORT).show();
                        }
                    }
            );

        });
    }
    private void setupToRegisterButton() {
        btnToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(AuthenticationActivity.this, RegistrationActivity.class);
            registrationLauncher.launch(intent);
        });
    }
}
