package com.frequenciazero;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AppActivity extends AppCompatActivity {
    private GameState state;
    private TextView feedback;
    private TextView status;
    private TextView energy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_restore);
        state = new GameState();
        feedback = findViewById(R.id.feedback);
        status = findViewById(R.id.status);
        energy = findViewById(R.id.energy);
        bind(R.id.btnClean, 1, 1, -1, 4);
        bind(R.id.btnAmp, 0, 1, 0, 6);
        bind(R.id.btnCut, 1, 0, 0, 5);
        bind(R.id.btnStable, 2, 3, 0, 8);
        bind(R.id.btnSend, 2, 2, 0, 2);
        bind(R.id.btnKeep, 0, 0, 1, 3);
        updateEnergy();
    }

    private void bind(int id, int a, int b, int c, int cost) {
        MaterialButton button = findViewById(id);
        button.setOnClickListener(v -> apply(a, b, c, cost));
    }

    private void apply(int a, int b, int c, int cost) {
        state.addA(a);
        state.addB(b);
        state.addC(c);
        state.useEnergy(cost);
        feedback.setText("Operacao aplicada.");
        updateEnergy();
        updateRhythm();
    }

    private void updateEnergy() {
        energy.setText("Energia: " + state.getEnergy() + "%");
    }

    private void updateRhythm() {
        int total = state.getA() + state.getB();
        if (total >= 9) {
            status.setText("Integridade: 51%");
        } else if (total >= 6) {
            status.setText("Integridade: 47%");
        } else if (total >= 3) {
            status.setText("Integridade: 43%");
        }
    }
}
