package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class HelenaAvatarView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public HelenaAvatarView(Context context) { this(context, null); }
    public HelenaAvatarView(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        float r = Math.min(w, h) / 2f;
        paint.setShader(new RadialGradient(w / 2f, h * .35f, r, Color.rgb(44, 54, 52), Color.rgb(4, 4, 5), Shader.TileMode.CLAMP));
        canvas.drawCircle(w / 2f, h / 2f, r - 2, paint);
        paint.setShader(null);
        paint.setColor(Color.rgb(18, 14, 13));
        canvas.drawOval(new RectF(w * .25f, h * .12f, w * .75f, h * .72f), paint);
        paint.setColor(Color.rgb(92, 78, 67));
        canvas.drawOval(new RectF(w * .34f, h * .25f, w * .66f, h * .60f), paint);
        paint.setColor(Color.rgb(10, 8, 8));
        canvas.drawRect(w * .30f, h * .52f, w * .70f, h * .88f, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.rgb(79, 104, 95));
        canvas.drawCircle(w / 2f, h / 2f, r - 3, paint);
        paint.setStyle(Paint.Style.FILL);
    }
}
