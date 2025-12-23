package com.example.events.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.events.R;
import com.example.events.UI.DateTimePickerHelper;
import com.example.events.model.Event;
import com.example.events.network.ApiClient;
import com.example.events.network.ApiConfig;
import com.google.gson.Gson;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    private EditText etEventName, etEventDescription, etStartTime, etEndTime, etEventLocation, etEventCity;
    private Button btnSave;
    private ImageView btnBack;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_event_activity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();

        DateTimePickerHelper.setupDateTimePicker(this, etStartTime, startCalendar);
        DateTimePickerHelper.setupDateTimePicker(this, etEndTime, endCalendar);


        setupSaveButton();
        setupBackButton();

        // Устанавливаем город из интента
        setCityFromIntent();
    }

    private void setCityFromIntent() {
        Intent intent = getIntent();
        String selectedCity = intent.getStringExtra("selected_city");
        if (selectedCity != null && !selectedCity.equals(ApiConfig.ANY_CITY)) {
            etEventCity.setText(selectedCity);
        }
    }

    private void initViews() {
        etEventName = findViewById(R.id.etEventName);
        etEventDescription = findViewById(R.id.etEventDescription);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etEventLocation = findViewById(R.id.etEventLocation);
        etEventCity = findViewById(R.id.etEventCity);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (!validateInput()) {
                return;
            }
            saveEvent();
        });
    }


    private boolean validateInput() {
        String name = etEventName.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String city = etEventCity.getText().toString().trim();

        if (name.isEmpty()) {
            showToast("Введите название мероприятия");
            return false;
        }

        if (startTime.isEmpty()) {
            showToast("Выберите время начала");
            return false;
        }

        if (endTime.isEmpty()) {
            showToast("Выберите время окончания");
            return false;
        }

        if (location.isEmpty()) {
            showToast("Введите местоположение");
            return false;
        }

        if (city.isEmpty()) {
            showToast("Введите город");
            return false;
        }

        // Проверка времени
        try {
            java.util.Date startDate = DateTimePickerHelper.dateFormat.parse(startTime); // Changed
            java.util.Date endDate = DateTimePickerHelper.dateFormat.parse(endTime); // Changed

            if (endDate.before(startDate)) {
                showToast("Время окончания должно быть позже времени начала");
                return false;
            }

            // Дополнительная проверка: событие не должно быть в прошлом
            if (startDate.before(new java.util.Date())) {
                showToast("Время начала не может быть в прошлом");
                return false;
            }
        } catch (Exception e) {
            showToast("Ошибка в формате времени");
            return false;
        }

        return true;
    }

    private void saveEvent() {
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String location = etEventLocation.getText().toString().trim();
        String city = etEventCity.getText().toString().trim();
        String name = etEventName.getText().toString().trim();
        String description = etEventDescription.getText().toString().trim();


        Event newEvent = new Event (
                startTime, endTime,
                location, city,
                name, description, 1
        );

        Gson gson = new Gson();
        String jsonData = gson.toJson(newEvent);
        String url = ApiConfig.POST_STR;

        btnSave.setEnabled(false);
        btnSave.setText("Сохранение...");

        ApiClient.postJsonAsync(url, jsonData, new ApiClient.PostCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i("INFO", response);
                // Возвращаемся в UI-поток
                runOnUiThread(() -> {
                    Toast.makeText(AddEventActivity.this,
                            "Мероприятие успешно сохранено!", Toast.LENGTH_SHORT).show();

                    // Возвращаем результат в MainActivity
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(Exception error) {
                // Возвращаемся в UI-поток
                runOnUiThread(() -> {
                    // Восстанавливаем кнопку
                    btnSave.setEnabled(true);
                    btnSave.setText("Сохранить");

                    // Показываем ошибку
                    String errorMessage = error.toString();
//                    if (error.contains("timeout") || error.contains("connect")) {
//                        errorMessage = "Ошибка подключения. Проверьте интернет.";
//                    } else if (error.contains("HTTP 4")) {
//                        errorMessage = "Ошибка данных (код 4xx)";
//                    } else if (error.contains("HTTP 5")) {
//                        errorMessage = "Ошибка сервера (код 5xx)";
//                    } else {
//                        errorMessage = "Ошибка: " + error;
//                    }
                    Log.e("ERROr", errorMessage);
                    Toast.makeText(AddEventActivity.this,
                            errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            // Спрашиваем подтверждение, если есть введенные данные
            if (hasUnsavedChanges()) {
                showUnsavedChangesDialog();
            } else {
                finish();
            }
        });
    }

    private boolean hasUnsavedChanges() {
        return !etEventName.getText().toString().trim().isEmpty() ||
                !etEventDescription.getText().toString().trim().isEmpty() ||
                !etStartTime.getText().toString().trim().isEmpty();
    }

    private void showUnsavedChangesDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Несохраненные изменения")
                .setMessage("У вас есть несохраненные изменения. Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> finish())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}