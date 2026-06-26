package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class CinematicBackdropView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mode;

    public CinematicBackdropView(Context context) {
        this(context, null);
    }

    public CinematicBackdropView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMode(int mode) {
        this.mode = mode;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        float pulse = (float) (0.5f + Math.sin(now * 0.0026f) * 0.5f);

        paint.setShader(new LinearGradient(0, 0, w, h,
                Color.rgb(5, 4, 4), Color.rgb(18 + mode * 2, 11, 8), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.18f, h * 0.18f, w * 0.85f,
                Color.argb(52, 127, 175, 146), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.86f, h * 0.30f, w * (0.45f + pulse * 0.10f),
                Color.argb(64, 224, 111, 47), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(22, 247, 238, 227));
        float scan = (now % 2400L) / 2400f * h;
        canvas.drawLine(0, scan, w, scan, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(42, 247, 238, 227));
        for (int i = 0; i < 34; i++) {
            float x = ((i * 97) % 1000) / 1000f * w;
            float y = (((i * 173) + (now / 22)) % 1000) / 1000f * h;
            canvas.drawCircle(x, y, 0.8f + (i % 3) * 0.45f, paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.argb(28 + (int) (pulse * 28), 224, 111, 47));
        for (int i = 0; i < 5; i++) {
            float y = h * (0.18f + i * 0.17f);
            canvas.drawLine(w * 0.05f, y, w * 0.95f, y + (float) Math.sin(now * 0.001f + i) * 10f, paint);
        }
        postInvalidateDelayed(33);
    }
}
