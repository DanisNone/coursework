package com.example.events.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.events.UI.DateTimePickerHelper;
import com.example.events.model.Event;
import com.example.events.model.PublicEvent;
import com.example.events.model.PublicUser;
import com.example.events.network.ApiClient;

import java.util.ArrayList;
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

    public void setPublicEvents(List<Event> events) {
        List<PublicEvent> pubEventList = new ArrayList<PublicEvent>();

        // Счетчик для отслеживания завершения всех запросов
        final int[] pendingRequests = {events.size()};

        if (events.isEmpty()) {
            publicEvents.postValue(pubEventList);
            return;
        }

        for (Event event: events) {
            Event processedEvent = new Event(event);

            processedEvent.setStartTime(DateTimePickerHelper.removeTAndFormat(event.getStartTime()));
            processedEvent.setEndTime(DateTimePickerHelper.removeTAndFormat(event.getEndTime()));

            String description = event.getDescription();
            if (description != null) {
                processedEvent.setDescription(description.replaceFirst("^[\\s,.]+", ""));
            }

            ApiClient.getUserAsync(event.getOwnerId(), new ApiClient.UserCallback() {
                @Override
                public void onSuccess(PublicUser user) {
                    synchronized (pubEventList) {
                        pubEventList.add(new PublicEvent(processedEvent, user));

                        pendingRequests[0]--;
                        if (pendingRequests[0] == 0) {
                            publicEvents.postValue(pubEventList);
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    // Добавляем event с пустым пользователем
                    synchronized (pubEventList) {
                        pubEventList.add(new PublicEvent(processedEvent, null));

                        pendingRequests[0]--;
                        if (pendingRequests[0] == 0) {
                            publicEvents.postValue(pubEventList);
                        }
                    }
                }
            });
        }
    }
}
