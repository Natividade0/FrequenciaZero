package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class SpectrogramView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF frame = new RectF();
    private float frequency = 2.84f;
    private boolean captured;

    public SpectrogramView(Context context) {
        this(context, null);
    }

    public SpectrogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
        invalidate();
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        frame.set(2, 2, w - 2, h - 2);

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, w, h,
                Color.rgb(10, 9, 9), Color.rgb(27, 20, 16), Shader.TileMode.CLAMP));
        canvas.drawRoundRect(frame, 12, 12, paint);
        paint.setShader(null);

        drawGrid(canvas, w, h);
        drawHeat(canvas, w, h);
        drawCarrier(canvas, w, h);
        drawLabels(canvas, w, h);
        if (!captured) postInvalidateDelayed(90);
    }

    private void drawGrid(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.rgb(58, 48, 40));
        for (int i = 1; i < 8; i++) {
            float x = i * w / 8f;
            canvas.drawLine(x, 0, x, h, paint);
        }
        for (int i = 1; i < 6; i++) {
            float y = i * h / 6f;
            canvas.drawLine(0, y, w, y, paint);
        }
    }

    private void drawHeat(Canvas canvas, float w, float h) {
        float drift = (System.currentTimeMillis() % 1200L) / 1200f;
        for (int i = 0; i < 54; i++) {
            float x = 10 + i * (w - 20) / 54f;
            float noise = (float) Math.abs(Math.sin(i * 0.47f + drift * 4f + frequency));
            int alpha = 26 + (int) (noise * 80);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(alpha, 216, 116, 50));
            float top = h * (0.18f + noise * 0.48f);
            canvas.drawRect(x, top, x + 3, h - 16, paint);
        }
    }

    private void drawCarrier(Canvas canvas, float w, float h) {
        float lock = Math.max(0f, 1f - Math.abs(frequency - 3.17f) / 0.20f);
        float y = h * (0.72f - lock * 0.42f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f + lock * 3f);
        paint.setColor(captured ? Color.rgb(244, 237, 226) : Color.rgb(111, 155, 135));
        for (int i = 0; i < 42; i++) {
            float x1 = 16 + i * (w - 32) / 42f;
            float x2 = 16 + (i + 1) * (w - 32) / 42f;
            float n1 = (float) Math.sin(i * 0.82f + frequency * 3f) * (24f - lock * 18f);
            float n2 = (float) Math.sin((i + 1) * 0.82f + frequency * 3f) * (24f - lock * 18f);
            canvas.drawLine(x1, y + n1, x2, y + n2, paint);
        }
    }

    private void drawLabels(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(12f);
        paint.setColor(Color.rgb(169, 158, 144));
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("LOW BAND", 14, 22, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.US, "%.2f", frequency), w - 14, h - 16, paint);
        if (captured) {
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(18f);
            paint.setColor(Color.rgb(244, 237, 226));
            canvas.drawText("AMOSTRA FIXADA", w * 0.5f, h * 0.52f, paint);
        }
    }
}
