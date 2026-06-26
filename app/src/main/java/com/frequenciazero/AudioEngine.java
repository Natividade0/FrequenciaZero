package com.frequenciazero;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.provider.Settings;

public class AudioEngine {
    private final Context context;
    private ToneGenerator tone;
    private boolean enabled = true;

    public AudioEngine(Context context) {
        this.context = context.getApplicationContext();
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 42);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void playMessage() {
        if (!enabled) return;
        playSystemCue(0.18f);
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_BEEP, 80);
    }

    public void playTransmission() {
        if (!enabled) return;
        if (tone != null) tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 120);
    }

    public void playNoise() {
        if (!enabled) return;
        if (tone != null) tone.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150);
    }

    public void playCapture() {
        if (!enabled) return;
        playSystemCue(0.26f);
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_ACK, 180);
    }

    public void release() {
        if (tone != null) {
            tone.release();
            tone = null;
        }
    }

    private void playSystemCue(float volume) {
        try {
            MediaPlayer player = MediaPlayer.create(context, Settings.System.DEFAULT_NOTIFICATION_URI);
            if (player == null) return;
            player.setVolume(volume, volume);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
            player.start();
        } catch (RuntimeException ignored) {
            // ToneGenerator remains the fallback when system sounds are unavailable.
        }
    }
}
