package com.example.events.main;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.events.R;

public class EventDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_event_detail_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvName = findViewById(R.id.tvEventName);
        TextView tvOwnerInfo = findViewById(R.id.tvCreatorInfo);
        TextView tvTime = findViewById(R.id.tvEventTime);
        TextView tvLocation = findViewById(R.id.tvEventLocation);
        TextView tvEventDescription = findViewById(R.id.tvEventDescr);

        tvName.setText(getIntent().getStringExtra("name"));
        tvOwnerInfo.setText(getIntent().getStringExtra("ownerInfo"));
        tvTime.setText(getIntent().getStringExtra("time"));
        tvLocation.setText(getIntent().getStringExtra("location"));
        tvEventDescription.setText(getIntent().getStringExtra("description"));
    }
}
