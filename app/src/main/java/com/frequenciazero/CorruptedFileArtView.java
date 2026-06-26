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

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0, 0, w, h,
                Color.rgb(4, 8, 9), Color.rgb(7, 27, 34), Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0, 0, w, h, 18, 18, paint);
        paint.setShader(null);

        paint.setShader(new RadialGradient(w * 0.76f, h * 0.42f, w * (0.18f + pulse * 0.05f),
                Color.argb(150, 181, 64, 58), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setColor(Color.rgb(4, 9, 10));
        for (int i = 0; i < 16; i++) {
            float x = i * w / 15f;
            float base = h * (0.78f + (i % 3) * 0.04f);
            float size = 34 + (i % 5) * 9;
            Path tree = new Path();
            tree.moveTo(x, base - size);
            tree.lineTo(x - size * 0.35f, base);
            tree.lineTo(x + size * 0.35f, base);
            tree.close();
            canvas.drawPath(tree, paint);
        }

        float x = w * 0.52f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.rgb(42, 72, 75));
        canvas.drawLine(x, h * 0.76f, x, h * 0.28f, paint);
        canvas.drawLine(x - 24, h * 0.47f, x + 24, h * 0.47f, paint);
        canvas.drawLine(x - 16, h * 0.36f, x + 16, h * 0.36f, paint);

        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(55, 232, 236, 234));
        for (int i = 0; i < 9; i++) {
            float y = h * (0.15f + i * 0.08f);
            canvas.drawLine(0, y, w, y + (float) Math.sin(i + now * 0.002f) * 7f, paint);
        }
        postInvalidateDelayed(70);
    }
}
