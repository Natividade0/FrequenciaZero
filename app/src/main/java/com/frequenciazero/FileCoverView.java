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

public class FileCoverView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FileCoverView(Context context) {
        this(context, null);
    }

    public FileCoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        float pulse = (float) (0.5f + Math.sin(now * 0.0028f) * 0.5f);

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, w, h,
                Color.rgb(7, 13, 14), Color.rgb(4, 4, 5), Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0, 0, w, h, 16, 16, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.68f, h * 0.34f, w * 0.42f,
                Color.argb(70, 182, 61, 50), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(32, 247, 238, 227));
        for (int i = 0; i < 8; i++) {
            float y = h * (0.15f + i * 0.10f);
            canvas.drawLine(0, y, w, y + (float) Math.sin(i + now * 0.001f) * 5f, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(8, 18, 17));
        for (int i = 0; i < 18; i++) {
            drawTree(canvas, i * w / 17f, h * (0.76f + (i % 4) * 0.035f), 25 + (i % 5) * 7);
        }

        float antennaX = w * 0.54f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.rgb(57, 78, 72));
        canvas.drawLine(antennaX, h * 0.75f, antennaX, h * 0.25f, paint);
        canvas.drawLine(antennaX - 24, h * 0.44f, antennaX + 24, h * 0.44f, paint);
        canvas.drawLine(antennaX - 16, h * 0.32f, antennaX + 16, h * 0.32f, paint);

        paint.setStrokeWidth(1.4f);
        paint.setColor(Color.argb(70 + (int) (pulse * 80), 224, 111, 47));
        canvas.drawCircle(antennaX, h * 0.24f, 10 + pulse * 8, paint);
        canvas.drawCircle(antennaX, h * 0.24f, 28 + pulse * 10, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(34, 127, 175, 146));
        canvas.drawRect(0, h * 0.62f, w, h * 0.68f, paint);
        postInvalidateDelayed(80);
    }

    private void drawTree(Canvas canvas, float x, float base, float size) {
        Path path = new Path();
        path.moveTo(x, base - size);
        path.lineTo(x - size * 0.34f, base);
        path.lineTo(x + size * 0.34f, base);
        path.close();
        canvas.drawPath(path, paint);
    }
}
