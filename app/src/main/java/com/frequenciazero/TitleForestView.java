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

    public TitleForestView(Context context) { this(context, null); }
    public TitleForestView(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        paint.setShader(new LinearGradient(0, 0, 0, h, Color.rgb(5, 8, 9), Color.rgb(1, 1, 2), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        paint.setShader(new RadialGradient(w * .5f, h * .42f, w * .55f, Color.argb(70, 72, 116, 116), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);

        paint.setColor(Color.argb(70, 180, 205, 200));
        for (int i = 0; i < 22; i++) {
            float y = h * (.20f + i * .025f) + (now % 2600L) / 2600f * 12f;
            canvas.drawLine(0, y, w, y - 18f, paint);
        }

        paint.setColor(Color.rgb(8, 15, 15));
        for (int i = 0; i < 18; i++) drawTree(canvas, i * w / 17f, h * (.70f + (i % 4) * .025f), 42 + (i % 5) * 12);
        drawAntenna(canvas, w * .52f, h * .72f);
        postInvalidateDelayed(50);
    }

    private void drawTree(Canvas canvas, float x, float base, float size) {
        Path p = new Path();
        p.moveTo(x, base - size);
        p.lineTo(x - size * .34f, base);
        p.lineTo(x + size * .34f, base);
        p.close();
        canvas.drawPath(p, paint);
    }

    private void drawAntenna(Canvas canvas, float x, float base) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.rgb(20, 30, 30));
        canvas.drawLine(x, base, x, base - 150, paint);
        canvas.drawLine(x - 38, base - 84, x + 38, base - 84, paint);
        canvas.drawLine(x - 28, base - 125, x + 28, base - 125, paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
