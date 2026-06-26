package com.frequenciazero;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class HouseSceneView extends View {
    private static final String PREFS = "chapter_one_house";
    private static final String KEY_RADIO = "radio_inspected";
    private static final String KEY_CAPTURED = "signal_captured";
    private static final String KEY_FREQ = "approx_frequency";
    private static final String KEY_LOG = "technical_log";

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF kitchenRect = new RectF();
    private final RectF windowRect = new RectF();
    private final RectF lampRect = new RectF();
    private final RectF radioRect = new RectF();
    private final RectF dialRect = new RectF();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SharedPreferences prefs;
    private ToneGenerator tone;

    private TextView narrativeText;
    private TextView frequencyText;
    private TextView techLog;
    private boolean radioInspected;
    private boolean signalCaptured;
    private boolean tuning;
    private boolean dragging;
    private float downX;
    private float downY;
    private float frequency = 2.84f;
    private long lastGlitchAt;
    private String log;

    public HouseSceneView(Context context) {
        this(context, null);
    }

    public HouseSceneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setSoundEffectsEnabled(true);
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        radioInspected = prefs.getBoolean(KEY_RADIO, false);
        signalCaptured = prefs.getBoolean(KEY_CAPTURED, false);
        frequency = prefs.getFloat(KEY_FREQ, 2.84f);
        log = prefs.getString(KEY_LOG, "LOG: casa fria / sem rede");
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 45);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        narrativeText = getRootView().findViewById(R.id.narrativeText);
        frequencyText = getRootView().findViewById(R.id.frequencyText);
        techLog = getRootView().findViewById(R.id.techLog);
        if (signalCaptured) {
            show("O rádio não estava ligado na tomada.");
        } else {
            show("Vértice não aparece no mapa desde a evacuação.");
        }
        updateFrequencyText();
        updateLog(log);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (tone != null) {
            tone.release();
            tone = null;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        updateHotspots(w, h);
        drawRoom(canvas, w, h);
        drawColdWindow(canvas);
        drawKitchen(canvas);
        drawRedLamp(canvas);
        drawRadio(canvas);
        drawSignal(canvas, w, h);
        drawDust(canvas, w, h);
    }

    private void updateHotspots(float w, float h) {
        kitchenRect.set(w * 0.08f, h * 0.48f, w * 0.38f, h * 0.78f);
        windowRect.set(w * 0.08f, h * 0.12f, w * 0.43f, h * 0.44f);
        lampRect.set(w * 0.73f, h * 0.15f, w * 0.94f, h * 0.45f);
        radioRect.set(w * 0.39f, h * 0.52f, w * 0.77f, h * 0.79f);
        dialRect.set(radioRect.left + 34, radioRect.bottom - 58, radioRect.right - 34, radioRect.bottom - 24);
    }

    private void drawRoom(Canvas canvas, float w, float h) {
        paint.setShader(new LinearGradient(0, 0, w, h, Color.rgb(5, 14, 22), Color.rgb(2, 4, 7), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(8, 9, 12));
        Path floor = new Path();
        floor.moveTo(0, h * 0.64f);
        floor.lineTo(w, h * 0.58f);
        floor.lineTo(w, h);
        floor.lineTo(0, h);
        floor.close();
        canvas.drawPath(floor, paint);

        paint.setColor(Color.rgb(12, 15, 18));
        canvas.drawRect(w * 0.45f, h * 0.30f, w * 0.82f, h * 0.80f, paint);
        paint.setColor(Color.rgb(19, 21, 24));
        canvas.drawRect(w * 0.49f, h * 0.35f, w * 0.78f, h * 0.80f, paint);
        paint.setColor(Color.rgb(4, 5, 7));
        canvas.drawRect(w * 0.56f, h * 0.41f, w * 0.64f, h * 0.80f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.rgb(30, 35, 39));
        for (int i = 0; i < 7; i++) {
            float y = h * (0.68f + i * 0.045f);
            canvas.drawLine(0, y, w, y - h * 0.08f, paint);
        }
    }

    private void drawColdWindow(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(6, 19, 30));
        canvas.drawRect(windowRect, paint);
        paint.setShader(new LinearGradient(windowRect.left, windowRect.top, windowRect.right, windowRect.bottom,
                Color.argb(170, 82, 151, 185), Color.argb(10, 25, 42, 56), Shader.TileMode.CLAMP));
        canvas.drawRect(windowRect, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setColor(Color.rgb(30, 42, 50));
        canvas.drawRect(windowRect, paint);
        canvas.drawLine(windowRect.centerX(), windowRect.top, windowRect.centerX(), windowRect.bottom, paint);
        canvas.drawLine(windowRect.left, windowRect.centerY(), windowRect.right, windowRect.centerY(), paint);
    }

    private void drawKitchen(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(13, 13, 14));
        canvas.drawRect(kitchenRect, paint);
        paint.setColor(Color.rgb(24, 24, 24));
        canvas.drawRect(kitchenRect.left + 14, kitchenRect.top + 22, kitchenRect.right - 18, kitchenRect.bottom - 16, paint);
        paint.setColor(Color.rgb(54, 55, 55));
        for (int i = 0; i < 3; i++) {
            float y = kitchenRect.top + 54 + i * 46;
            canvas.drawOval(kitchenRect.left + 48, y, kitchenRect.right - 50, y + 16, paint);
        }
        paint.setColor(Color.rgb(8, 8, 9));
        canvas.drawRect(kitchenRect.left + 20, kitchenRect.bottom - 64, kitchenRect.right - 20, kitchenRect.bottom - 22, paint);
    }

    private void drawRedLamp(Canvas canvas) {
        float pulse = (float) (0.55f + Math.sin(System.currentTimeMillis() * 0.006) * 0.25f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb((int) (80 + pulse * 110), 120, 18, 25));
        canvas.drawCircle(lampRect.centerX(), lampRect.centerY(), lampRect.width() * 0.9f, paint);
        paint.setColor(Color.rgb(120, 22, 26));
        canvas.drawCircle(lampRect.centerX(), lampRect.centerY(), lampRect.width() * 0.18f, paint);
        postInvalidateDelayed(80);
    }

    private void drawRadio(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(11, 10, 10));
        canvas.drawRoundRect(radioRect, 10, 10, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(radioInspected ? Color.rgb(112, 31, 35) : Color.rgb(47, 50, 52));
        canvas.drawRoundRect(radioRect, 10, 10, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(20, 22, 23));
        canvas.drawRect(radioRect.left + 26, radioRect.top + 26, radioRect.right - 26, radioRect.top + 70, paint);
        paint.setColor(isTuned() ? Color.rgb(177, 206, 214) : Color.rgb(82, 99, 105));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28f);
        canvas.drawText(String.format(Locale.US, "%.2f", frequency), radioRect.centerX(), radioRect.top + 57, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setColor(Color.rgb(75, 79, 82));
        canvas.drawLine(dialRect.left, dialRect.centerY(), dialRect.right, dialRect.centerY(), paint);
        float dialX = dialRect.left + ((frequency - 2.70f) / 0.75f) * dialRect.width();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(isTuned() ? Color.rgb(210, 226, 230) : Color.rgb(108, 30, 34));
        canvas.drawCircle(dialX, dialRect.centerY(), 13f, paint);

        paint.setColor(Color.rgb(21, 21, 22));
        canvas.drawCircle(radioRect.right - 46, radioRect.top + 106, 24f, paint);
        paint.setColor(Color.rgb(35, 35, 35));
        canvas.drawCircle(radioRect.left + 46, radioRect.top + 106, 22f, paint);
    }

    private void drawSignal(Canvas canvas, float w, float h) {
        if (!tuning && !signalCaptured) return;
        float amp = isTuned() ? 13f : 31f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(isTuned() ? 4f : 2f);
        paint.setColor(isTuned() ? Color.rgb(178, 217, 224) : Color.rgb(94, 34, 42));
        float mid = h * 0.46f;
        for (int i = 0; i < 46; i++) {
            float x1 = w * 0.16f + i * (w * 0.68f) / 46f;
            float x2 = w * 0.16f + (i + 1) * (w * 0.68f) / 46f;
            float y1 = mid + (float) Math.sin(i * 0.65f + frequency * 5f) * amp;
            float y2 = mid + (float) Math.sin((i + 1) * 0.65f + frequency * 5f) * amp;
            canvas.drawLine(x1, y1, x2, y2, paint);
        }
    }

    private void drawDust(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(60, 190, 205, 210));
        for (int i = 0; i < 28; i++) {
            float x = ((i * 83) % 1000) / 1000f * w;
            float y = ((i * 151) % 1000) / 1000f * h * 0.76f;
            canvas.drawCircle(x, y, 1.2f + (i % 3), paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
            dragging = false;
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (tuning || radioRect.contains(downX, downY)) {
                dragging = true;
                tuning = true;
                updateFrequency(event.getX());
                if (isTuned()) {
                    show("O sinal estabilizou em 03.17.");
                    playGlitchNearSignal();
                } else {
                    show("Arraste devagar. Tem algo por baixo do ruído.");
                }
                saveState();
                invalidate();
                return true;
            }
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleTap(event.getX(), event.getY());
            invalidate();
            return true;
        }
        return true;
    }

    private void handleTap(float x, float y) {
        if (tuning && !dragging && isTuned() && !signalCaptured) {
            captureSignal();
            return;
        }
        if (radioRect.contains(x, y)) {
            radioInspected = true;
            tuning = true;
            show("O rádio responde antes do botão encostar.");
            updateLog("LOG: radio inspecionado / portadora baixa");
            playRadioBeep();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (tuning && !signalCaptured) show("Arraste devagar. Tem algo por baixo do ruído.");
                }
            }, 900);
        } else if (kitchenRect.contains(x, y)) {
            show("Tudo ficou no lugar. Até os pratos.");
            updateLog("LOG: cozinha intacta / poeira interrompida");
            playDryClick();
        } else if (windowRect.contains(x, y)) {
            show("Lá fora, nenhuma luz. Nenhum cachorro. Nenhum vento.");
            updateLog("LOG: exterior sem movimento");
            playDryClick();
        } else if (lampRect.contains(x, y)) {
            show("A lâmpada pulsa no mesmo ritmo do chiado.");
            updateLog("LOG: pulso vermelho sincronizado");
            playGlitch();
        } else if (tuning && !dragging && isTuned() && signalCaptured) {
            show("O rádio não estava ligado na tomada.");
            playDryClick();
        } else {
            playDryClick();
        }
        saveState();
    }

    private void updateFrequency(float x) {
        float clamped = Math.max(dialRect.left, Math.min(dialRect.right, x));
        float amount = (clamped - dialRect.left) / Math.max(1f, dialRect.width());
        frequency = 2.70f + amount * 0.75f;
        updateFrequencyText();
        updateLog(isTuned() ? "LOG: freq 03.17 / sinal estavel" : "LOG: varredura " + String.format(Locale.US, "%.2f", frequency));
    }

    private void captureSignal() {
        signalCaptured = true;
        tuning = false;
        frequency = 3.17f;
        updateFrequencyText();
        show("...você voltou...");
        updateLog("LOG: amostra capturada / origem interna");
        playCaptureSound();
        saveState();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                show("O rádio não estava ligado na tomada.");
                updateLog("LOG: radio sem tomada / sinal persistente");
                saveState();
            }
        }, 1700);
    }

    private boolean isTuned() {
        return Math.abs(frequency - 3.17f) <= 0.025f;
    }

    private void show(String text) {
        if (narrativeText != null) narrativeText.setText(text);
    }

    private void updateFrequencyText() {
        if (frequencyText == null) return;
        String status = signalCaptured ? "AMOSTRA" : tuning ? "SINTONIA" : "RADIO";
        frequencyText.setText(status + ": " + String.format(Locale.US, "%.2f", frequency));
    }

    private void updateLog(String value) {
        log = value;
        if (techLog != null) techLog.setText(log);
    }

    private void saveState() {
        prefs.edit()
                .putBoolean(KEY_RADIO, radioInspected)
                .putBoolean(KEY_CAPTURED, signalCaptured)
                .putFloat(KEY_FREQ, frequency)
                .putString(KEY_LOG, log)
                .apply();
    }

    private void playDryClick() {
        playSoundEffect(SoundEffectConstants.CLICK);
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_CLICK, 70);
    }

    private void playRadioBeep() {
        if (tone != null) tone.startTone(ToneGenerator.TONE_CDMA_LOW_L, 120);
    }

    private void playGlitchNearSignal() {
        long now = System.currentTimeMillis();
        if (now - lastGlitchAt > 260) {
            lastGlitchAt = now;
            playGlitch();
        }
    }

    private void playGlitch() {
        playWave(0.16, 170, 52, true);
    }

    private void playCaptureSound() {
        playWave(0.26, 260, 110, false);
        if (tone != null) tone.startTone(ToneGenerator.TONE_PROP_ACK, 160);
    }

    private void playWave(double seconds, int startHz, int endHz, boolean noisy) {
        final int sampleRate = 8000;
        final int samples = Math.max(1, (int) (sampleRate * seconds));
        final short[] data = new short[samples];
        for (int i = 0; i < samples; i++) {
            double t = i / (double) sampleRate;
            double sweep = startHz + (endHz - startHz) * (i / (double) samples);
            double wave = Math.sin(2.0 * Math.PI * sweep * t);
            if (noisy) wave += (((i * 73) % 41) - 20) / 24.0;
            data[i] = (short) (Math.max(-1.0, Math.min(1.0, wave)) * 6000);
        }
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                data.length * 2, AudioTrack.MODE_STATIC);
        track.write(data, 0, data.length);
        track.play();
    }
}
