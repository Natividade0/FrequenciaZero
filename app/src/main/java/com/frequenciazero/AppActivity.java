package com.frequenciazero;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frequenciazero.databinding.ActivityMainBinding;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppActivity extends AppCompatActivity {
    private static final String PREFS = "freq_zero_investigation";
    private static final String KEY_RESPONDED = "responded";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_CAPTURED = "captured";
    private static final String KEY_FREQUENCY = "frequency";
    private static final String KEY_SOUND = "sound";
    private static final String KEY_LOGS = "logs";
    private static final String KEY_TRUST = "trust";
    private static final String KEY_CURIOSITY = "curiosity";
    private static final String KEY_MEMORY = "memory";

    private ActivityMainBinding binding;
    private SharedPreferences prefs;
    private AudioEngine audio;
    private final ArrayList<Message> messages = new ArrayList<>();
    private final ArrayList<Transmission> transmissions = new ArrayList<>();
    private final ArrayList<String> logEntries = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private TransmissionAdapter transmissionAdapter;
    private LogAdapter logAdapter;
    private boolean responded;
    private boolean captured;
    private boolean nearLogged;
    private String response;
    private float frequency;
    private int protocolTrust;
    private int signalCuriosity;
    private int memoryResistance;
    private int lastLoggedFrequency = -1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        audio = new AudioEngine(this);
        loadState();
        setupLists();
        setupResponses();
        setupRestore();
        setupNavigation();
        setupSettings();
        addLog("BOOT", "session opened / secure channel warm");
        renderConversation();
        renderTransmissions();
        renderLog();
        showMessages();
    }

    @Override
    protected void onDestroy() {
        if (audio != null) audio.release();
        super.onDestroy();
    }

    private void loadState() {
        responded = prefs.getBoolean(KEY_RESPONDED, false);
        captured = prefs.getBoolean(KEY_CAPTURED, false);
        response = prefs.getString(KEY_RESPONSE, "");
        frequency = prefs.getFloat(KEY_FREQUENCY, 2.84f);
        protocolTrust = prefs.getInt(KEY_TRUST, 0);
        signalCuriosity = prefs.getInt(KEY_CURIOSITY, 0);
        memoryResistance = prefs.getInt(KEY_MEMORY, 0);
        audio.setEnabled(prefs.getBoolean(KEY_SOUND, true));
        String stored = prefs.getString(KEY_LOGS, "");
        logEntries.clear();
        if (!stored.isEmpty()) {
            String[] lines = stored.split("\\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) logEntries.add(line);
            }
        }
    }

    private void saveState() {
        prefs.edit()
                .putBoolean(KEY_RESPONDED, responded)
                .putBoolean(KEY_CAPTURED, captured)
                .putString(KEY_RESPONSE, response)
                .putFloat(KEY_FREQUENCY, frequency)
                .putInt(KEY_TRUST, protocolTrust)
                .putInt(KEY_CURIOSITY, signalCuriosity)
                .putInt(KEY_MEMORY, memoryResistance)
                .putBoolean(KEY_SOUND, binding.soundSwitch.isChecked())
                .apply();
    }

    private void setupLists() {
        messageAdapter = new MessageAdapter(messages);
        binding.messageList.setLayoutManager(new LinearLayoutManager(this));
        binding.messageList.setAdapter(messageAdapter);

        transmissionAdapter = new TransmissionAdapter(transmissions, new TransmissionAdapter.Listener() {
            @Override
            public void onOpen(Transmission transmission) {
                addLog("TRANS_OPEN", transmission.name + " / restore requested");
                audio.playTransmission();
                showRestore();
            }
        });
        binding.transmissionList.setLayoutManager(new LinearLayoutManager(this));
        binding.transmissionList.setAdapter(transmissionAdapter);

        logAdapter = new LogAdapter(logEntries);
        binding.logList.setLayoutManager(new LinearLayoutManager(this));
        binding.logList.setAdapter(logAdapter);
    }

    private void setupResponses() {
        binding.responseConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer("Confirmado.", 0);
            }
        });
        binding.responseFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer("Que frequência?", 1);
            }
        });
        binding.responseMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answer("Por que eu esqueceria?", 2);
            }
        });
    }

    private void setupRestore() {
        binding.frequencySlider.setValue(frequency);
        updateRestore(false);
        binding.frequencySlider.addOnChangeListener((slider, value, fromUser) -> {
            frequency = value;
            updateRestore(fromUser);
            saveState();
        });
        binding.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureSample();
            }
        });
    }

    private void setupNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_messages) {
                showMessages();
                return true;
            } else if (id == R.id.nav_transmissions) {
                showTransmissions();
                return true;
            } else if (id == R.id.nav_log) {
                showLog();
                return true;
            } else if (id == R.id.nav_map) {
                showMap();
                return true;
            } else if (id == R.id.nav_settings) {
                showSettings();
                return true;
            }
            return false;
        });
    }

    private void setupSettings() {
        binding.soundSwitch.setChecked(prefs.getBoolean(KEY_SOUND, true));
        binding.soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            audio.setEnabled(isChecked);
            prefs.edit().putBoolean(KEY_SOUND, isChecked).apply();
            addLog("CFG", isChecked ? "audio enabled" : "audio muted");
        });
        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetProgress();
            }
        });
    }

    private void answer(String text, int branch) {
        responded = true;
        response = text;
        if (branch == 0) {
            protocolTrust += 2;
        } else if (branch == 1) {
            signalCuriosity += 2;
        } else {
            memoryResistance += 2;
        }
        audio.playMessage();
        addLog("MSG_TX", text + " / hidden state updated");
        saveState();
        renderConversation();
    }

    private void renderConversation() {
        messages.clear();
        messages.add(new Message("HELENA", "Elias.", false));
        messages.add(new Message("HELENA", "Confirme o recebimento.", false));
        messages.add(new Message("HELENA", "Recebemos um sinal vindo de Vértice.", false));
        messages.add(new Message("HELENA", "A frequência ficou ativa por onze segundos.", false));
        messages.add(new Message("HELENA", "Não abra o arquivo bruto.", false));
        messages.add(new Message("HELENA", "Você ainda não lembra do protocolo.", false));
        if (responded) {
            messages.add(new Message("ELIAS", response, true));
            messages.add(new Message("HELENA", "A transmissão foi isolada. Restaure a amostra, não execute o bruto.", false));
        }
        binding.responseGroup.setVisibility(responded ? View.GONE : View.VISIBLE);
        messageAdapter.notifyDataSetChanged();
        if (!messages.isEmpty()) binding.messageList.scrollToPosition(messages.size() - 1);
    }

    private void renderTransmissions() {
        transmissions.clear();
        transmissions.add(new Transmission(
                "VX_0317_A.raw",
                "Origem: Vértice / Rádio Âncora",
                "Duração: 00:11",
                captured ? "Status: amostra capturada" : "Status: bruto bloqueado"));
        transmissionAdapter.notifyDataSetChanged();
    }

    private void renderLog() {
        logAdapter.notifyDataSetChanged();
        if (!logEntries.isEmpty()) binding.logList.scrollToPosition(logEntries.size() - 1);
    }

    private void updateRestore(boolean fromUser) {
        binding.frequencyReadout.setText(String.format(Locale.US, "%.2f", frequency));
        binding.spectrogramView.setFrequency(frequency);
        binding.spectrogramView.setCaptured(captured);
        boolean near = Math.abs(frequency - 3.17f) <= 0.025f;
        binding.captureButton.setEnabled(near && !captured);
        if (captured) {
            binding.restoreStatus.setText("...você voltou...");
        } else if (near) {
            binding.restoreStatus.setText("sinal estabilizado / pronto para captura");
            if (!nearLogged) {
                nearLogged = true;
                addLog("FREQ", "03.17 stabilized / carrier visible");
                audio.playNoise();
            }
        } else {
            binding.restoreStatus.setText("ruído bruto / procure a portadora 03.17");
            nearLogged = false;
        }
        int bucket = Math.round(frequency * 100f);
        if (fromUser && Math.abs(bucket - lastLoggedFrequency) >= 5) {
            lastLoggedFrequency = bucket;
            addLog("FREQ", String.format(Locale.US, "scan %.2f", frequency));
        }
    }

    private void captureSample() {
        if (captured || Math.abs(frequency - 3.17f) > 0.025f) return;
        captured = true;
        frequency = 3.17f;
        binding.frequencySlider.setValue(frequency);
        binding.restoreStatus.setText("...você voltou...");
        binding.captureButton.setEnabled(false);
        binding.spectrogramView.setCaptured(true);
        audio.playCapture();
        addLog("CAPTURE", "VX_0317_A sample captured / fragment decoded");
        saveState();
        renderTransmissions();
    }

    private void addLog(String type, String detail) {
        String time = new SimpleDateFormat("HH:mm:ss", Locale.US).format(new Date());
        logEntries.add(time + " // " + type + " // " + detail);
        while (logEntries.size() > 80) logEntries.remove(0);
        StringBuilder builder = new StringBuilder();
        for (String entry : logEntries) builder.append(entry).append('\n');
        prefs.edit().putString(KEY_LOGS, builder.toString()).apply();
        if (logAdapter != null) renderLog();
    }

    private void resetProgress() {
        prefs.edit().clear().apply();
        responded = false;
        captured = false;
        response = "";
        frequency = 2.84f;
        protocolTrust = 0;
        signalCuriosity = 0;
        memoryResistance = 0;
        nearLogged = false;
        lastLoggedFrequency = -1000;
        logEntries.clear();
        binding.soundSwitch.setChecked(true);
        audio.setEnabled(true);
        binding.frequencySlider.setValue(frequency);
        addLog("BOOT", "progress reset / clean session");
        saveState();
        renderConversation();
        renderTransmissions();
        showMessages();
    }

    private void showMessages() {
        showOnly(binding.screenMessages);
        binding.screenTitle.setText("HELENA");
        binding.screenSubtitle.setText("canal seguro / conversa interceptada");
    }

    private void showTransmissions() {
        showOnly(binding.screenTransmissions);
        binding.screenTitle.setText("TRANSMISSÕES");
        binding.screenSubtitle.setText("arquivos recebidos / bruto bloqueado");
        addLog("TRANS", "list viewed");
    }

    private void showRestore() {
        showOnly(binding.screenRestore);
        binding.screenTitle.setText("RESTAURAÇÃO");
        binding.screenSubtitle.setText("VX_0317_A.raw / espectrograma ativo");
        updateRestore(false);
    }

    private void showLog() {
        showOnly(binding.screenLog);
        binding.screenTitle.setText("LOG");
        binding.screenSubtitle.setText("registro técnico automático");
    }

    private void showMap() {
        showOnly(binding.screenMap);
        binding.screenTitle.setText("MAPA");
        binding.screenSubtitle.setText("Vértice / exploração bloqueada");
        addLog("MAP", "passive map opened");
    }

    private void showSettings() {
        showOnly(binding.screenSettings);
        binding.screenTitle.setText("CONFIGURAÇÕES");
        binding.screenSubtitle.setText("som / progresso / créditos");
    }

    private void showOnly(View active) {
        binding.screenMessages.setVisibility(active == binding.screenMessages ? View.VISIBLE : View.GONE);
        binding.screenTransmissions.setVisibility(active == binding.screenTransmissions ? View.VISIBLE : View.GONE);
        binding.screenRestore.setVisibility(active == binding.screenRestore ? View.VISIBLE : View.GONE);
        binding.screenLog.setVisibility(active == binding.screenLog ? View.VISIBLE : View.GONE);
        binding.screenMap.setVisibility(active == binding.screenMap ? View.VISIBLE : View.GONE);
        binding.screenSettings.setVisibility(active == binding.screenSettings ? View.VISIBLE : View.GONE);
    }

    private static int dp(View view, int value) {
        return (int) (value * view.getResources().getDisplayMetrics().density + 0.5f);
    }

    private static GradientDrawable bg(int color, int strokeColor, float radius, float density) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius * density);
        drawable.setStroke((int) (1 * density), strokeColor);
        return drawable;
    }

    private static class Message {
        final String speaker;
        final String text;
        final boolean player;

        Message(String speaker, String text, boolean player) {
            this.speaker = speaker;
            this.text = text;
            this.player = player;
        }
    }

    private static class Transmission {
        final String name;
        final String origin;
        final String duration;
        final String status;

        Transmission(String name, String origin, String duration, String status) {
            this.name = name;
            this.origin = origin;
            this.duration = duration;
            this.status = status;
        }
    }

    private static class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {
        private final List<Message> items;

        MessageAdapter(List<Message> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            LinearLayout row = new LinearLayout(parent.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, dp(row, 5), 0, dp(row, 5));
            row.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            TextView bubble = new TextView(parent.getContext());
            bubble.setTextSize(15f);
            bubble.setLineSpacing(2f, 1f);
            bubble.setPadding(dp(row, 14), dp(row, 10), dp(row, 14), dp(row, 10));
            bubble.setMaxWidth(dp(row, 310));
            row.addView(bubble, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            return new Holder(row, bubble);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Message message = items.get(position);
            LinearLayout row = (LinearLayout) holder.itemView;
            row.setGravity(message.player ? Gravity.END : Gravity.START);
            holder.bubble.setText(message.player ? message.text : message.speaker + "\n" + message.text);
            holder.bubble.setTextColor(message.player ? Color.rgb(6, 6, 7) : Color.rgb(244, 237, 226));
            float density = holder.bubble.getResources().getDisplayMetrics().density;
            holder.bubble.setBackground(bg(
                    message.player ? Color.rgb(216, 116, 50) : Color.rgb(26, 23, 20),
                    message.player ? Color.rgb(216, 116, 50) : Color.rgb(58, 48, 40),
                    18f,
                    density));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView bubble;

            Holder(@NonNull View itemView, TextView bubble) {
                super(itemView);
                this.bubble = bubble;
            }
        }
    }

    private static class TransmissionAdapter extends RecyclerView.Adapter<TransmissionAdapter.Holder> {
        interface Listener {
            void onOpen(Transmission transmission);
        }

        private final List<Transmission> items;
        private final Listener listener;

        TransmissionAdapter(List<Transmission> items, Listener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            MaterialCardView card = new MaterialCardView(parent.getContext());
            RecyclerView.LayoutParams cardParams = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, dp(card, 12));
            card.setLayoutParams(cardParams);
            card.setRadius(dp(card, 8));
            card.setCardBackgroundColor(Color.rgb(26, 23, 20));
            card.setStrokeColor(Color.rgb(58, 48, 40));
            card.setStrokeWidth(dp(card, 1));
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout box = new LinearLayout(parent.getContext());
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(card, 16), dp(card, 14), dp(card, 16), dp(card, 14));
            TextView name = rowText(parent, 18f, Color.rgb(244, 237, 226), true);
            TextView origin = rowText(parent, 13f, Color.rgb(169, 158, 144), false);
            TextView duration = rowText(parent, 13f, Color.rgb(169, 158, 144), false);
            TextView status = rowText(parent, 13f, Color.rgb(111, 155, 135), false);
            box.addView(name);
            box.addView(origin);
            box.addView(duration);
            box.addView(status);
            card.addView(box);
            return new Holder(card, name, origin, duration, status);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Transmission item = items.get(position);
            holder.name.setText(item.name);
            holder.origin.setText(item.origin);
            holder.duration.setText(item.duration);
            holder.status.setText(item.status);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOpen(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private static TextView rowText(android.view.ViewGroup parent, float size, int color, boolean strong) {
            TextView text = new TextView(parent.getContext());
            text.setTextSize(size);
            text.setTextColor(color);
            if (strong) text.setTypeface(Typeface.DEFAULT_BOLD);
            text.setPadding(0, 0, 0, dp(text, 5));
            return text;
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView name;
            final TextView origin;
            final TextView duration;
            final TextView status;

            Holder(@NonNull View itemView, TextView name, TextView origin, TextView duration, TextView status) {
                super(itemView);
                this.name = name;
                this.origin = origin;
                this.duration = duration;
                this.status = status;
            }
        }
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogAdapter.Holder> {
        private final List<String> items;

        LogAdapter(List<String> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            TextView text = new TextView(parent.getContext());
            text.setTextColor(Color.rgb(169, 158, 144));
            text.setTextSize(12f);
            text.setTypeface(Typeface.MONOSPACE);
            text.setPadding(0, dp(text, 6), 0, dp(text, 6));
            text.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            return new Holder(text);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            holder.text.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView text;

            Holder(@NonNull View itemView) {
                super(itemView);
                text = (TextView) itemView;
            }
        }
    }
}
