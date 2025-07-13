package com.example.susach.managers;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundManager {
    private static final String TAG = "SoundManager";
    private Context context;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private MediaPlayer finishSound;
    private SoundSettings soundSettings;

    public SoundManager(Context context) {
        this.context = context;
        this.soundSettings = new SoundSettings(context);
        initializeSounds();
    }

    private void initializeSounds() {
        try {
            // Khởi tạo âm thanh cho đáp án đúng
            correctSound = MediaPlayer.create(context, com.example.susach.R.raw.correct_answer);
            correctSound.setVolume(soundSettings.getVolume(), soundSettings.getVolume());

            // Khởi tạo âm thanh cho đáp án sai
            wrongSound = MediaPlayer.create(context, com.example.susach.R.raw.wrong_answer);
            wrongSound.setVolume(soundSettings.getVolume(), soundSettings.getVolume());

            // Khởi tạo âm thanh cho hoàn thành quiz
            finishSound = MediaPlayer.create(context, com.example.susach.R.raw.finish_quiz);
            finishSound.setVolume(soundSettings.getVolume(), soundSettings.getVolume());

        } catch (Exception e) {
            Log.e(TAG, "Error initializing sounds: " + e.getMessage());
        }
    }

    public void playCorrectSound() {
        if (soundSettings.isSoundEnabled() && correctSound != null) {
            try {
                if (correctSound.isPlaying()) {
                    correctSound.stop();
                }
                correctSound.seekTo(0);
                correctSound.start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing correct sound: " + e.getMessage());
            }
        }
    }

    public void playWrongSound() {
        if (soundSettings.isSoundEnabled() && wrongSound != null) {
            try {
                if (wrongSound.isPlaying()) {
                    wrongSound.stop();
                }
                wrongSound.seekTo(0);
                wrongSound.start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing wrong sound: " + e.getMessage());
            }
        }
    }

    public void playFinishSound() {
        if (soundSettings.isSoundEnabled() && finishSound != null) {
            try {
                if (finishSound.isPlaying()) {
                    finishSound.stop();
                }
                finishSound.seekTo(0);
                finishSound.start();
            } catch (Exception e) {
                Log.e(TAG, "Error playing finish sound: " + e.getMessage());
            }
        }
    }

    public void setSoundEnabled(boolean enabled) {
        soundSettings.setSoundEnabled(enabled);
    }

    public boolean isSoundEnabled() {
        return soundSettings.isSoundEnabled();
    }

    public void setVolume(float volume) {
        soundSettings.setVolume(volume);
        // Cập nhật volume cho các MediaPlayer hiện tại
        if (correctSound != null) {
            correctSound.setVolume(volume, volume);
        }
        if (wrongSound != null) {
            wrongSound.setVolume(volume, volume);
        }
        if (finishSound != null) {
            finishSound.setVolume(volume, volume);
        }
    }

    public float getVolume() {
        return soundSettings.getVolume();
    }

    public void release() {
        try {
            if (correctSound != null) {
                correctSound.release();
                correctSound = null;
            }
            if (wrongSound != null) {
                wrongSound.release();
                wrongSound = null;
            }
            if (finishSound != null) {
                finishSound.release();
                finishSound = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error releasing sounds: " + e.getMessage());
        }
    }
} 