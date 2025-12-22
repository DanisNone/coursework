package com.example.events.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.events.UI.DateTimePickerHelper;
import com.example.events.model.Event;
import java.util.List;

public class EventsViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        for (Event event: events) {
            event.setStartTime(DateTimePickerHelper.removeTAndFormat(event.getStartTime()));
            event.setEndTime(DateTimePickerHelper.removeTAndFormat(event.getEndTime()));

            String description = event.getDescription();
            event.setDescription(description.replaceFirst("^[\\s,.]+", ""));
        }
        this.events.postValue(events);
    }
}
