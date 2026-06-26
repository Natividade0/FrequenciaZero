package com.frequenciazero;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class AppActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());

    private SaveManager saveManager;
    private AudioEngine audio;
    private FrameLayout root;
    private View titleScreen;
    private View lockScreen;
    private View chatScreen;
    private View fileScreen;
    private View glitchOverlay;
    private TextView startPrompt;
    private TextView notificationCard;
    private TextView typingIndicator;
    private TextView inputHint;
    private TextView audioCaption;
    private TextView playButton;
    private LinearLayout messageContainer;
    private LinearLayout choicePanel;
    private ScrollView chatScroll;
    private MiniWaveView waveView;

    private boolean firstChatScheduled;
    private boolean responseChosen;
    private boolean fileOpened;
    private boolean finalScheduled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.fz_black));
        window.setNavigationBarColor(getResources().getColor(R.color.fz_black));
        setContentView(R.layout.activity_main);

        saveManager = new SaveManager(this);
        audio = new AudioEngine(this, saveManager.isSoundEnabled());
        bindViews();
        setupTitle();
        setupLockScreen();
        setupChoices();
        setupFilePreview();
        showTitle();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (audio != null) audio.release();
        super.onDestroy();
    }

    private void bindViews() {
        root = findViewById(R.id.root);
        titleScreen = findViewById(R.id.titleScreen);
        lockScreen = findViewById(R.id.lockScreen);
        chatScreen = findViewById(R.id.chatScreen);
        fileScreen = findViewById(R.id.fileScreen);
        glitchOverlay = findViewById(R.id.glitchOverlay);
        startPrompt = findViewById(R.id.startPrompt);
        notificationCard = findViewById(R.id.notificationCard);
        typingIndicator = findViewById(R.id.typingIndicator);
        inputHint = findViewById(R.id.inputHint);
        audioCaption = findViewById(R.id.audioCaption);
        playButton = findViewById(R.id.playButton);
        messageContainer = findViewById(R.id.messageContainer);
        choicePanel = findViewById(R.id.choicePanel);
        chatScroll = findViewById(R.id.chatScroll);
        waveView = findViewById(R.id.waveView);
    }

    private void setupTitle() {
        View.OnClickListener start = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audio.playTap();
                flashGlitch(new Runnable() {
                    @Override public void run() { showLockScreen(); }
                });
            }
        };
        titleScreen.setOnClickListener(start);
        startPrompt.setOnClickListener(start);
    }

    private void setupLockScreen() {
        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audio.playMessageCue();
                showChat();
            }
        });
    }

    private void setupChoices() {
        findViewById(R.id.choiceConfirm).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { chooseResponse("Confirmar recebimento"); }
        });
        findViewById(R.id.choiceWho).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { chooseResponse("Quem é você?"); }
        });
        findViewById(R.id.choiceName).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { chooseResponse("Como sabe meu nome?"); }
        });
    }

    private void setupFilePreview() {
        findViewById(R.id.backFromFile).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { showChat(); }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { playCorruptedAudio(); }
        });
    }

    private void showTitle() {
        showOnly(titleScreen);
        titleScreen.setAlpha(0f);
        titleScreen.animate().alpha(1f).setDuration(700).start();
        pulseStartPrompt();
    }

    private void pulseStartPrompt() {
        if (startPrompt == null) return;
        startPrompt.animate().alpha(0.35f).setDuration(760).withEndAction(new Runnable() {
            @Override
            public void run() {
                startPrompt.animate().alpha(1f).setDuration(760).withEndAction(new Runnable() {
                    @Override public void run() { if (titleScreen.getVisibility() == View.VISIBLE) pulseStartPrompt(); }
                }).start();
            }
        }).start();
    }

    private void showLockScreen() {
        showOnly(lockScreen);
        notificationCard.setVisibility(View.INVISIBLE);
        notificationCard.setAlpha(0f);
        notificationCard.setTranslationY(-dp(50));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audio.playMessageCue();
                notificationCard.setVisibility(View.VISIBLE);
                notificationCard.animate().alpha(1f).translationY(0f).setDuration(420)
                        .setInterpolator(new DecelerateInterpolator()).start();
            }
        }, 650);
    }

    private void showChat() {
        showOnly(chatScreen);
        fileScreen.setVisibility(View.GONE);
        if (!firstChatScheduled) {
            firstChatScheduled = true;
            scheduleHelenaMessages();
        } else if (fileOpened && !finalScheduled) {
            scheduleFinalHook();
        }
    }

    private void scheduleHelenaMessages() {
        postTyping(500);
        postHelena(1200, "Elias.");
        postTyping(2200);
        postHelena(3000, "Confirme o recebimento.");
        postTyping(4200);
        postHelena(5000, "Recebemos um sinal vindo de Vértice.");
        postHelena(6300, "Ele ficou ativo por onze segundos.");
        postHelena(7600, "Antes de cair, ele enviou uma identificação.");
        postHelena(8800, "Era o seu nome.");
        handler.postDelayed(new Runnable() {
            @Override public void run() { showChoices(); }
        }, 9400);
    }

    private void showChoices() {
        if (responseChosen) return;
        typingIndicator.setVisibility(View.GONE);
        choicePanel.setVisibility(View.VISIBLE);
        choicePanel.setAlpha(0f);
        choicePanel.setTranslationY(dp(14));
        choicePanel.animate().alpha(1f).translationY(0f).setDuration(260).start();
        inputHint.setText("Escolha uma resposta...");
    }

    private void chooseResponse(String response) {
        if (responseChosen) return;
        responseChosen = true;
        saveManager.saveChoice(response);
        audio.playTap();
        choicePanel.setVisibility(View.GONE);
        inputHint.setText("Digite sua resposta...");
        addPlayerMessage(response);
        scheduleHelenaAfterChoice();
    }

    private void scheduleHelenaAfterChoice() {
        postTyping(500);
        postHelena(1300, "Não tenho tempo para explicar tudo.");
        postHelena(2400, "Um arquivo acabou de chegar no seu dispositivo.");
        postHelena(3400, "Não abra no volume alto.");
        handler.postDelayed(new Runnable() {
            @Override public void run() { addReceivedFileCard(); }
        }, 4300);
    }

    private void showFilePreview() {
        fileOpened = true;
        audio.playGlitch();
        audioCaption.setText("Áudio parcialmente corrompido.");
        waveView.setActive(false);
        showOnly(fileScreen);
        fileScreen.setAlpha(0f);
        fileScreen.animate().alpha(1f).setDuration(260).start();
    }

    private void playCorruptedAudio() {
        playButton.setEnabled(false);
        audio.playGlitch();
        waveView.setActive(true);
        audioCaption.setText("...");
        handler.postDelayed(new Runnable() {
            @Override public void run() { audioCaption.setText("...você..."); }
        }, 650);
        handler.postDelayed(new Runnable() {
            @Override public void run() { audioCaption.setText("...voltou..."); audio.playLowEnd(); }
        }, 1800);
        handler.postDelayed(new Runnable() {
            @Override public void run() { flashGlitch(null); }
        }, 2650);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                waveView.setActive(false);
                playButton.setEnabled(true);
                showChat();
            }
        }, 3200);
    }

    private void scheduleFinalHook() {
        finalScheduled = true;
        postTyping(450);
        postHelena(1100, "Você ouviu?");
        postHelena(2200, "Elias...");
        postHelena(3100, "Esse arquivo foi gravado nove anos atrás.");
        handler.postDelayed(new Runnable() {
            @Override public void run() { addFinalCard(); }
        }, 4100);
    }

    private void postTyping(long delayMs) {
        handler.postDelayed(new Runnable() {
            @Override public void run() { typingIndicator.setVisibility(View.VISIBLE); }
        }, delayMs);
    }

    private void postHelena(long delayMs, final String text) {
        handler.postDelayed(new Runnable() {
            @Override public void run() { addHelenaMessage(text); }
        }, delayMs);
    }

    private void addHelenaMessage(String text) {
        typingIndicator.setVisibility(View.GONE);
        audio.playMessageCue();
        addMessage(text, false);
    }

    private void addPlayerMessage(String text) {
        addMessage(text, true);
    }

    private void addMessage(String text, boolean player) {
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setTextSize(15f);
        bubble.setLineSpacing(dp(2), 1f);
        bubble.setTextColor(getResources().getColor(player ? R.color.fz_black : R.color.fz_text));
        bubble.setBackgroundResource(player ? R.drawable.bg_player_bubble : R.drawable.bg_helena_bubble);
        bubble.setPadding(dp(14), dp(10), dp(14), dp(10));
        bubble.setMaxWidth(dp(292));

        LinearLayout row = new LinearLayout(this);
        row.setGravity(player ? Gravity.RIGHT : Gravity.LEFT);
        row.setPadding(0, dp(4), 0, dp(4));
        row.addView(bubble, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageContainer.addView(row, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        row.setAlpha(0f);
        row.setTranslationY(dp(8));
        row.animate().alpha(1f).translationY(0f).setDuration(210).start();
        scrollToBottom();
    }

    private void addReceivedFileCard() {
        typingIndicator.setVisibility(View.GONE);
        audio.playGlitch();
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_file_card);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { showFilePreview(); }
        });

        TextView label = new TextView(this);
        label.setText("ARQUIVO RECEBIDO");
        label.setTextColor(getResources().getColor(R.color.fz_amber));
        label.setTextSize(12f);
        label.setLetterSpacing(0.08f);
        card.addView(label);

        TextView name = new TextView(this);
        name.setText("VX_0317_A.raw");
        name.setTextColor(getResources().getColor(R.color.fz_text));
        name.setTextSize(18f);
        name.setPadding(0, dp(8), 0, 0);
        card.addView(name);

        TextView hint = new TextView(this);
        hint.setText("Toque para abrir");
        hint.setTextColor(getResources().getColor(R.color.fz_text_secondary));
        hint.setTextSize(12f);
        hint.setPadding(0, dp(8), 0, 0);
        card.addView(hint);

        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.LEFT);
        row.setPadding(0, dp(8), 0, dp(8));
        row.addView(card, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageContainer.addView(row);
        row.setAlpha(0f);
        row.setTranslationY(dp(10));
        row.animate().alpha(1f).translationY(0f).setDuration(260).start();
        scrollToBottom();
    }

    private void addFinalCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setBackgroundResource(R.drawable.bg_file_card);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));

        TextView title = new TextView(this);
        title.setText("FIM DO PRÓLOGO");
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.08f);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(R.color.fz_text));
        title.setTextSize(16f);
        card.addView(title, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView subtitle = new TextView(this);
        subtitle.setText("Continua no Capítulo 1");
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextColor(getResources().getColor(R.color.fz_text_secondary));
        subtitle.setTextSize(14f);
        subtitle.setPadding(0, dp(10), 0, dp(16));
        card.addView(subtitle, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView save = new TextView(this);
        save.setText("SALVAR PROGRESSO");
        save.setGravity(Gravity.CENTER);
        save.setTextColor(getResources().getColor(R.color.fz_black));
        save.setTypeface(Typeface.DEFAULT_BOLD);
        save.setTextSize(13f);
        save.setBackgroundResource(R.drawable.bg_save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) { saveProgress(); }
        });
        card.addView(save, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(46)));

        LinearLayout row = new LinearLayout(this);
        row.setPadding(0, dp(14), 0, dp(18));
        row.addView(card, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        messageContainer.addView(row);
        row.setAlpha(0f);
        row.setTranslationY(dp(12));
        row.animate().alpha(1f).translationY(0f).setDuration(260).start();
        scrollToBottom();
    }

    private void saveProgress() {
        saveManager.setPrologueFinished(true);
        audio.playMessageCue();
        Snackbar.make(root, "Progresso salvo.", Snackbar.LENGTH_SHORT).show();
    }

    private void showOnly(View active) {
        titleScreen.setVisibility(active == titleScreen ? View.VISIBLE : View.GONE);
        lockScreen.setVisibility(active == lockScreen ? View.VISIBLE : View.GONE);
        chatScreen.setVisibility(active == chatScreen ? View.VISIBLE : View.GONE);
        fileScreen.setVisibility(active == fileScreen ? View.VISIBLE : View.GONE);
    }

    private void flashGlitch(final Runnable after) {
        glitchOverlay.setVisibility(View.VISIBLE);
        glitchOverlay.setAlpha(0f);
        glitchOverlay.animate().alpha(0.42f).setDuration(55).withEndAction(new Runnable() {
            @Override
            public void run() {
                glitchOverlay.animate().alpha(0f).setDuration(95).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        glitchOverlay.setVisibility(View.GONE);
                        if (after != null) after.run();
                    }
                }).start();
            }
        }).start();
    }

    private void scrollToBottom() {
        chatScroll.post(new Runnable() {
            @Override public void run() { chatScroll.fullScroll(View.FOCUS_DOWN); }
        });
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
