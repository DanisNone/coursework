package com.example.events.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public class ProfileRepository {

    private static final String PREFS_NAME = "secure_profile";
    private static final String KEY_JWT = "jwt_token";

    private static ProfileRepository instance;

    private final MutableLiveData<String> JWToken = new MutableLiveData<>();
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

            // 🔹 загрузка токена при инициализации
            JWToken.setValue(securePrefs.getString(KEY_JWT, null));

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

    public LiveData<String> getJWToken() {
        return JWToken;
    }

    public void setJWToken(String value) {
        JWToken.postValue(value);

        securePrefs.edit()
                .putString(KEY_JWT, value)
                .apply();
    }

    public void logout() {
        JWToken.postValue(null);
        securePrefs.edit().remove(KEY_JWT).apply();
    }

    public boolean isLogged() {
        return JWToken.getValue() != null;
    }
}
