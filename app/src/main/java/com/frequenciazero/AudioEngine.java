package com.frequenciazero;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;

public class AudioEngine {
    private ToneGenerator tone;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean enabled;

    public AudioEngine(Context context, boolean enabled) {
        this.enabled = enabled;
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 48);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    public void playMessageCue() {
        play(ToneGenerator.TONE_PROP_ACK, 55);
        delayed(ToneGenerator.TONE_PROP_PROMPT, 90, 34); // Pequena cauda para parecer notificação de jogo.
    }

    public void playGlitch() {
        play(ToneGenerator.TONE_PROP_NACK, 90);
        delayed(ToneGenerator.TONE_DTMF_3, 55, 48);
        delayed(ToneGenerator.TONE_DTMF_8, 45, 105);
    }

    public void playLowEnd() {
        play(ToneGenerator.TONE_DTMF_0, 210);
        delayed(ToneGenerator.TONE_PROP_NACK, 65, 240);
    }

    public void playTap() {
        play(ToneGenerator.TONE_PROP_BEEP, 38);
    }

    public void release() {
        handler.removeCallbacksAndMessages(null);
        if (tone != null) {
            tone.release();
            tone = null;
        }
    }

    private void delayed(final int type, final int durationMs, int delayMs) {
        handler.postDelayed(new Runnable() {
            @Override public void run() { play(type, durationMs); }
        }, delayMs);
    }

    private void play(int type, int durationMs) {
        if (!enabled || tone == null) return;
        try {
            tone.startTone(type, durationMs);
        } catch (RuntimeException ignored) {
            // O áudio nunca pode derrubar o prólogo caso algum aparelho rejeite o tom.
        }
    }
}
