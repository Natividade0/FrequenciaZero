package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MiniWaveView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public MiniWaveView(Context context) { this(context, null); }
    public MiniWaveView(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        long now = System.currentTimeMillis();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.rgb(127, 175, 146));
        float mid = h / 2f;
        for (int i = 0; i < 58; i++) {
            float x = 8 + i * (w - 16) / 58f;
            float amp = (float) Math.abs(Math.sin(i * .55f + now * .004f)) * h * .38f;
            canvas.drawLine(x, mid - amp, x, mid + amp, paint);
        }
        postInvalidateDelayed(80);
    }
}
