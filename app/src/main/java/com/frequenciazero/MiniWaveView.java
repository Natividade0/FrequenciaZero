package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MiniWaveView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean active;

    public MiniWaveView(Context context) {
        this(context, null);
    }

    public MiniWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setActive(boolean active) {
        this.active = active;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float mid = h / 2f;
        long now = System.currentTimeMillis();

        drawCenterLine(canvas, w, mid);
        drawWave(canvas, w, h, mid, now, true);
        drawWave(canvas, w, h, mid, now + 220, false);
        drawSignalNoise(canvas, w, h, now);

        postInvalidateDelayed(active ? 33 : 120);
    }

    private void drawCenterLine(Canvas canvas, float w, float mid) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(active ? 65 : 38, 232, 236, 234));
        canvas.drawLine(6, mid, w - 6, mid, paint);
    }

    private void drawWave(Canvas canvas, float w, float h, float mid, long now, boolean primary) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(primary ? 3.2f : 1.4f);
        if (primary) paint.setColor(Color.argb(active ? 230 : 126, 59, 175, 159));
        else paint.setColor(Color.argb(active ? 110 : 45, 217, 155, 74));

        int count = 56;
        for (int i = 0; i < count; i++) {
            float x = 8f + i * (w - 16f) / (count - 1f);
            float speed = active ? 0.012f : 0.002f;
            float irregular = (float) Math.sin(i * 1.73f) * 0.35f + 0.75f;
            float envelope = 0.40f + (float) Math.sin(i * 0.31f + now * 0.001f) * 0.22f;
            float amp = Math.abs((float) Math.sin(i * 0.58f + now * speed)) * h * envelope * irregular;
            amp *= active ? 0.43f : 0.16f;
            canvas.drawLine(x, mid - amp, x, mid + amp, paint);
        }
    }

    private void drawSignalNoise(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(active ? 82 : 32, 232, 236, 234));
        for (int i = 0; i < 18; i++) {
            float x = ((i * 97 + now / 17) % 1000) / 1000f * w;
            float y = ((i * 151 + now / 29) % 1000) / 1000f * h;
            canvas.drawCircle(x, y, active ? 1.4f : 0.8f, paint);
        }
    }
}
