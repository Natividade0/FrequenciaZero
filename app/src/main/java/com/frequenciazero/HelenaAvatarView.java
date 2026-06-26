package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class HelenaAvatarView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HelenaAvatarView(Context context) {
        this(context, null);
    }

    public HelenaAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float r = Math.min(w, h) * 0.5f;
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(w * 0.5f, h * 0.35f, r,
                Color.rgb(62, 76, 74), Color.rgb(5, 6, 7), Shader.TileMode.CLAMP));
        canvas.drawCircle(w * 0.5f, h * 0.5f, r - 2f, paint);
        paint.setShader(null);

        paint.setColor(Color.rgb(18, 23, 24));
        canvas.drawOval(w * 0.28f, h * 0.20f, w * 0.72f, h * 0.68f, paint);
        paint.setColor(Color.rgb(118, 96, 84));
        canvas.drawOval(w * 0.36f, h * 0.26f, w * 0.64f, h * 0.58f, paint);
        paint.setColor(Color.rgb(9, 12, 13));
        canvas.drawRect(w * 0.22f, h * 0.58f, w * 0.78f, h * 0.88f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.argb(130, 59, 175, 159));
        canvas.drawCircle(w * 0.5f, h * 0.5f, r - 3f, paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
