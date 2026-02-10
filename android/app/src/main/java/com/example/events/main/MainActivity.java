package com.example.events.main;

import android.content.Intent;
import android.content.res.Configuration;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.events.R;
import com.example.events.UI.DateTimePickerHelper;
import com.example.events.UI.NightModeView;
import com.example.events.model.PublicEvent;
import com.example.events.network.ApiClient;
import com.example.events.viewModel.CitiesViewModel;
import com.example.events.viewModel.EventsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
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
        NightModeView nightMode = new ViewModelProvider(this).get(NightModeView.class);
        AppCompatDelegate.setDefaultNightMode(nightMode.getMode());

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

        CitiesViewModel cities = new ViewModelProvider(this).get(CitiesViewModel.class);
        executor.execute(cities::loadCities);
        cities.getCities().observe(this, this::setSpinnerData);
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
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        EventsViewModel eventsView = new ViewModelProvider(this).get(EventsViewModel.class);
        eventsView.getPubEvents().observe(this,  publicEvents -> rvEvents.setAdapter(new PubEventAdapter(publicEvents)));
    }

    private void setSpinnerData(List<String> cities) {
        ArrayList<String> cities_copy = new ArrayList<>();
        cities_copy.add("Любой");
        cities_copy.addAll(cities);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities_copy
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
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
        btnClearStart.setOnClickListener(v -> DateTimePickerHelper.clearDateTime(etStartDate));
    }

    private void setupClearEndButton() {
        btnClearEnd.setOnClickListener(v -> DateTimePickerHelper.clearDateTime(etEndDate));
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
        boolean is_dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        NightModeView nightMode = new ViewModelProvider(this).get(NightModeView.class);
        if (is_dark)
            nightMode.setMode(AppCompatDelegate.MODE_NIGHT_NO);
        else
            nightMode.setMode(AppCompatDelegate.MODE_NIGHT_YES);
        recreate();
    }

    private void loadEvents(String city, String start, String end) {
        EventsViewModel eventsView = new ViewModelProvider(this).get(EventsViewModel.class);
        ApiClient.getEventsAsync(city, start, end, new ApiClient.EventsCallback() {
            @Override
            public void onSuccess(List<PublicEvent> events) {
                eventsView.setPublicEvents(events);
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