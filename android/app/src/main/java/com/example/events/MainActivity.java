package com.example.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

class Event {
    public String startTime;
    public String endTime;
    public String full_location;
    public String city;
    public String name;
}


public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCity;
    private EditText etStartDate, etEndDate;
    private Button btnSearch;
    private RecyclerView rvEvents;

    private final Calendar startDateTimeCalendar = Calendar.getInstance();
    private final Calendar endDateTimeCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private final String url_base = "https://4mwnpw7m-8080.euw.devtunnels.ms/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCity = findViewById(R.id.spinnerCity);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSearch = findViewById(R.id.btnSearch);
        rvEvents = findViewById(R.id.rvEvents);

        setupSpinner();
        setupDateTimePickers();
        setupRecyclerView();
        setupSearchButton();
    }

    private String readInputStream(InputStream stream) throws IOException {
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }
    private void loadCities() {
        String[] cities;
        try {
            URI uri = new URI(url_base + "get_cities");
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String data = readInputStream(conn.getInputStream());
            cities = data.split(",");
        } catch (Exception e) {
            Log.e("Dan", e.toString());
            cities = new String[0];
        }
        String[] all_cities = new String[cities.length + 1];
        all_cities[0] = "Любой";
        for (int i = 0; i < cities.length; i++)
            all_cities[i + 1] = cities[i];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_spinner_item, all_cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
    }
    private void setupSpinner() {
        String[] cities = {"Любой"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCity.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loadCities();
            }
        }).start();
    }

    private void setupDateTimePickers() {
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate, startDateTimeCalendar));
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate, endDateTimeCalendar));
    }

    private void showDateTimePicker(EditText editText, Calendar calendar) {
        DatePickerDialog dateDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // Потом TimePicker
                    TimePickerDialog timeDialog = new TimePickerDialog(
                            this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                editText.setText(dateTimeFormat.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timeDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dateDialog.show();
    }


    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadEvents(String city, String start, String end) {
        try {
            String s_url = url_base + "get_events/?";
            if (!city.equals("Любой"))
                s_url += "city=" + URLEncoder.encode(city, StandardCharsets.UTF_8);
            if (!start.isEmpty())
                s_url += "&start=" + URLEncoder.encode(start, StandardCharsets.UTF_8);
            if (!end.isEmpty())
                s_url += "&end=" + URLEncoder.encode(end, StandardCharsets.UTF_8);

            Log.i("Dan", s_url);
            URI uri = new URI(s_url);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String data = readInputStream(conn.getInputStream());

            Gson gson = new Gson();
            List<Event> events = gson.fromJson(data, new TypeToken<List<Event>>(){}.getType());

            runOnUiThread(() -> {
                EventAdapter adapter = new EventAdapter(events);
                rvEvents.setAdapter(adapter);
            });

        } catch (Exception e) {
            Log.e("error", e.toString());
        }
    }
    private void setupSearchButton() {
        btnSearch.setOnClickListener(v -> {
            String selectedCity = spinnerCity.getSelectedItem().toString();
            String startDate = etStartDate.getText().toString();
            String endDate = etEndDate.getText().toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadEvents(selectedCity, startDate, endDate);
                }
            }).start();
        });
    }
}
