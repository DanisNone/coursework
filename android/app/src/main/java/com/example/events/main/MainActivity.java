package com.example.events.main;

import static com.example.events.network.ApiClient.httpGet;
import static com.example.events.network.ApiConfig.ANY_CITY;
import static com.example.events.network.ApiConfig.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.events.R;
import com.example.events.UI.DateTimePickerHelper;
import com.example.events.UI.ThemeManager;
import com.example.events.model.Event;
import com.example.events.network.ApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EventsApp";

    private Spinner spinnerCity;

    private EditText etStartDate, etEndDate;
    private Button btnSearch;
    private Button btnProfile;
    private Button btnSwitchTheme;
    private FloatingActionButton btnAddEvent;
    private ImageButton btnClearStart;
    private ImageButton btnClearEnd;

    private RecyclerView rvEvents;

    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();

        DateTimePickerHelper.setupDateTimePicker(this, etStartDate, startCalendar);
        DateTimePickerHelper.setupDateTimePicker(this, etEndDate, endCalendar);

        setupSearchButton();
        setupAddEventButton();
        setupProfileButton();
        setupThemeButton();
        setupClearStartButton();
        setupClearEndButton();

        loadCities();
    }


    private void initViews() {
        spinnerCity = findViewById(R.id.spinnerCity);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddEvent = findViewById(R.id.btnAddEvent);
        btnProfile = findViewById(R.id.btnProfile);
        btnSwitchTheme = findViewById(R.id.btnSwitchTheme);
        btnClearStart = findViewById(R.id.btnClearStartDate);
        btnClearEnd = findViewById(R.id.btnClearEndDate);
        rvEvents = findViewById(R.id.rvEvents);

        setSpinnerData(new String[]{ANY_CITY});
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setSpinnerData(String[] cities) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
    }

    private void loadCities() {
        executor.execute(() -> {
            boolean loaded = false;
            while (true) {
                try {
                    String data = httpGet(BASE_URL + "get_cities");
                    Gson gson = new Gson();
                    String[] cities = gson.fromJson(data, new TypeToken<String[]>(){}.getType());
                    runOnUiThread(() -> setSpinnerData(cities));
                    loaded = true;
                } catch (Exception e) {
                    Log.e(TAG, "loadCities", e);
                }

                if (loaded)
                    break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException err) {}
            }
        });
    }



    private void setupSearchButton() {
        btnSearch.setOnClickListener(v ->
                loadEvents(
                        spinnerCity.getSelectedItem().toString(),
                        etStartDate.getText().toString(),
                        etEndDate.getText().toString()
                )
        );
    }
    private void setupClearStartButton() {
        btnClearStart.setOnClickListener(v -> {
            DateTimePickerHelper.clearDateTime(etStartDate);
        });
    }

    private void setupClearEndButton() {
        btnClearEnd.setOnClickListener(v -> {
            DateTimePickerHelper.clearDateTime(etEndDate);
        });
    }

    private void setupAddEventButton() {
        btnAddEvent.setOnClickListener(v -> {
            // Создаем Intent для перехода на новую Activity
            Intent intent = new Intent(MainActivity.this, AddEventActivity.class);

            // Можно передать текущие фильтры в новую Activity
            intent.putExtra("selected_city", spinnerCity.getSelectedItem().toString());
            intent.putExtra("start_date", etStartDate.getText().toString());
            intent.putExtra("end_date", etEndDate.getText().toString());

            // Запускаем новую Activity
            startActivity(intent);
        });
    }

    private void setupProfileButton() {
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupThemeButton() {
        btnSwitchTheme.setOnClickListener(v -> switchTheme());
    }

    private void switchTheme() {
        int theme = ThemeManager.getSavedTheme(this);
        ThemeManager.saveTheme(this, theme == ThemeManager.THEME_DARK ? ThemeManager.THEME_LIGHT : ThemeManager.THEME_DARK);
        ThemeManager.applyTheme(theme);
        recreate();
    }

    private void loadEvents(String city, String start, String end) {
        ApiClient.getEventsAsync(city, start, end, new ApiClient.EventsCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                for (Event event : events) {
                    event.setStartTime(DateTimePickerHelper.removeTAndFormat(event.getStartTime()));
                    event.setEndTime(DateTimePickerHelper.removeTAndFormat(event.getEndTime()));

                    if (!event.getDescription().isEmpty()) {
                        event.setDescription(event.getDescription().substring(2));
                    }
                    runOnUiThread(() -> rvEvents.setAdapter(new EventAdapter(events)));
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Ошибка: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        });
    }
}
