package com.frequenciazero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AppActivity extends AppCompatActivity {
    private static final String PREFS = "fz_save";
    private static final String KEY_A = "a";
    private static final String KEY_B = "b";
    private static final String KEY_C = "c";
    private static final String KEY_ENERGY = "energy";
    private static final String KEY_LOG = "log";
    private static final String SPLIT = " :: ";

    private GameState state;
    private TextView feedback;
    private TextView status;
    private TextView energy;
    private TextView logView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_restore);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        state = new GameState();
        loadState();

        feedback = findViewById(R.id.feedback);
        status = findViewById(R.id.status);
        energy = findViewById(R.id.energy);
        logView = findViewById(R.id.logView);

        bind(R.id.btnClean, 1, 1, -1, 4, "Limpeza aplicada", "Camada removida");
        bind(R.id.btnAmp, 0, 1, 0, 6, "Pico amplificado", "Camada priorizada");
        bind(R.id.btnCut, 1, 0, 0, 5, "Trecho isolado", "Camada retirada");
        bind(R.id.btnStable, 2, 3, 0, 8, "Arquivo estabilizado", "Modulo restaurado");
        bind(R.id.btnSend, 2, 2, 0, 2, "Pacote enviado", "Canal alimentado");
        bind(R.id.btnKeep, 0, 0, 1, 3, "Arquivo local", "Copia preservada");

        updateEnergy();
        updateRhythm();
        updateLog();
    }

    private void bind(int id, int a, int b, int c, int cost, String label, String secondLabel) {
        MaterialButton button = findViewById(id);
        button.setOnClickListener(v -> apply(a, b, c, cost, label, secondLabel));
    }

    private void apply(int a, int b, int c, int cost, String label, String secondLabel) {
        state.addA(a);
        state.addB(b);
        state.addC(c);
        state.useEnergy(cost);
        feedback.setText(label);
        appendLog(label, secondLabel);
        saveState();
        updateEnergy();
        updateRhythm();
        updateLog();
    }

    private void loadState() {
        state.setA(prefs.getInt(KEY_A, 0));
        state.setB(prefs.getInt(KEY_B, 0));
        state.setC(prefs.getInt(KEY_C, 0));
        state.setEnergy(prefs.getInt(KEY_ENERGY, 100));
    }

    private void saveState() {
        prefs.edit()
                .putInt(KEY_A, state.getA())
                .putInt(KEY_B, state.getB())
                .putInt(KEY_C, state.getC())
                .putInt(KEY_ENERGY, state.getEnergy())
                .apply();
    }

    private void appendLog(String label, String secondLabel) {
        String current = prefs.getString(KEY_LOG, "");
        prefs.edit().putString(KEY_LOG, current + label + SPLIT + secondLabel + "\n").apply();
    }

    private void updateEnergy() {
        energy.setText("Energia: " + state.getEnergy() + "%");
    }

    private void updateRhythm() {
        int total = state.getA() + state.getB();
        if (total >= 9) {
            status.setText("Integridade: 51% | Estabilizacao acima do previsto");
        } else if (total >= 6) {
            status.setText("Integridade: 47% | Fonte secundaria respondendo");
        } else if (total >= 3) {
            status.setText("Integridade: 43% | Padrao respiratorio detectado");
        }
    }

    private void updateLog() {
        if (logView == null) {
            return;
        }

        String savedLog = prefs.getString(KEY_LOG, "");
        if (savedLog == null || savedLog.trim().isEmpty()) {
            logView.setText("Nenhuma operacao registrada.");
            return;
        }

        boolean secondMode = state.getA() + state.getB() >= 9;
        StringBuilder builder = new StringBuilder();
        String[] lines = savedLog.split("\n");
        for (String line : lines) {
            String[] parts = line.split(SPLIT);
            if (parts.length == 2) {
                builder.append(secondMode ? parts[1] : parts[0]).append("\n");
            }
        }

        logView.setText(builder.toString());
    }
}
