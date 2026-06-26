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
        float slow = (float) (0.5f + Math.sin(now * 0.0008f) * 0.5f);

        drawSky(canvas, w, h, pulse);
        drawDistantHills(canvas, w, h);
        drawWetRoad(canvas, w, h, slow);
        drawForestLayers(canvas, w, h);
        drawAntenna(canvas, w, h, pulse);
        drawRain(canvas, w, h, now);
        drawFog(canvas, w, h, now);
        drawVignette(canvas, w, h);

        postInvalidateDelayed(33); // Mantém a arte viva sem exigir engine de jogo.
    }

    private void drawSky(Canvas canvas, float w, float h, float pulse) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, 0, h,
                new int[]{Color.rgb(3, 5, 7), Color.rgb(5, 17, 22), Color.rgb(2, 3, 4)},
                new float[]{0f, 0.52f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.45f, h * 0.22f, w * 0.78f,
                Color.argb(44, 83, 122, 130), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.67f, h * 0.48f, w * (0.18f + pulse * 0.05f),
                Color.argb(118, 181, 64, 58), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }

    private void drawDistantHills(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(135, 6, 20, 25));
        Path hill = new Path();
        hill.moveTo(0, h * 0.58f);
        for (int i = 0; i <= 8; i++) {
            float x = i * w / 8f;
            float y = h * (0.56f + (float) Math.sin(i * 1.37f) * 0.035f);
            hill.lineTo(x, y);
        }
        hill.lineTo(w, h);
        hill.lineTo(0, h);
        hill.close();
        canvas.drawPath(hill, paint);
    }

    private void drawWetRoad(Canvas canvas, float w, float h, float slow) {
        Path road = new Path();
        road.moveTo(w * 0.19f, h);
        road.cubicTo(w * 0.34f, h * 0.88f, w * 0.42f, h * 0.68f, w * 0.49f, h * 0.54f);
        road.cubicTo(w * 0.55f, h * 0.68f, w * 0.68f, h * 0.88f, w * 0.84f, h);
        road.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, h * 0.55f, 0, h,
                Color.argb(150, 9, 13, 15), Color.argb(230, 2, 3, 4), Shader.TileMode.CLAMP));
        canvas.drawPath(road, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.4f);
        paint.setColor(Color.argb(56, 232, 236, 234));
        canvas.drawLine(w * 0.49f, h * 0.58f, w * (0.47f + slow * 0.04f), h, paint);
        paint.setColor(Color.argb(34, 59, 175, 159));
        canvas.drawLine(w * 0.61f, h * 0.74f, w * 0.76f, h, paint);
    }

    private void drawForestLayers(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        for (int layer = 0; layer < 4; layer++) {
            int alpha = 205 - layer * 34;
            paint.setColor(Color.argb(alpha, 2 + layer, 8 + layer * 4, 10 + layer * 5));
            for (int i = 0; i < 24; i++) {
                float x = ((i * 71 + layer * 43) % 1000) / 1000f * w;
                float base = h * (0.64f + layer * 0.085f);
                float size = 46 + (i % 5) * 17 + layer * 12;
                Path tree = new Path();
                tree.moveTo(x, base - size);
                tree.lineTo(x - size * 0.34f, base);
                tree.lineTo(x + size * 0.34f, base);
                tree.close();
                canvas.drawPath(tree, paint);
            }
        }
    }

    private void drawAntenna(Canvas canvas, float w, float h, float pulse) {
        float x = w * 0.67f;
        float bottom = h * 0.62f;
        float top = h * 0.34f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.2f);
        paint.setColor(Color.rgb(38, 65, 70));
        canvas.drawLine(x, bottom, x, top, paint);
        canvas.drawLine(x - 18, h * 0.50f, x + 18, h * 0.50f, paint);
        canvas.drawLine(x - 13, h * 0.42f, x + 13, h * 0.42f, paint);
        canvas.drawLine(x, top, x - 23, bottom, paint);
        canvas.drawLine(x, top, x + 23, bottom, paint);

        paint.setStrokeWidth(1.5f);
        paint.setColor(Color.argb(95 + (int) (pulse * 100), 181, 64, 58));
        canvas.drawCircle(x, top, 9 + pulse * 4, paint);
        canvas.drawCircle(x, top, 28 + pulse * 10, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(180, 181, 64, 58));
        canvas.drawCircle(x, top, 3.8f + pulse * 1.8f, paint);
    }

    private void drawRain(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.1f);
        paint.setColor(Color.argb(58, 183, 211, 212));
        for (int i = 0; i < 58; i++) {
            float x = ((i * 97 + now / 9) % 1000) / 1000f * w;
            float y = ((i * 179 + now / 5) % 1000) / 1000f * h;
            canvas.drawLine(x, y, x - 5, y + 28, paint);
        }
    }

    private void drawFog(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 5; i++) {
            float y = h * (0.46f + i * 0.075f);
            float drift = (now % (5000 + i * 300)) / (5000f + i * 300f) * w;
            paint.setColor(Color.argb(18 + i * 5, 232, 236, 234));
            canvas.drawRect(-w + drift, y, drift, y + h * 0.035f, paint);
        }
    }

    private void drawVignette(Canvas canvas, float w, float h) {
        paint.setShader(new RadialGradient(w * 0.5f, h * 0.48f, w * 0.72f,
                Color.TRANSPARENT, Color.argb(225, 0, 0, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
    }
}
