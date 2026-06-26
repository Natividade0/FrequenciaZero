package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
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
        paint.setColor(getResources().getColor(R.color.fz_teal));
        paint.setStrokeWidth(3f);
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
        paint.setAlpha(active ? 230 : 120);
        for (int i = 0; i < 44; i++) {
            float x = 8f + i * (w - 16f) / 43f;
            float speed = active ? 0.010f : 0.002f;
            float amp = (float) Math.abs(Math.sin(i * 0.62f + now * speed)) * h * (active ? 0.42f : 0.18f);
            canvas.drawLine(x, mid - amp, x, mid + amp, paint);
        }
        postInvalidateDelayed(active ? 50 : 160);
    }
}
