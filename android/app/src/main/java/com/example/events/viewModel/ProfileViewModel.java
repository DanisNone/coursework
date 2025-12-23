package com.example.events.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<String> JWToken = new MutableLiveData<>();

    public LiveData<String> getJWToken() {
        return JWToken;
    }
    public boolean isLogged() {
        return JWToken.getValue() != null;
    }
}
