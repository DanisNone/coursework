package com.example.events.UI;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimePickerHelper {
    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public static String toISO8601(String s) throws ParseException {
        java.util.Date date = dateFormat.parse(s);

        java.text.SimpleDateFormat isoFormat =
                new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        return isoFormat.format(date);
    }

    public static String removeTAndFormat(String isoDate) {
        if (isoDate == null) return null;

        if (isoDate.contains("T")) {
            return isoDate.replace('T', ' ');
        }

        return isoDate;
    }

    public static void clearDateTime(EditText editText) {
        editText.setText("");
    }


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

