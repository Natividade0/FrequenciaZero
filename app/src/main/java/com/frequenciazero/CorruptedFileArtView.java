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

public class CorruptedFileArtView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public CorruptedFileArtView(Context context) {
        this(context, null);
    }

    public CorruptedFileArtView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        float pulse = (float) (0.5f + Math.sin(now * 0.003f) * 0.5f);

        drawBase(canvas, w, h, pulse);
        drawTowerAndForest(canvas, w, h, pulse);
        drawStaticLines(canvas, w, h, now);
        drawGlitchBlocks(canvas, w, h, now);
        drawFrame(canvas, w, h);

        postInvalidateDelayed(45);
    }

    private void drawBase(Canvas canvas, float w, float h, float pulse) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, w, h,
                new int[]{Color.rgb(4, 8, 9), Color.rgb(7, 27, 34), Color.rgb(2, 3, 4)},
                new float[]{0f, 0.55f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0, 0, w, h, 20, 20, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.58f, h * 0.34f, w * (0.22f + pulse * 0.05f),
                Color.argb(135, 181, 64, 58), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0, 0, w, h, 20, 20, paint);
        paint.setShader(null);
    }

    private void drawTowerAndForest(Canvas canvas, float w, float h, float pulse) {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 22; i++) {
            float x = i * w / 21f;
            float base = h * (0.78f + (i % 4) * 0.03f);
            float size = 38 + (i % 5) * 11;
            paint.setColor(Color.argb(205, 2, 8, 10));
            Path tree = new Path();
            tree.moveTo(x, base - size);
            tree.lineTo(x - size * 0.35f, base);
            tree.lineTo(x + size * 0.35f, base);
            tree.close();
            canvas.drawPath(tree, paint);
        }

        float x = w * 0.55f;
        float top = h * 0.22f;
        float bottom = h * 0.76f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.rgb(55, 86, 88));
        canvas.drawLine(x, bottom, x, top, paint);
        canvas.drawLine(x, top, x - 28, bottom, paint);
        canvas.drawLine(x, top, x + 28, bottom, paint);
        canvas.drawLine(x - 26, h * 0.48f, x + 26, h * 0.48f, paint);
        canvas.drawLine(x - 18, h * 0.35f, x + 18, h * 0.35f, paint);

        paint.setColor(Color.argb(135 + (int) (pulse * 85), 181, 64, 58));
        canvas.drawCircle(x, top, 8 + pulse * 4, paint);
        canvas.drawCircle(x, top, 24 + pulse * 9, paint);
    }

    private void drawStaticLines(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < 14; i++) {
            float y = h * (0.07f + i * 0.065f);
            int alpha = 20 + (i % 4) * 10;
            paint.setStrokeWidth(i % 5 == 0 ? 2f : 1f);
            paint.setColor(Color.argb(alpha, 232, 236, 234));
            canvas.drawLine(0, y, w, y + (float) Math.sin(i + now * 0.002f) * 8f, paint);
        }
    }

    private void drawGlitchBlocks(Canvas canvas, float w, float h, long now) {
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 18; i++) {
            float seed = ((i * 137 + now / 45) % 1000) / 1000f;
            float x = ((i * 211 + now / 31) % 1000) / 1000f * w;
            float y = ((i * 83 + now / 53) % 1000) / 1000f * h;
            float bw = w * (0.035f + (i % 4) * 0.018f);
            float bh = 3f + (i % 3) * 3f;
            if (seed > 0.52f) paint.setColor(Color.argb(44, 59, 175, 159));
            else paint.setColor(Color.argb(46, 181, 64, 58));
            canvas.drawRect(x, y, Math.min(w, x + bw), Math.min(h, y + bh), paint);
        }
    }

    private void drawFrame(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.argb(120, 36, 54, 58));
        canvas.drawRoundRect(1, 1, w - 1, h - 1, 20, 20, paint);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(52, 232, 236, 234));
        canvas.drawRoundRect(8, 8, w - 8, h - 8, 16, 16, paint);
    }
}
