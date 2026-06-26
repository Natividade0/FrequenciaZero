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
        paint.setShader(new LinearGradient(0, 0, 0, h,
                Color.rgb(5, 6, 7), Color.rgb(5, 16, 20), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        paint.setShader(new RadialGradient(w * 0.18f, h * 0.18f, w * 0.75f,
                Color.argb(62, 59, 175, 159), Color.TRANSPARENT, Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(28, 232, 236, 234));
        float scan = (now % 2600L) / 2600f * h;
        canvas.drawLine(0, scan, w, scan, paint);
        postInvalidateDelayed(80);
    }
}
