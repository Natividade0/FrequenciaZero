package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ConsoleGameView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float downX;
    private float downY;
    private long downAt;
    private int energy = 100;
    private int count = 0;
    private String last = "operar painel";

    public ConsoleGameView(Context c) {
        super(c);
    }

    public ConsoleGameView(Context c, AttributeSet a) {
        super(c, a);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w = getWidth();
        float h = getHeight();

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(7, 17, 18));
        c.drawRoundRect(8, 8, w - 8, h - 8, 28, 28, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        p.setColor(Color.rgb(41, 66, 70));
        c.drawRoundRect(8, 8, w - 8, h - 8, 28, 28, p);

        p.setStrokeWidth(1);
        p.setColor(Color.rgb(22, 38, 40));
        for (int i = 1; i < 8; i++) {
            float x = w * i / 8f;
            c.drawLine(x, 24, x, h - 24, p);
        }
        for (int i = 1; i < 6; i++) {
            float y = h * i / 6f;
            c.drawLine(24, y, w - 24, y, p);
        }

        p.setStrokeWidth(5);
        p.setColor(Color.rgb(216, 162, 58));
        float mid = h * 0.42f;
        for (int i = 0; i < 42; i++) {
            float x1 = 28 + i * (w - 56) / 42f;
            float x2 = 28 + (i + 1) * (w - 56) / 42f;
            float y1 = mid + (float) Math.sin(i * 0.7f) * 36f;
            float y2 = mid + (float) Math.sin((i + 1) * 0.7f) * 36f;
            c.drawLine(x1, y1, x2, y2, p);
        }

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(241, 237, 226));
        c.drawCircle(w * 0.25f, h * 0.28f, 8, p);
        c.drawCircle(w * 0.55f, h * 0.32f, 10, p);
        c.drawCircle(w * 0.77f, h * 0.25f, 7, p);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
        p.setColor(Color.rgb(196, 176, 244));
        c.drawRoundRect(w * 0.36f, h * 0.62f, w * 0.64f, h * 0.78f, 18, 18, p);

        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(28);
        p.setColor(Color.rgb(241, 237, 226));
        c.drawText("SEGURE", w * 0.5f, h * 0.70f, p);

        p.setTextAlign(Paint.Align.LEFT);
        p.setTextSize(22);
        p.setColor(Color.rgb(156, 170, 167));
        c.drawText(last, 28, h - 42, p);
        p.setTextSize(18);
        c.drawText("arraste / toque / segure / swipe", 28, h - 18, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            downX = e.getX();
            downY = e.getY();
            downAt = System.currentTimeMillis();
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float dx = e.getX() - downX;
            float dy = e.getY() - downY;
            long held = System.currentTimeMillis() - downAt;
            if (held > 650) action("estabilizado", 8);
            else if (Math.abs(dy) > 130) action(dy < 0 ? "enviado" : "arquivado", 3);
            else if (Math.abs(dx) > 80) action("faixa limpa", 4);
            else action("pico isolado", 6);
            return true;
        }
        return true;
    }

    private void action(String text, int cost) {
        last = text;
        count++;
        energy = Math.max(0, energy - cost);
        setText(R.id.feedback, text);
        setText(R.id.energy, "Energia: " + energy + "%");
        setText(R.id.status, "Integridade: " + (42 + count) + "%");
        TextView log = getRootView().findViewById(R.id.logView);
        if (log != null) log.setText(log.getText().toString() + text + "\n");
        invalidate();
    }

    private void setText(int id, String text) {
        TextView t = getRootView().findViewById(id);
        if (t != null) t.setText(text);
    }
}
