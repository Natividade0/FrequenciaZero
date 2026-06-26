package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class LockScreenView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LockScreenView(Context context) {
        this(context, null);
    }

    public LockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        float pulse = (float) (0.5f + Math.sin(now * 0.002f) * 0.5f);

        drawWallpaper(canvas, w, h, pulse);
        drawRain(canvas, w, h, now);
        drawPhoneGlass(canvas, w, h);
        drawScan(canvas, w, h, now);

        postInvalidateDelayed(50);
    }

    private void drawWallpaper(Canvas canvas, float w, float h, float pulse) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{Color.rgb(2, 4, 5), Color.rgb(5, 20, 26), Color.rgb(1, 2, 3)},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.20f, h * 0.16f, w * 0.72f,
                Color.argb(58, 59, 175, 159), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        float towerX = w * 0.68f;
        float top = h * 0.42f;
        float bottom = h * 0.63f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.4f);
        paint.setColor(Color.argb(165, 35, 61, 66));
        canvas.drawLine(towerX, bottom, towerX, top, paint);
        canvas.drawLine(towerX, top, towerX - 18, bottom, paint);
        canvas.drawLine(towerX, top, towerX + 18, bottom, paint);
        canvas.drawLine(towerX - 14, h * 0.50f, towerX + 14, h * 0.50f, paint);
        paint.setColor(Color.argb(115 + (int) (pulse * 85), 181, 64, 58));
        canvas.drawCircle(towerX, top, 5 + pulse * 3, paint);
        canvas.drawCircle(towerX, top, 18 + pulse * 8, paint);

        paint.setStyle(Paint.Style.FILL);
        for (int layer = 0; layer < 3; layer++) {
            paint.setColor(Color.argb(170 - layer * 35, 2, 8 + layer * 3, 10 + layer * 5));
            for (int i = 0; i < 20; i++) {
                float x = ((i * 89 + layer * 37) % 1000) / 1000f * w;
                float base = h * (0.66f + layer * 0.08f);
                float size = 42 + (i % 4) * 15 + layer * 10;
                Path tree = new Path();
                tree.moveTo(x, base - size);
                tree.lineTo(x - size * 0.34f, base);
                tree.lineTo(x + size * 0.34f, base);
                tree.close();
                canvas.drawPath(tree, paint);
            }
        }
    }

    private void drawRain(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(44, 183, 211, 212));
        for (int i = 0; i < 48; i++) {
            float x = ((i * 113 + now / 11) % 1000) / 1000f * w;
            float y = ((i * 191 + now / 6) % 1000) / 1000f * h;
            canvas.drawLine(x, y, x - 4, y + 22, paint);
        }
    }

    private void drawPhoneGlass(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, w, h,
                Color.argb(32, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        paint.setShader(new RadialGradient(w * 0.5f, h * 0.55f, w * 0.8f,
                Color.TRANSPARENT, Color.argb(195, 0, 0, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private void drawScan(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(28, 232, 236, 234));
        float scan = (now % 2600L) / 2600f * h;
        canvas.drawLine(0, scan, w, scan, paint);

        paint.setColor(Color.argb(12, 232, 236, 234));
        for (int i = 0; i < 9; i++) {
            float y = h * i / 9f;
            canvas.drawLine(0, y, w, y, paint);
        }
    }
}
