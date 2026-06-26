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

public class TitleForestView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TitleForestView(Context context) {
        this(context, null);
    }

    public TitleForestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        float pulse = (float) (0.5f + Math.sin(now * 0.0024f) * 0.5f);

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, h,
                Color.rgb(5, 6, 7), Color.rgb(7, 27, 34), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.15f, h * 0.26f, w * 0.7f,
                Color.argb(58, 59, 175, 159), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.78f, h * 0.62f, w * (0.16f + pulse * 0.04f),
                Color.argb(150, 181, 64, 58), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        drawRoad(canvas, w, h);
        drawForest(canvas, w, h);
        drawAntenna(canvas, w, h, pulse);
        drawNoise(canvas, w, h, now);
        postInvalidateDelayed(50);
    }

    private void drawRoad(Canvas canvas, float w, float h) {
        Path road = new Path();
        road.moveTo(w * 0.46f, h);
        road.lineTo(w * 0.54f, h);
        road.lineTo(w * 0.50f, h * 0.50f);
        road.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(120, 8, 11, 12));
        canvas.drawPath(road, paint);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(70, 139, 154, 150));
        canvas.drawLine(w * 0.5f, h, w * 0.5f, h * 0.58f, paint);
    }

    private void drawForest(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        for (int layer = 0; layer < 3; layer++) {
            paint.setColor(Color.argb(190 - layer * 35, 5, 9 + layer * 4, 10 + layer * 5));
            for (int i = 0; i < 18; i++) {
                float x = ((i * 61 + layer * 29) % 1000) / 1000f * w;
                float base = h * (0.72f + layer * 0.07f);
                float size = 42 + (i % 4) * 18 + layer * 10;
                Path tree = new Path();
                tree.moveTo(x, base - size);
                tree.lineTo(x - size * 0.32f, base);
                tree.lineTo(x + size * 0.32f, base);
                tree.close();
                canvas.drawPath(tree, paint);
            }
        }
        paint.setColor(Color.argb(54, 232, 236, 234));
        canvas.drawRect(0, h * 0.58f, w, h * 0.64f, paint);
        canvas.drawRect(0, h * 0.67f, w, h * 0.72f, paint);
    }

    private void drawAntenna(Canvas canvas, float w, float h, float pulse) {
        float x = w * 0.56f;
        float bottom = h * 0.70f;
        float top = h * 0.42f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.rgb(36, 54, 58));
        canvas.drawLine(x, bottom, x, top, paint);
        canvas.drawLine(x - 18, h * 0.52f, x + 18, h * 0.52f, paint);
        canvas.drawLine(x - 12, h * 0.46f, x + 12, h * 0.46f, paint);
        paint.setStrokeWidth(1.5f);
        paint.setColor(Color.argb(80 + (int) (pulse * 80), 181, 64, 58));
        canvas.drawCircle(x, top, 12 + pulse * 5, paint);
        canvas.drawCircle(x, top, 30 + pulse * 10, paint);
    }

    private void drawNoise(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(34, 232, 236, 234));
        for (int i = 0; i < 44; i++) {
            float x = ((i * 97) % 1000) / 1000f * w;
            float y = (((i * 173) + (now / 18)) % 1000) / 1000f * h;
            canvas.drawCircle(x, y, 0.8f + (i % 3) * 0.45f, paint);
        }
    }
}
