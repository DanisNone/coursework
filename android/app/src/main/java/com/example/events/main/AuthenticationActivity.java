package com.example.events.main;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.events.R;
import com.example.events.network.ApiClient;
import com.example.events.network.ApiConfig;
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

    private String checkLogin(String login) {
        if (login.length() <= 3)
            return "Логин слишком короткий";
        if (64 <= login.length())
            return "Логин слишком Длинный";
        if (!login.matches("^[a-zA-Z0-9._]$")) {
            return "Логин содержит недопустимые символы";
        }
        return null;
    }

    private String checkPassword(String password) {
        if (password.length() < 8)
            return "Пароль должен быть не короче 8 символов";
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};:'\",./?\\\\|<>]+$"))
            return "Пароль содержит недопустимые символы";
        if (password.matches("^[a-zA-Z0-9]"))
            return "Пароль должен содержать спецсимволы";
        if (password.equals(password.toLowerCase()) || password.equals(password.toUpperCase()))
            return "Пароль должен содержать буквы различного регистра";
        return null;
    }
    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString();
            String password = etPassword.getText().toString();

            String loginError = checkLogin(login);
            if (loginError != null) {
                Snackbar.make(v, loginError, Snackbar.LENGTH_SHORT).show();
                return;
            }

            String passwordError = checkPassword(password);
            if (passwordError != null) {
                Snackbar.make(v, passwordError, Snackbar.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);

            ApiClient.postJsonAsync(
                    ApiConfig.BASE_URL + "auth",
                    new Gson().toJson(map),
                    new ApiClient.PostCallback() {
                        @Override
                        public void onSuccess(String response) {

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
