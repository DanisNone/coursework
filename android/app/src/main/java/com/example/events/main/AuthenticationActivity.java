package com.example.events.main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
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
    private Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_authentication_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        setupLoginButton();
        setupRegisterButton();
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
                            setResult(Activity.RESULT_OK);
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
    private void setupRegisterButton() {
        btnRegister.setOnClickListener(v -> {
            // TODO: Добавить логику регистрации
        });
    }
}
