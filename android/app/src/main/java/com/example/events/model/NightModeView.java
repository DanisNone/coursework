package com.example.events.model;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModel;

public class NightModeView extends ViewModel {

    private int mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
