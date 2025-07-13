package com.example.susach.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SoundSettings {
    private static final String PREF_NAME = "sound_settings";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_VOLUME = "volume";
    
    private SharedPreferences preferences;
    
    public SoundSettings(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public void setSoundEnabled(boolean enabled) {
        preferences.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }
    
    public boolean isSoundEnabled() {
        return preferences.getBoolean(KEY_SOUND_ENABLED, true); // Mặc định bật âm thanh
    }
    
    public void setVolume(float volume) {
        // Volume từ 0.0f đến 1.0f
        volume = Math.max(0.0f, Math.min(1.0f, volume));
        preferences.edit().putFloat(KEY_VOLUME, volume).apply();
    }
    
    public float getVolume() {
        return preferences.getFloat(KEY_VOLUME, 0.7f); // Mặc định 70% volume
    }
} 