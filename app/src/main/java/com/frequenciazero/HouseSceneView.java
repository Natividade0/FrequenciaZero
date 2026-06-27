package com.frequenciazero;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class HouseSceneView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF radioRect = new RectF();
    private final RectF windowRect = new RectF();
    private final RectF kitchenRect = new RectF();
    private final RectF lampRect = new RectF();
    private final SharedPreferences prefs;
    private ToneGenerator tone;
    private float frequency = 2.84f;
    private boolean tuning;
    private boolean captured;

    public HouseSceneView(Context context) {
        this(context, null);
    }

    public HouseSceneView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(true);
        prefs = context.getSharedPreferences("chapter_one_house", Context.MODE_PRIVATE);
        frequency = prefs.getFloat("freq", 2.84f);
        captured = prefs.getBoolean("captured", false);
        try {
            tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 45);
        } catch (RuntimeException ignored) {
            tone = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (captured) {
            setPanel("O rádio não estava ligado na tomada.", "AMOSTRA: 03.17", prefs.getString("log", "03:17 — amostra local capturada"));
        } else {
            setPanel("Vértice não aparece no mapa desde a evacuação.", "RADIO: --.--", prefs.getString("log", "LOG: casa fria / sem rede"));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (tone != null) {
            tone.release();
            tone = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        radioRect.set(w * 0.40f, h * 0.54f, w * 0.78f, h * 0.80f);
        windowRect.set(w * 0.08f, h * 0.12f, w * 0.42f, h * 0.43f);
        kitchenRect.set(w * 0.07f, h * 0.49f, w * 0.35f, h * 0.80f);
        lampRect.set(w * 0.76f, h * 0.17f, w * 0.93f, h * 0.43f);
        drawBackground(canvas, w, h);
        drawWindow(canvas);
        drawKitchen(canvas);
        drawLamp(canvas);
        drawRadio(canvas);
        drawSignal(canvas, w, h);
        drawDust(canvas, w, h);
    }

    private void drawBackground(Canvas canvas, float w, float h) {
        paint.setShader(new LinearGradient(0, 0, w, h, Color.rgb(5, 14, 22), Color.rgb(2, 4, 7), Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        paint.setColor(Color.rgb(7, 8, 10));
        canvas.drawRect(0, h * 0.64f, w, h, paint);
        paint.setColor(Color.rgb(12, 15, 18));
        canvas.drawRect(w * 0.48f, h * 0.30f, w * 0.83f, h * 0.83f, paint);
        paint.setColor(Color.rgb(4, 5, 7));
        canvas.drawRect(w * 0.58f, h * 0.39f, w * 0.65f, h * 0.83f, paint);
    }

    private void drawWindow(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(windowRect.left, windowRect.top, windowRect.right, windowRect.bottom, Color.argb(170, 82, 151, 185), Color.argb(15, 15, 25, 35), Shader.TileMode.CLAMP));
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
        paint.setColor(Color.rgb(15, 15, 16));
        canvas.drawRect(kitchenRect, paint);
        paint.setColor(Color.rgb(45, 45, 45));
        for (int i = 0; i < 3; i++) {
            float y = kitchenRect.top + 45 + i * 44;
            canvas.drawOval(kitchenRect.left + 45, y, kitchenRect.right - 42, y + 15, paint);
        }
        paint.setColor(Color.rgb(7, 7, 8));
        canvas.drawRect(kitchenRect.left + 16, kitchenRect.bottom - 58, kitchenRect.right - 16, kitchenRect.bottom - 18, paint);
    }

    private void drawLamp(Canvas canvas) {
        float pulse = (float) (0.55f + Math.sin(System.currentTimeMillis() * 0.006) * 0.25f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb((int) (80 + pulse * 110), 120, 18, 25));
        canvas.drawCircle(lampRect.centerX(), lampRect.centerY(), lampRect.width() * 0.9f, paint);
        paint.setColor(Color.rgb(125, 22, 28));
        canvas.drawCircle(lampRect.centerX(), lampRect.centerY(), lampRect.width() * 0.18f, paint);
        postInvalidateDelayed(90);
    }

    private void drawRadio(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(11, 10, 10));
        canvas.drawRoundRect(radioRect, 12, 12, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(tuning || captured ? Color.rgb(112, 31, 35) : Color.rgb(47, 50, 52));
        canvas.drawRoundRect(radioRect, 12, 12, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(20, 22, 23));
        canvas.drawRect(radioRect.left + 26, radioRect.top + 26, radioRect.right - 26, radioRect.top + 70, paint);
        paint.setColor(isTuned() ? Color.rgb(210, 226, 230) : Color.rgb(82, 99, 105));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(28f);
        canvas.drawText(String.format(Locale.US, "%.2f", frequency), radioRect.centerX(), radioRect.top + 57, paint);
        paint.setColor(Color.rgb(35, 35, 35));
        canvas.drawCircle(radioRect.left + 48, radioRect.top + 108, 22f, paint);
        canvas.drawCircle(radioRect.right - 48, radioRect.top + 108, 24f, paint);
    }

    private void drawSignal(Canvas canvas, float w, float h) {
        if (!tuning && !captured) return;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(isTuned() ? 4f : 2f);
        paint.setColor(isTuned() ? Color.rgb(178, 217, 224) : Color.rgb(94, 34, 42));
        float mid = h * 0.46f;
        float amp = isTuned() ? 12f : 30f;
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
        if (event.getAction() == MotionEvent.ACTION_MOVE && tuning && !captured) {
            float ratio = Math.max(0f, Math.min(1f, event.getX() / Math.max(1f, getWidth())));
            frequency = 2.70f + ratio * 0.75f;
            if (isTuned()) {
                setPanel("O sinal estabilizou em 03.17.", "SINTONIA: 03.17", "03:17 — portadora encontrada");
                playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 80);
            } else {
                setPanel("Arraste devagar. Tem algo por baixo do ruído.", "SINTONIA: " + String.format(Locale.US, "%.2f", frequency), "LOG: sintonia manual");
            }
            save();
            invalidate();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleTap(event.getX(), event.getY());
            invalidate();
            return true;
        }
        return true;
    }

    private void handleTap(float x, float y) {
        playSoundEffect(SoundEffectConstants.CLICK);
        if (radioRect.contains(x, y)) {
            if (isTuned() && tuning && !captured) {
                captured = true;
                setPanel("...você voltou...", "AMOSTRA: 03.17", "03:17 — amostra local capturada");
                playTone(ToneGenerator.TONE_PROP_ACK, 160);
            } else {
                tuning = true;
                setPanel("O rádio responde antes do botão encostar.", "RADIO: " + String.format(Locale.US, "%.2f", frequency), "LOG: rádio inspecionado");
                playTone(ToneGenerator.TONE_CDMA_LOW_L, 120);
            }
        } else if (windowRect.contains(x, y)) {
            setPanel("Lá fora, nenhuma luz. Nenhum cachorro. Nenhum vento.", currentStatus(), currentLog());
        } else if (kitchenRect.contains(x, y)) {
            setPanel("Tudo ficou no lugar. Até os pratos.", currentStatus(), currentLog());
        } else if (lampRect.contains(x, y)) {
            setPanel("A lâmpada pulsa no mesmo ritmo do chiado.", currentStatus(), currentLog());
            playTone(ToneGenerator.TONE_CDMA_PIP, 80);
        } else if (captured) {
            setPanel("O rádio não estava ligado na tomada.", "AMOSTRA: 03.17", "03:17 — amostra local capturada");
        } else {
            setPanel("Vértice não aparece no mapa desde a evacuação.", currentStatus(), currentLog());
        }
        save();
    }

    private boolean isTuned() {
        return frequency > 3.12f && frequency < 3.22f;
    }

    private String currentStatus() {
        if (captured) return "AMOSTRA: 03.17";
        if (tuning) return "SINTONIA: " + String.format(Locale.US, "%.2f", frequency);
        return "RADIO: --.--";
    }

    private String currentLog() {
        return prefs.getString("log", "LOG: casa fria / sem rede");
    }

    private void save() {
        prefs.edit().putFloat("freq", frequency).putBoolean("captured", captured).putString("log", currentLog()).apply();
    }

    private void setPanel(String text, String status, String log) {
        prefs.edit().putString("log", log).apply();
        TextView narrative = getRootView().findViewById(R.id.narrativeText);
        TextView frequencyText = getRootView().findViewById(R.id.frequencyText);
        TextView techLog = getRootView().findViewById(R.id.techLog);
        if (narrative != null) narrative.setText(text);
        if (frequencyText != null) frequencyText.setText(status);
        if (techLog != null) techLog.setText(log);
    }

    private void playTone(int toneId, int durationMs) {
        if (tone != null) tone.startTone(toneId, durationMs);
    }
}
