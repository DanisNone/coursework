package com.example.events.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class ProfileRepository {

    private static final String PREFS_NAME = "secure_profile";
    private static final String KEY_LOGIN = "PROFILE_LOGIN";
    private static final String KEY_PASSWORD = "PROFILE_PASSWORD";

    private static ProfileRepository instance;

    private String login;
    private String password;
    private final SharedPreferences securePrefs;

    private ProfileRepository(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            securePrefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            login = securePrefs.getString(KEY_LOGIN, null);
            password = securePrefs.getString(KEY_PASSWORD, null);

        } catch (Exception e) {
            throw new RuntimeException("Secure storage init failed", e);
        }
    }

    public static synchronized ProfileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void setLogin(String value) {
        login = value;
        securePrefs.edit()
                .putString(KEY_LOGIN, value)
                .apply();
    }

    public void setPassword(String value) {
        password = value;
        securePrefs.edit()
                .putString(KEY_PASSWORD, value)
                .apply();
    }

    public String getLogin() {
        return login;
    }
    public void logout() {
        login = null;
        password = null;
        securePrefs.edit().remove(KEY_PASSWORD).apply();
        securePrefs.edit().remove(KEY_LOGIN).apply();
    }

    public boolean isLogged() {
        return login != null && password != null;
    }

    public String getPassword() {
        return password;
    }
}
