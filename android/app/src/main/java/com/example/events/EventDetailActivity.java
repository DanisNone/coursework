package com.example.events;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        TextView tvName = findViewById(R.id.tvEventName);
        TextView tvTime = findViewById(R.id.tvEventTime);
        TextView tvLocation = findViewById(R.id.tvEventLocation);

        tvName.setText(getIntent().getStringExtra("name"));
        tvTime.setText(getIntent().getStringExtra("time"));
        tvLocation.setText(getIntent().getStringExtra("location"));
    }
}
