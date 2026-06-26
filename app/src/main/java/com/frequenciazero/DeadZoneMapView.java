package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class DeadZoneMapView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF frame = new RectF();

    public DeadZoneMapView(Context context) {
        this(context, null);
    }

    public DeadZoneMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        frame.set(2, 2, w - 2, h - 2);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(13, 10, 8));
        canvas.drawRoundRect(frame, 12, 12, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.rgb(70, 54, 43));
        canvas.drawRoundRect(frame, 12, 12, paint);

        paint.setStrokeWidth(1f);
        paint.setColor(Color.rgb(38, 31, 27));
        for (int i = 1; i < 8; i++) canvas.drawLine(i * w / 8f, 0, i * w / 8f, h, paint);
        for (int i = 1; i < 6; i++) canvas.drawLine(0, i * h / 6f, w, i * h / 6f, paint);

        drawNode(canvas, w * 0.38f, h * 0.40f, "Cidade Vértice", Color.rgb(247, 238, 227));
        drawNode(canvas, w * 0.55f, h * 0.58f, "Zona Morta", Color.rgb(182, 61, 50));
        drawNode(canvas, w * 0.68f, h * 0.28f, "Antena", Color.rgb(127, 175, 146));
        drawNode(canvas, w * 0.30f, h * 0.72f, "Rádio Âncora", Color.rgb(224, 111, 47));

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        paint.setColor(Color.argb(90, 224, 111, 47));
        canvas.drawCircle(w * 0.55f, h * 0.58f, w * 0.23f, paint);
        canvas.drawCircle(w * 0.55f, h * 0.58f, w * 0.34f, paint);
    }

    private void drawNode(Canvas canvas, float x, float y, String label, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(x, y, 7f, paint);
        paint.setTextSize(13f);
        paint.setColor(Color.rgb(247, 238, 227));
        canvas.drawText(label, x + 12f, y + 5f, paint);
    }
}
