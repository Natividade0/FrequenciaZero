package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

public class VanSceneView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int step = 0;
    private float dial = 0.18f;
    private float startX;
    private String line = "Toque para ligar o painel.";

    public VanSceneView(Context c) { super(c); }
    public VanSceneView(Context c, AttributeSet a) { super(c, a); }

    @Override
    protected void onDraw(Canvas c) {
        float w = getWidth();
        float h = getHeight();

        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(3, 8, 10));
        c.drawRect(0, 0, w, h, p);

        drawRoad(c, w, h);
        drawCity(c, w, h);
        drawDashboard(c, w, h);
        drawRadio(c, w, h);
        drawNoise(c, w, h);
        drawText(c, w, h);
    }

    private void drawRoad(Canvas c, float w, float h) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(8, 14, 15));
        c.drawPath(makeRoad(w, h), p);
        p.setColor(Color.rgb(216, 162, 58));
        c.drawRect(w * .49f, h * .36f, w * .51f, h * .58f, p);
    }

    private android.graphics.Path makeRoad(float w, float h) {
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(w * .42f, h * .34f);
        path.lineTo(w * .58f, h * .34f);
        path.lineTo(w * .88f, h * .72f);
        path.lineTo(w * .12f, h * .72f);
        path.close();
        return path;
    }

    private void drawCity(Canvas c, float w, float h) {
        p.setColor(Color.rgb(13, 26, 28));
        for (int i = 0; i < 9; i++) {
            float x = 30 + i * w / 9f;
            float top = h * (.18f + (i % 3) * .035f);
            c.drawRect(x, top, x + w * .06f, h * .34f, p);
        }
        p.setColor(Color.rgb(31, 111, 91));
        p.setStrokeWidth(2);
        c.drawLine(0, h * .34f, w, h * .34f, p);
    }

    private void drawDashboard(Canvas c, float w, float h) {
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(6, 15, 16));
        c.drawRoundRect(new RectF(0, h * .62f, w, h + 40), 42, 42, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        p.setColor(Color.rgb(41, 66, 70));
        c.drawRoundRect(new RectF(12, h * .64f, w - 12, h - 12), 28, 28, p);
    }

    private void drawRadio(Canvas c, float w, float h) {
        float left = w * .12f;
        float top = h * .68f;
        float right = w * .88f;
        float bottom = h * .88f;
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(13, 26, 28));
        c.drawRoundRect(new RectF(left, top, right, bottom), 24, 24, p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setColor(step < 2 ? Color.rgb(41, 66, 70) : Color.rgb(216, 162, 58));
        c.drawRoundRect(new RectF(left, top, right, bottom), 24, 24, p);

        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(28);
        p.setColor(Color.rgb(241, 237, 226));
        c.drawText(step < 2 ? "RADIO OFF" : "AM 03.17", w * .5f, top + 44, p);

        p.setStrokeWidth(5);
        p.setColor(Color.rgb(196, 176, 244));
        float dialX = left + (right - left) * dial;
        c.drawLine(left + 28, bottom - 48, right - 28, bottom - 48, p);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(216, 162, 58));
        c.drawCircle(dialX, bottom - 48, 14, p);
    }

    private void drawNoise(Canvas c, float w, float h) {
        if (step < 2) return;
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3);
        p.setColor(Color.rgb(31, 111, 91));
        float y = h * .56f;
        for (int i = 0; i < 36; i++) {
            float x1 = 24 + i * (w - 48) / 36f;
            float x2 = 24 + (i + 1) * (w - 48) / 36f;
            float y1 = y + (float)Math.sin(i * .8f + dial * 8f) * (10 + step * 4);
            float y2 = y + (float)Math.sin((i + 1) * .8f + dial * 8f) * (10 + step * 4);
            c.drawLine(x1, y1, x2, y2, p);
        }
    }

    private void drawText(Canvas c, float w, float h) {
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.LEFT);
        p.setTextSize(30);
        p.setColor(Color.rgb(241, 237, 226));
        c.drawText("VERTICE // ENTRADA NORTE", 26, 46, p);
        p.setTextSize(22);
        p.setColor(Color.rgb(156, 170, 167));
        c.drawText(line, 26, h - 28, p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            startX = e.getX();
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE && step >= 2) {
            dial = Math.max(0f, Math.min(1f, e.getX() / Math.max(1f, getWidth())));
            updateLine();
            invalidate();
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            playSoundEffect(SoundEffectConstants.CLICK);
            if (step < 2) step++;
            else if (Math.abs(e.getX() - startX) < 20) step++;
            updateLine();
            invalidate();
            return true;
        }
        return true;
    }

    private void updateLine() {
        if (step == 0) line = "Toque para ligar o painel.";
        else if (step == 1) line = "O radio ligou sozinho.";
        else if (step == 2) line = "Arraste o dial ate o ruido estabilizar.";
        else if (dial > .46f && dial < .58f) line = "Sinal encontrado. Toque para capturar.";
        else if (step >= 3) line = "Amostra capturada. O arquivo apareceu no console.";
        else line = "Frequencia instavel.";
        set(R.id.feedback, line);
        set(R.id.status, dial > .46f && dial < .58f ? "Sinal: forte" : "Sinal: fraco");
        set(R.id.energy, "Energia: " + Math.max(63, 100 - step * 9) + "%");
        TextView log = getRootView().findViewById(R.id.logView);
        if (log != null && step == 3) log.setText("03:17 sinal capturado\narquivo local criado");
    }

    private void set(int id, String text) {
        TextView t = getRootView().findViewById(id);
        if (t != null) t.setText(text);
    }
}
