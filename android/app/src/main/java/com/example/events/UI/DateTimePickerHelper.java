package com.example.events.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerHelper {
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public static void setupDateTimePicker(
            Context context,
            EditText editText,
            Calendar calendar) {

        editText.setOnClickListener(v ->
                showDateTimePicker(context, editText, calendar));
    }

    public static void showDateTimePicker(
            Context context,
            EditText target,
            Calendar calendar) {

        new DatePickerDialog(
                context,
                (d, y, m, day) -> {
                    calendar.set(y, m, day);

                    new TimePickerDialog(
                            context,
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
}

