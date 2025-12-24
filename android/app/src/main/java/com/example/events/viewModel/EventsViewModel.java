package com.example.events.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.events.UI.DateTimePickerHelper;
import com.example.events.model.Event;
import com.example.events.model.PublicEvent;

import java.util.List;

public class EventsViewModel extends ViewModel {
    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();

    private final MutableLiveData<List<PublicEvent>> publicEvents = new MutableLiveData<>();

    public LiveData<List<Event>> getEvents() {
        return events;
    }

    public LiveData<List<PublicEvent>> getPubEvents() {
        return publicEvents;
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

    public void setPublicEvents(List<PublicEvent> events) {
        this.publicEvents.postValue(events);
    }
}
