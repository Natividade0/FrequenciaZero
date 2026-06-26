package com.frequenciazero;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class AudioEngine {
    private ToneGenerator tone;
    private boolean enabled = true;

    public AudioEngine(Context context) {
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 38);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void playClick() {
        playTone(ToneGenerator.TONE_PROP_BEEP, 45);
    }

    public void playMessage() {
        playTone(ToneGenerator.TONE_PROP_ACK, 75);
    }

    public void playTransmission() {
        playTone(ToneGenerator.TONE_SUP_DIAL, 110);
    }

    public void playNoise() {
        playTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 150);
    }

    public void playCapture() {
        playTone(ToneGenerator.TONE_PROP_PROMPT, 210);
    }

    public void release() {
        if (tone != null) {
            tone.release();
            tone = null;
        }
    }

    private void playTone(int toneType, int durationMs) {
        if (!enabled || tone == null) return;
        try {
            tone.startTone(toneType, durationMs);
        } catch (RuntimeException ignored) {
            // Devices can refuse short generated tones; silence is safer than a crash.
        }
    }
}
