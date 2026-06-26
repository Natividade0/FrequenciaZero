package com.frequenciazero;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class AudioEngine {
    private ToneGenerator tone;
    private boolean enabled;

    public AudioEngine(Context context, boolean enabled) {
        this.enabled = enabled;
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 42);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    public void playMessageCue() {
        play(ToneGenerator.TONE_PROP_ACK, 70);
    }

    public void playGlitch() {
        play(ToneGenerator.TONE_PROP_NACK, 120);
    }

    public void playLowEnd() {
        play(ToneGenerator.TONE_DTMF_0, 180);
    }

    public void playTap() {
        play(ToneGenerator.TONE_PROP_BEEP, 45);
    }

    public void release() {
        if (tone != null) {
            tone.release();
            tone = null;
        }
    }

    private void play(int type, int durationMs) {
        if (!enabled || tone == null) return;
        try {
            tone.startTone(type, durationMs);
        } catch (RuntimeException ignored) {
            // Audio feedback must never interrupt the prologue.
        }
    }
}
