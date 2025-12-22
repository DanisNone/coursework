package com.example.events.viewModel;

import static com.example.events.network.ApiClient.httpGet;
import static com.example.events.network.ApiConfig.ANY_CITY;
import static com.example.events.network.ApiConfig.BASE_URL;

import android.os.SystemClock;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;

public class CitiesViewModel extends ViewModel {
    private boolean loaded = false;
    private final MutableLiveData<List<String>> cities = new MutableLiveData<>(List.of(ANY_CITY));
    public LiveData<List<String>> getCities() {
        return cities;
    }
    public void loadCities() {
        if (loaded)
            return;
        while (true) {
            try {
                String data = httpGet(BASE_URL + "get_cities");
                Gson gson = new Gson();
                String[] c = gson.fromJson(data, new TypeToken<String[]>(){}.getType());
                cities.postValue(Arrays.asList(c));
                break;
            } catch (Exception e) {
                Log.i("CitiesViewModel", "fail load cities: " + e.toString());
            }
            SystemClock.sleep(1000);
        }
        loaded = true;
    }
}
