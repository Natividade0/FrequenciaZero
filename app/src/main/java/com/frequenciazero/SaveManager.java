package com.frequenciazero;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveManager {
    private static final String PREFS = "frequencia_zero_prologue";
    private static final String KEY_CHOICE = "prologueChoice";
    private static final String KEY_FINISHED = "prologueFinished";
    private static final String KEY_SOUND = "soundEnabled";

    private final SharedPreferences prefs;

    public SaveManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void saveChoice(String choice) {
        prefs.edit().putString(KEY_CHOICE, choice).apply();
    }

    public String getChoice() {
        return prefs.getString(KEY_CHOICE, "");
    }

    public void setPrologueFinished(boolean finished) {
        prefs.edit().putBoolean(KEY_FINISHED, finished).apply();
    }

    public boolean isPrologueFinished() {
        return prefs.getBoolean(KEY_FINISHED, false);
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND, true);
    }
}
