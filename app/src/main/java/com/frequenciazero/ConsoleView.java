package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ConsoleView extends View {
    public interface Listener {
        void onClean();
        void onAmplify();
        void onCut();
        void onStable();
        void onSend();
        void onKeep();
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final RectF panel = new RectF();
    private final RectF center = new RectF();
    private Listener listener;
    private float downX;
    private float downY;
    private long downTime;
    private float cursor = 0.35f;
    private String hint = "arraste a faixa ou toque nos picos";

    public ConsoleView(Context context) {
        super(context);
        setFocusable(true);
    }

    public ConsoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setHint(String hint) {
        this.hint = hint;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        panel.set(6, 6, w - 6, h - 6);
        center.set(w * 0.36f, h * 0.36f, w * 0.64f, h * 0.64f);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(7, 17, 18));
        canvas.drawRoundRect(panel, 28, 28, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.rgb(41, 66, 70));
        canvas.drawRoundRect(panel, 28, 28, paint);

        drawGrid(canvas, w, h);
        drawWave(canvas, w, h);
        drawPeaks(canvas, w, h);
        drawCursor(canvas, w, h);
        drawCenterCommand(canvas);
        drawText(canvas, w, h);
    }

    private void drawGrid(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.rgb(22, 38, 40));
        for (int i = 1; i < 8; i++) {
            float x = w * i / 8f;
            canvas.drawLine(x, 18, x, h - 18, paint);
        }
        for (int i = 1; i < 6; i++) {
            float y = h * i / 6f;
            canvas.drawLine(18, y, w - 18, y, paint);
        }
    }

    private void drawWave(Canvas canvas, float w, float h) {
        path.reset();
        float mid = h * 0.45f;
        path.moveTo(28, mid);
        for (int i = 0; i <= 80; i++) {
            float x = 28 + ((w - 56) * i / 80f);
            float y = mid + (float) Math.sin(i * 0.45f) * 28f + (float) Math.sin(i * 0.13f) * 18f;
            path.lineTo(x, y);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.rgb(216, 162, 58));
        canvas.drawPath(path, paint);

        path.reset();
        float mid2 = h * 0.62f;
        path.moveTo(28, mid2);
        for (int i = 0; i <= 80; i++) {
            float x = 28 + ((w - 56) * i / 80f);
            float y = mid2 + (float) Math.cos(i * 0.36f) * 16f;
            path.lineTo(x, y);
        }
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(31, 111, 91));
        canvas.drawPath(path, paint);
    }

    private void drawPeaks(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(241, 237, 226));
        canvas.drawCircle(w * 0.23f, h * 0.32f, 7, paint);
        canvas.drawCircle(w * 0.55f, h * 0.25f, 9, paint);
        canvas.drawCircle(w * 0.78f, h * 0.38f, 6, paint);
    }

    private void drawCursor(Canvas canvas, float w, float h) {
        float x = 28 + (w - 56) * cursor;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.rgb(196, 176, 244));
        canvas.drawLine(x, 28, x, h - 28, paint);
    }

    private void drawCenterCommand(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(16, 32, 34));
        canvas.drawRoundRect(center, 18, 18, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.rgb(216, 162, 58));
        canvas.drawRoundRect(center, 18, 18, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(28);
        paint.setColor(Color.rgb(241, 237, 226));
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("SEGURE", center.centerX(), center.centerY() - 6, paint);
        paint.setTextSize(18);
        paint.setColor(Color.rgb(156, 170, 167));
        canvas.drawText("estabilizar", center.centerX(), center.centerY() + 24, paint);
    }

    private void drawText(Canvas canvas, float w, float h) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(22);
        paint.setColor(Color.rgb(156, 170, 167));
        canvas.drawText(hint, 28, h - 34, paint);
        paint.setTextSize(18);
        canvas.drawText("swipe cima: enviar  |  swipe baixo: arquivar", 28, h - 12, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener == null) {
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
            downTime = System.currentTimeMillis();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float w = getWidth();
            cursor = Math.max(0f, Math.min(1f, event.getX() / Math.max(1f, w)));
            invalidate();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = event.getX() - downX;
            float dy = event.getY() - downY;
            long held = System.currentTimeMillis() - downTime;

            if (held > 650 && center.contains(downX, downY)) {
                listener.onStable();
            } else if (Math.abs(dy) > 140 && Math.abs(dy) > Math.abs(dx)) {
                if (dy < 0) {
                    listener.onSend();
                } else {
                    listener.onKeep();
                }
            } else if (Math.abs(dx) > 80) {
                listener.onClean();
            } else if (nearPeak(event.getX(), event.getY())) {
                listener.onAmplify();
            } else {
                listener.onCut();
            }
            invalidate();
            return true;
        }
        return true;
    }

    private boolean nearPeak(float x, float y) {
        float w = getWidth();
        float h = getHeight();
        return distance(x, y, w * 0.23f, h * 0.32f) < 70
                || distance(x, y, w * 0.55f, h * 0.25f) < 80
                || distance(x, y, w * 0.78f, h * 0.38f) < 70;
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
