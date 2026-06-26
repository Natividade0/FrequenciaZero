package com.frequenciazero;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

public class SignalView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float sx;
    private float sy;
    private long st;
    private int energy = 100;
    private int level = 42;
    private String msg = "sintonize o sinal";

    public SignalView(Context c) { super(c); }
    public SignalView(Context c, AttributeSet a) { super(c, a); }

    protected void onDraw(Canvas c) {
        float w = getWidth();
        float h = getHeight();
        p.setColor(Color.rgb(7,17,18));
        p.setStyle(Paint.Style.FILL);
        c.drawRoundRect(8,8,w-8,h-8,28,28,p);
        p.setColor(Color.rgb(41,66,70));
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        c.drawRoundRect(8,8,w-8,h-8,28,28,p);
        p.setStrokeWidth(4);
        p.setColor(Color.rgb(216,162,58));
        float mid = h * 0.42f;
        for (int i = 0; i < 36; i++) {
            float x1 = 28 + i * (w - 56) / 36f;
            float x2 = 28 + (i + 1) * (w - 56) / 36f;
            float y1 = mid + (float)Math.sin(i * 0.72f + level * 0.04f) * 34f;
            float y2 = mid + (float)Math.sin((i + 1) * 0.72f + level * 0.04f) * 34f;
            c.drawLine(x1,y1,x2,y2,p);
        }
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(241,237,226));
        c.drawCircle(w*.25f,h*.28f,9,p);
        c.drawCircle(w*.56f,h*.30f,11,p);
        c.drawCircle(w*.78f,h*.25f,8,p);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
        p.setColor(Color.rgb(196,176,244));
        c.drawRoundRect(w*.36f,h*.62f,w*.64f,h*.78f,18,18,p);
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
        p.setTextSize(28);
        p.setColor(Color.rgb(241,237,226));
        c.drawText("SEGURE",w*.5f,h*.70f,p);
        p.setTextAlign(Paint.Align.LEFT);
        p.setTextSize(22);
        p.setColor(Color.rgb(156,170,167));
        c.drawText(msg,28,h-42,p);
        p.setTextSize(18);
        c.drawText("toque nos picos | arraste | segure | swipe",28,h-18,p);
    }

    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            sx=e.getX(); sy=e.getY(); st=System.currentTimeMillis(); return true;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float dx=e.getX()-sx;
            float dy=e.getY()-sy;
            long hold=System.currentTimeMillis()-st;
            if (hold>650) act("estabilizado",8);
            else if (Math.abs(dy)>130) act(dy<0?"enviado":"arquivado",3);
            else if (Math.abs(dx)>80) act("faixa limpa",4);
            else act("pico isolado",6);
            return true;
        }
        return true;
    }

    private void act(String text,int cost) {
        playSoundEffect(SoundEffectConstants.CLICK);
        msg=text;
        energy=Math.max(0,energy-cost);
        level++;
        set(R.id.feedback,text);
        set(R.id.energy,"Energia: "+energy+"%");
        set(R.id.status,"Integridade: "+level+"%");
        TextView log=getRootView().findViewById(R.id.logView);
        if(log!=null) log.setText(log.getText().toString()+text+"\n");
        invalidate();
    }

    private void set(int id,String text) {
        TextView t=getRootView().findViewById(id);
        if(t!=null) t.setText(text);
    }
}
