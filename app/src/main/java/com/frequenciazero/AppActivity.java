package com.frequenciazero;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

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

    private SharedPreferences prefs;
    private AudioEngine audio;

    private CinematicBackdropView cinematicBackdrop;
    private View titleScreen;
    private LinearLayout phoneShell;
    private MaterialCardView topPanel;
    private TextView startPrompt;
    private TextView screenTitle;
    private TextView screenSubtitle;
    private TextView livePill;
    private View signalDock;
    private View screenHome;
    private LinearLayout screenMessages;
    private LinearLayout screenTransmissions;
    private View screenFileDetail;
    private LinearLayout screenRestore;
    private LinearLayout screenSuccess;
    private LinearLayout screenLog;
    private View screenMap;
    private View screenSettings;
    private LinearLayout screenCall;
    private RecyclerView messageList;
    private RecyclerView transmissionList;
    private RecyclerView logList;
    private ChipGroup responseGroup;
    private TextView frequencyReadout;
    private TextView restoreStatus;
    private TextView callLine;
    private SpectrogramView spectrogramView;
    private SeekBar frequencySlider;
    private MaterialButton captureButton;
    private MaterialButton openRestoreButton;
    private MaterialButton saveAndCloseButton;
    private MaterialButton exportLogButton;
    private MaterialSwitch soundSwitch;
    private TextView navHome;
    private TextView navMessages;
    private TextView navTransmissions;
    private TextView navLog;
    private TextView navMap;
    private TextView navSettings;

    private final ArrayList<Message> messages = new ArrayList<Message>();
    private final ArrayList<Transmission> transmissions = new ArrayList<Transmission>();
    private final ArrayList<String> logEntries = new ArrayList<String>();
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
    private View currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        audio = new AudioEngine(this);
        bindViews();
        loadState();
        setupLists();
        setupResponses();
        setupRestore();
        setupNavigation();
        setupSettings();
        setupTitle();
        setupHome();
        setupCall();
        addLog("BOOT", "ECO-0 session started / cinematic shell ready");
        renderConversation();
        renderTransmissions();
        renderLog();
        showTitle();
    }

    @Override
    protected void onDestroy() {
        if (audio != null) audio.release();
        super.onDestroy();
    }

    private void bindViews() {
        cinematicBackdrop = findViewById(R.id.cinematicBackdrop);
        titleScreen = findViewById(R.id.titleScreen);
        phoneShell = findViewById(R.id.phoneShell);
        topPanel = findViewById(R.id.topPanel);
        startPrompt = findViewById(R.id.startPrompt);
        screenTitle = findViewById(R.id.screenTitle);
        screenSubtitle = findViewById(R.id.screenSubtitle);
        livePill = findViewById(R.id.livePill);
        signalDock = findViewById(R.id.signalDock);
        screenHome = findViewById(R.id.screenHome);
        screenMessages = findViewById(R.id.screenMessages);
        screenTransmissions = findViewById(R.id.screenTransmissions);
        screenFileDetail = findViewById(R.id.screenFileDetail);
        screenRestore = findViewById(R.id.screenRestore);
        screenSuccess = findViewById(R.id.screenSuccess);
        screenLog = findViewById(R.id.screenLog);
        screenMap = findViewById(R.id.screenMap);
        screenSettings = findViewById(R.id.screenSettings);
        screenCall = findViewById(R.id.screenCall);
        messageList = findViewById(R.id.messageList);
        transmissionList = findViewById(R.id.transmissionList);
        logList = findViewById(R.id.logList);
        responseGroup = findViewById(R.id.responseGroup);
        frequencyReadout = findViewById(R.id.frequencyReadout);
        restoreStatus = findViewById(R.id.restoreStatus);
        callLine = findViewById(R.id.callLine);
        spectrogramView = findViewById(R.id.spectrogramView);
        frequencySlider = findViewById(R.id.frequencySlider);
        captureButton = findViewById(R.id.captureButton);
        openRestoreButton = findViewById(R.id.openRestoreButton);
        saveAndCloseButton = findViewById(R.id.saveAndCloseButton);
        exportLogButton = findViewById(R.id.exportLogButton);
        soundSwitch = findViewById(R.id.soundSwitch);
        navHome = findViewById(R.id.navHome);
        navMessages = findViewById(R.id.navMessages);
        navTransmissions = findViewById(R.id.navTransmissions);
        navLog = findViewById(R.id.navLog);
        navMap = findViewById(R.id.navMap);
        navSettings = findViewById(R.id.navSettings);
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
                .putBoolean(KEY_SOUND, soundSwitch.isChecked())
                .apply();
    }

    private void setupLists() {
        messageAdapter = new MessageAdapter(messages);
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(messageAdapter);

        transmissionAdapter = new TransmissionAdapter(transmissions, new TransmissionAdapter.Listener() {
            @Override
            public void onOpen(Transmission transmission) {
                audio.playTransmission();
                addLog("TRANS", transmission.name + " opened / status " + transmission.statusLabel);
                if (transmission.playable) {
                    showFileDetail();
                } else {
                    Toast.makeText(AppActivity.this, "Arquivo danificado.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        transmissionList.setLayoutManager(new LinearLayoutManager(this));
        transmissionList.setAdapter(transmissionAdapter);

        logAdapter = new LogAdapter(logEntries);
        logList.setLayoutManager(new LinearLayoutManager(this));
        logList.setAdapter(logAdapter);
    }

    private void setupTitle() {
        View.OnClickListener start = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enterSystem();
            }
        };
        titleScreen.setOnClickListener(start);
        startPrompt.setOnClickListener(start);
    }

    private void setupHome() {
        findViewById(R.id.homeHelenaCard).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showMessages(); }
        });
        findViewById(R.id.homeMessages).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showMessages(); }
        });
        findViewById(R.id.homeFiles).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showTransmissions(); }
        });
        findViewById(R.id.homeCalls).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playTransmission(); showCall(); }
        });
        findViewById(R.id.homeMap).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showMap(); }
        });
        findViewById(R.id.homeLastTransmission).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playTransmission(); showFileDetail(); }
        });
    }

    private void setupResponses() {
        findViewById(R.id.responseConfirm).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { answer("Confirmado.", 0); }
        });
        findViewById(R.id.responseFrequency).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { answer("Que frequência?", 1); }
        });
        findViewById(R.id.responseMemory).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { answer("Por que eu esqueceria?", 2); }
        });
        findViewById(R.id.responseProtocol).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { answer("Qual protocolo?", 3); }
        });
    }

    private void setupRestore() {
        frequencySlider.setMax(75);
        frequencySlider.setProgress(frequencyToProgress(frequency));
        updateRestore(false);
        frequencySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = 2.70f + (progress / 100f);
                updateRestore(fromUser);
                saveState();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) { audio.playClick(); }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { updateRestore(true); }
        });
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { captureSample(); }
        });
        openRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playTransmission(); addLog("RESTORE", "tool opened / VX_0317_A.raw"); showRestore(); }
        });
        saveAndCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showMessages(); }
        });
        exportLogButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); Toast.makeText(AppActivity.this, "Exportação indisponível neste trecho.", Toast.LENGTH_SHORT).show(); }
        });
    }

    private int frequencyToProgress(float value) {
        return Math.max(0, Math.min(75, Math.round((value - 2.70f) * 100f)));
    }

    private void setupNavigation() {
        navHome.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        navMessages.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showMessages(); } });
        navTransmissions.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showTransmissions(); } });
        navLog.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showLog(); } });
        navMap.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showMap(); } });
        navSettings.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showSettings(); } });

        findViewById(R.id.backFromMessages).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.backFromTransmissions).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.backFromDetail).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showTransmissions(); } });
        findViewById(R.id.backFromRestore).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showFileDetail(); } });
        findViewById(R.id.backFromLog).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.backFromMap).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.backFromSettings).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.backFromCall).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playClick(); showHome(); } });
        findViewById(R.id.openCall).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { audio.playTransmission(); showCall(); } });
    }

    private void setupSettings() {
        soundSwitch.setChecked(prefs.getBoolean(KEY_SOUND, true));
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audio.setEnabled(isChecked);
                prefs.edit().putBoolean(KEY_SOUND, isChecked).apply();
                addLog("CFG", isChecked ? "audio enabled" : "audio muted");
            }
        });
        MaterialButton resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { resetProgress(); }
        });
    }

    private void setupCall() {
        findViewById(R.id.declineCall).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); addLog("CALL", "incoming call declined"); showHome(); }
        });
        findViewById(R.id.answerCall).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                audio.playTransmission();
                callLine.setText("Elias, tem coisas sobre Vértice\nque não estão nos arquivos.");
                addLog("CALL", "voice channel opened / partial warning received");
            }
        });
        findViewById(R.id.messageCall).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { audio.playClick(); showMessages(); }
        });
    }

    private void answer(String text, int branch) {
        responded = true;
        response = text;
        if (branch == 0) protocolTrust += 2;
        else if (branch == 1) signalCuriosity += 2;
        else if (branch == 2) memoryResistance += 2;
        else { protocolTrust += 1; signalCuriosity += 1; }
        audio.playMessage();
        addLog("MSG", "reply sent / hidden state updated");
        saveState();
        renderConversation();
        responseGroup.animate().alpha(0f).setDuration(180).start();
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
            messages.add(new Message("HELENA", "A transmissão foi isolada.", false));
            messages.add(new Message("HELENA", "Restaure a amostra, não execute o bruto.", false));
        }
        if (captured) {
            messages.add(new Message("SINAL", "...você voltou...", false));
            messages.add(new Message("HELENA", "Elias, tem coisas sobre Vértice que não estão nos arquivos.", false));
        }
        responseGroup.setVisibility(responded ? View.GONE : View.VISIBLE);
        responseGroup.setAlpha(1f);
        messageAdapter.notifyDataSetChanged();
        if (!messages.isEmpty()) messageList.scrollToPosition(messages.size() - 1);
    }

    private void renderTransmissions() {
        transmissions.clear();
        transmissions.add(new Transmission("VX_0317_A.raw", "Origem: Vértice", "Duração: 11:03", captured ? "AMOSTRA CAPTURADA" : "CORROMPIDO", true));
        transmissions.add(new Transmission("VX_0317_B.raw", "Origem: Vértice", "Duração: 04:21", "CORROMPIDO", false));
        transmissions.add(new Transmission("VX_0316_A.raw", "Origem: Vértice", "Duração: 07:47", "DANIFICADO", false));
        transmissions.add(new Transmission("VX_0315_A.raw", "Origem: Vértice", "Duração: 02:19", "DANIFICADO", false));
        transmissionAdapter.notifyDataSetChanged();
    }

    private void renderLog() {
        logAdapter.notifyDataSetChanged();
        if (!logEntries.isEmpty()) logList.scrollToPosition(logEntries.size() - 1);
    }

    private void updateRestore(boolean fromUser) {
        frequencyReadout.setText(String.format(Locale.US, "%.2f MHz", frequency));
        spectrogramView.setFrequency(frequency);
        spectrogramView.setCaptured(captured);
        boolean near = Math.abs(frequency - 3.17f) <= 0.025f;
        captureButton.setEnabled(near && !captured);
        if (captured) {
            restoreStatus.setText("...você voltou...");
        } else if (near) {
            restoreStatus.setText("O sinal estabilizou em 03.17.");
            if (!nearLogged) {
                nearLogged = true;
                addLog("FREQ", "03.17 MHz stabilized / carrier visible");
                audio.playNoise();
                captureButton.animate().scaleX(1.03f).scaleY(1.03f).setDuration(140).withEndAction(new Runnable() {
                    @Override public void run() { captureButton.animate().scaleX(1f).scaleY(1f).setDuration(140).start(); }
                }).start();
            }
        } else {
            restoreStatus.setText("Arraste devagar. Tem algo por baixo do ruído.");
            nearLogged = false;
        }
        int bucket = Math.round(frequency * 100f);
        if (fromUser && Math.abs(bucket - lastLoggedFrequency) >= 5) {
            lastLoggedFrequency = bucket;
            addLog("FREQ", String.format(Locale.US, "scan %.2f MHz", frequency));
        }
    }

    private void captureSample() {
        if (captured || Math.abs(frequency - 3.17f) > 0.025f) return;
        captured = true;
        frequency = 3.17f;
        frequencySlider.setProgress(frequencyToProgress(frequency));
        restoreStatus.setText("...você voltou...");
        captureButton.setEnabled(false);
        spectrogramView.setCaptured(true);
        audio.playCapture();
        addLog("CAP", "VX_0317_A sample captured / voice fragment recovered");
        saveState();
        renderConversation();
        renderTransmissions();
        showSuccess();
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
        soundSwitch.setChecked(true);
        audio.setEnabled(true);
        frequencySlider.setProgress(frequencyToProgress(frequency));
        addLog("BOOT", "progress reset / clean session");
        saveState();
        renderConversation();
        renderTransmissions();
        showTitle();
    }

    private void showTitle() {
        currentScreen = null;
        cinematicBackdrop.setMode(0);
        phoneShell.setVisibility(View.GONE);
        titleScreen.setVisibility(View.VISIBLE);
        titleScreen.setAlpha(1f);
        startPrompt.setAlpha(0.45f);
        startPrompt.animate().alpha(1f).setDuration(900).setStartDelay(250).start();
    }

    private void enterSystem() {
        audio.playTransmission();
        titleScreen.animate().alpha(0f).setDuration(320).withEndAction(new Runnable() {
            @Override public void run() {
                titleScreen.setVisibility(View.GONE);
                phoneShell.setVisibility(View.VISIBLE);
                showHome();
                playOpeningAnimation();
            }
        }).start();
    }

    private void showHome() {
        screenTitle.setText("ECO-0");
        screenSubtitle.setText("Sistema de Recuperação de Transmissões");
        livePill.setText("INSTÁVEL");
        showOnly(screenHome, 0, navHome);
    }

    private void showMessages() {
        screenTitle.setText("MENSAGENS");
        screenSubtitle.setText("HELENA / canal seguro");
        livePill.setText("ONLINE");
        addLog("MSG", "conversation viewed");
        showOnly(screenMessages, 1, navMessages);
    }

    private void showTransmissions() {
        screenTitle.setText("TRANSMISSÕES");
        screenSubtitle.setText("arquivos interceptados / bruto bloqueado");
        livePill.setText("ARQUIVOS");
        addLog("TRANS", "transmission list viewed");
        showOnly(screenTransmissions, 2, navTransmissions);
    }

    private void showFileDetail() {
        screenTitle.setText("VX_0317_A.raw");
        screenSubtitle.setText("Vértice / 11:03 / corrompido");
        livePill.setText(captured ? "CAPTURADO" : "ISOLADO");
        addLog("FILE", "VX_0317_A.raw metadata inspected");
        showOnly(screenFileDetail, 2, navTransmissions);
    }

    private void showRestore() {
        screenTitle.setText("RESTAURAÇÃO");
        screenSubtitle.setText("espectrograma ativo / controle manual");
        livePill.setText("03.17");
        updateRestore(false);
        showOnly(screenRestore, 3, navTransmissions);
    }

    private void showSuccess() {
        screenTitle.setText("AMOSTRA");
        screenSubtitle.setText("voz parcial recuperada");
        livePill.setText("CAPTURADA");
        showOnly(screenSuccess, 4, navTransmissions);
    }

    private void showLog() {
        screenTitle.setText("LOG");
        screenSubtitle.setText("registro técnico automático");
        livePill.setText("SISTEMA");
        showOnly(screenLog, 5, navLog);
    }

    private void showMap() {
        screenTitle.setText("MAPA");
        screenSubtitle.setText("Vértice / exploração bloqueada");
        livePill.setText("ZONA MORTA");
        addLog("MAP", "passive map opened");
        showOnly(screenMap, 6, navMap);
    }

    private void showSettings() {
        screenTitle.setText("CONFIGURAÇÕES");
        screenSubtitle.setText("som / progresso / créditos");
        livePill.setText("LOCAL");
        showOnly(screenSettings, 7, navSettings);
    }

    private void showCall() {
        screenTitle.setText("CHAMADA");
        screenSubtitle.setText("HELENA / canal instável");
        livePill.setText("VOZ");
        addLog("CALL", "incoming Helena channel viewed");
        showOnly(screenCall, 8, navMessages);
    }

    private void showOnly(View active, int mode, TextView selectedNav) {
        titleScreen.setVisibility(View.GONE);
        phoneShell.setVisibility(View.VISIBLE);
        topPanel.setVisibility(View.VISIBLE);
        signalDock.setVisibility(View.VISIBLE);
        cinematicBackdrop.setMode(mode);
        selectNav(selectedNav);
        View[] screens = {screenHome, screenMessages, screenTransmissions, screenFileDetail, screenRestore, screenSuccess, screenLog, screenMap, screenSettings, screenCall};
        for (View screen : screens) {
            if (screen == active) {
                screen.setVisibility(View.VISIBLE);
                if (currentScreen != active) {
                    screen.setAlpha(0f);
                    screen.setTranslationY(dp(screen, 12));
                    screen.animate().alpha(1f).translationY(0f).setDuration(260)
                            .setInterpolator(new DecelerateInterpolator()).start();
                }
            } else {
                screen.setVisibility(View.GONE);
            }
        }
        currentScreen = active;
    }

    private void selectNav(TextView selected) {
        TextView[] navs = {navHome, navMessages, navTransmissions, navLog, navMap, navSettings};
        for (TextView nav : navs) {
            boolean active = nav == selected;
            nav.setTextColor(active ? Color.rgb(5, 4, 4) : Color.rgb(178, 162, 146));
            nav.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
            nav.setBackground(active ? bg(Color.rgb(127, 175, 146), Color.rgb(127, 175, 146), 8f, nav.getResources().getDisplayMetrics().density) : null);
        }
        selected.setScaleX(0.95f);
        selected.setScaleY(0.95f);
        selected.animate().scaleX(1f).scaleY(1f).setDuration(160).start();
    }

    private void playOpeningAnimation() {
        phoneShell.setAlpha(0f);
        phoneShell.setTranslationY(dp(phoneShell, 18));
        topPanel.setTranslationY(-dp(topPanel, 10));
        phoneShell.animate().alpha(1f).translationY(0f).setDuration(460)
                .setInterpolator(new DecelerateInterpolator()).start();
        topPanel.animate().translationY(0f).setDuration(520).setStartDelay(90)
                .setInterpolator(new DecelerateInterpolator()).start();
        livePill.setAlpha(0.35f);
        livePill.animate().alpha(1f).setDuration(700).setStartDelay(220).start();
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
        final String statusLabel;
        final boolean playable;

        Transmission(String name, String origin, String duration, String statusLabel, boolean playable) {
            this.name = name;
            this.origin = origin;
            this.duration = duration;
            this.statusLabel = statusLabel;
            this.playable = playable;
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
            row.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            TextView bubble = new TextView(parent.getContext());
            bubble.setTextSize(14f);
            bubble.setLineSpacing(3f, 1f);
            bubble.setPadding(dp(row, 13), dp(row, 9), dp(row, 13), dp(row, 9));
            bubble.setMaxWidth(dp(row, 300));
            row.addView(bubble, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            return new Holder(row, bubble);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Message message = items.get(position);
            LinearLayout row = (LinearLayout) holder.itemView;
            row.setGravity(message.player ? Gravity.END : Gravity.START);
            holder.bubble.setText(message.player ? message.text : message.speaker + "\n" + message.text);
            holder.bubble.setTextColor(message.player ? Color.rgb(5, 4, 4) : Color.rgb(247, 238, 227));
            float density = holder.bubble.getResources().getDisplayMetrics().density;
            int fill = message.player ? Color.rgb(127, 175, 146) : Color.rgb(18, 15, 13);
            int stroke = message.player ? Color.rgb(127, 175, 146) : Color.rgb(70, 54, 43);
            holder.bubble.setBackground(bg(fill, stroke, 15f, density));
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(dp(holder.itemView, 8));
            holder.itemView.animate().alpha(1f).translationY(0f).setStartDelay(Math.min(260, position * 35L)).setDuration(210).start();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView bubble;
            Holder(@NonNull View itemView, TextView bubble) { super(itemView); this.bubble = bubble; }
        }
    }

    private static class TransmissionAdapter extends RecyclerView.Adapter<TransmissionAdapter.Holder> {
        interface Listener { void onOpen(Transmission transmission); }
        private final List<Transmission> items;
        private final Listener listener;
        TransmissionAdapter(List<Transmission> items, Listener listener) { this.items = items; this.listener = listener; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            MaterialCardView card = new MaterialCardView(parent.getContext());
            RecyclerView.LayoutParams cardParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, dp(card, 9));
            card.setLayoutParams(cardParams);
            card.setRadius(dp(card, 8));
            card.setCardBackgroundColor(Color.rgb(15, 13, 12));
            card.setStrokeColor(Color.rgb(70, 54, 43));
            card.setStrokeWidth(dp(card, 1));
            card.setClickable(true);
            card.setFocusable(true);
            LinearLayout box = new LinearLayout(parent.getContext());
            box.setOrientation(LinearLayout.VERTICAL);
            box.setPadding(dp(card, 14), dp(card, 12), dp(card, 14), dp(card, 12));
            TextView name = rowText(parent, 16f, Color.rgb(247, 238, 227), true);
            TextView origin = rowText(parent, 12f, Color.rgb(178, 162, 146), false);
            TextView duration = rowText(parent, 12f, Color.rgb(178, 162, 146), false);
            TextView status = rowText(parent, 12f, Color.rgb(224, 111, 47), false);
            box.addView(name); box.addView(origin); box.addView(duration); box.addView(status); card.addView(box);
            return new Holder(card, name, origin, duration, status);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            final Transmission item = items.get(position);
            holder.name.setText(item.name);
            holder.origin.setText(item.origin);
            holder.duration.setText(item.duration);
            holder.status.setText(item.statusLabel);
            holder.status.setTextColor(item.playable ? Color.rgb(127, 175, 146) : Color.rgb(182, 61, 50));
            holder.itemView.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View view) { listener.onOpen(item); } });
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(dp(holder.itemView, 10));
            holder.itemView.animate().alpha(1f).translationY(0f).setStartDelay(position * 45L).setDuration(240).start();
        }

        @Override
        public int getItemCount() { return items.size(); }

        private static TextView rowText(android.view.ViewGroup parent, float size, int color, boolean strong) {
            TextView text = new TextView(parent.getContext());
            text.setTextSize(size);
            text.setTextColor(color);
            if (strong) text.setTypeface(Typeface.DEFAULT_BOLD);
            text.setPadding(0, 0, 0, dp(text, 4));
            return text;
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView name; final TextView origin; final TextView duration; final TextView status;
            Holder(@NonNull View itemView, TextView name, TextView origin, TextView duration, TextView status) { super(itemView); this.name = name; this.origin = origin; this.duration = duration; this.status = status; }
        }
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogAdapter.Holder> {
        private final List<String> items;
        LogAdapter(List<String> items) { this.items = items; }
        @NonNull @Override public Holder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
            TextView text = new TextView(parent.getContext());
            text.setTextColor(Color.rgb(178, 162, 146));
            text.setTextSize(12f);
            text.setTypeface(Typeface.MONOSPACE);
            text.setPadding(0, dp(text, 6), 0, dp(text, 6));
            text.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new Holder(text);
        }
        @Override public void onBindViewHolder(@NonNull Holder holder, int position) { holder.text.setText(items.get(position)); }
        @Override public int getItemCount() { return items.size(); }
        static class Holder extends RecyclerView.ViewHolder { final TextView text; Holder(@NonNull View itemView) { super(itemView); text = (TextView) itemView; } }
    }
}
