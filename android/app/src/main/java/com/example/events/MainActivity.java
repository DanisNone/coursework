package com.example.events;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Event {
    public String startTime;
    public String endTime;
    public String full_location;
    public String city;
    public String name;
    public String descr;
}

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EventsApp";
    private static final String BASE_URL = "http://193.108.113.136:8080/";
    private static final String ANY_CITY = "Любой";


    private Spinner spinnerCity;
    private EditText etStartDate, etEndDate;
    private Button btnSearch;
    private RecyclerView rvEvents;


    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setupDateTimePickers();
        setupSearchButton();
        loadCities();
    }


    private void initViews() {
        spinnerCity = findViewById(R.id.spinnerCity);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);
        btnSearch = findViewById(R.id.btnSearch);
        rvEvents = findViewById(R.id.rvEvents);

        setSpinnerData(new String[]{ANY_CITY});
    }

    private void setupRecyclerView() {
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupDateTimePickers() {
        etStartDate.setOnClickListener(v ->
                showDateTimePicker(etStartDate, startCalendar));

        etEndDate.setOnClickListener(v ->
                showDateTimePicker(etEndDate, endCalendar));
    }

    private void showDateTimePicker(EditText target, Calendar calendar) {
        new DatePickerDialog(
                this,
                (d, y, m, day) -> {
                    calendar.set(y, m, day);

                    new TimePickerDialog(
                            this,
                            (t, h, min) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, h);
                                calendar.set(Calendar.MINUTE, min);
                                target.setText(dateFormat.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
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
            try {
                String data = httpGet(BASE_URL + "get_cities");
                String[] cities = data.split(",");

                String[] result = new String[cities.length + 1];
                result[0] = ANY_CITY;
                System.arraycopy(cities, 0, result, 1, cities.length);

                runOnUiThread(() -> setSpinnerData(result));

            } catch (Exception e) {
                Log.e(TAG, "loadCities", e);
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

    private void loadEvents(String city, String start, String end) {
        executor.execute(() -> {
            try {
                String url = buildEventsUrl(city, start, end);
                Log.i(TAG, url);

                List<Event> events = gson.fromJson(
                        httpGet(url),
                        new TypeToken<List<Event>>() {}.getType()
                );

                runOnUiThread(() ->
                        rvEvents.setAdapter(new EventAdapter(events))
                );

            } catch (Exception e) {
                Log.e(TAG, "loadEvents", e);
            }
        });
    }

    private String buildEventsUrl(String city, String start, String end) {
        Uri.Builder builder = Uri.parse(BASE_URL + "get_events/").buildUpon();

        if (!ANY_CITY.equals(city)) {
            builder.appendQueryParameter("city", city);
        }
        if (!start.isEmpty()) {
            builder.appendQueryParameter("start", start);
        }
        if (!end.isEmpty()) {
            builder.appendQueryParameter("end", end);
        }
        return builder.build().toString();
    }



    private String httpGet(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream is = conn.getInputStream();
             Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            StringBuilder sb = new StringBuilder();
            char[] buf = new char[1024];
            int n;
            while ((n = reader.read(buf)) > 0) {
                sb.append(buf, 0, n);
            }
            return sb.toString();
        }
    }
}
