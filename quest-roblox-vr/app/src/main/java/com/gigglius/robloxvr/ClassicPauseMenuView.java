package com.gigglius.robloxvr;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Standalone classic in-game pause/settings panel.
 *
 * Attach this view over the original game surface when the native engine reports
 * that the pause menu is open. It deliberately does not replace the dashboard.
 * Engine actions are exposed through Listener so the native engine bridge can
 * implement resume/reset/leave/report behavior without coupling it to the UI.
 */
public final class ClassicPauseMenuView extends FrameLayout {
    public interface Listener {
        void onResume();
        void onResetCharacter();
        void onLeaveGame();
        void onReport(String reason, String description);
        void onSettingChanged(String key, String value);
    }

    private final Listener listener;
    private final LinearLayout panel;
    private int page = 0;

    public ClassicPauseMenuView(Context context, Listener listener) {
        super(context);
        this.listener = listener;
        setClickable(true);
        setBackgroundColor(0x99000000);

        panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER_HORIZONTAL);
        panel.setPadding(dp(22), dp(18), dp(22), dp(18));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xEE171717);
        bg.setStroke(dp(1), 0xFF777777);
        panel.setBackground(bg);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            dp(420), LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        addView(panel, params);
        showMain();
    }

    private int dp(float value) {
        return (int)(value * getResources().getDisplayMetrics().density + .5f);
    }

    private void heading(String title) {
        TextView t = new TextView(getContext());
        t.setText(title);
        t.setTextColor(Color.WHITE);
        t.setTextSize(22);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        panel.addView(t, new LinearLayout.LayoutParams(-1, dp(48)));
    }

    private void action(String label, Runnable callback) {
        Button b = new Button(getContext());
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF292929));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, dp(48));
        p.topMargin = dp(6);
        panel.addView(b, p);
        b.setOnClickListener(v -> callback.run());
    }

    private void clear(String title) {
        panel.removeAllViews();
        heading(title);
    }

    public void showMain() {
        page = 0;
        clear("Game Menu");
        action("Resume Game", () -> { if (listener != null) listener.onResume(); });
        action("Settings", this::showSettings);
        action("Reset Character", this::showResetConfirmation);
        action("Report Abuse", this::showReport);
        action("Leave Game", this::showLeaveConfirmation);
    }

    public void showSettings() {
        page = 1;
        clear("Settings");
        setting("Camera Mode", "Default", "CameraMode");
        setting("Movement Mode", "Default", "MovementMode");
        setting("Graphics Mode", "Automatic", "GraphicsMode");
        SeekBar volume = new SeekBar(getContext());
        volume.setMax(100);
        volume.setProgress(80);
        panel.addView(label("Volume"), new LinearLayout.LayoutParams(-1, dp(28)));
        panel.addView(volume, new LinearLayout.LayoutParams(-1, dp(42)));
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar b, int v, boolean user) {
                if (user && listener != null) listener.onSettingChanged("Volume", String.valueOf(v));
            }
            public void onStartTrackingTouch(SeekBar b) {}
            public void onStopTrackingTouch(SeekBar b) {}
        });
        action("Back", this::showMain);
    }

    private TextView label(String value) {
        TextView t = new TextView(getContext());
        t.setText(value);
        t.setTextColor(Color.WHITE);
        t.setTextSize(14);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private void setting(String title, String value, String key) {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        TextView name = label(title);
        row.addView(name, new LinearLayout.LayoutParams(0, dp(42), 1));
        Button choice = new Button(getContext());
        choice.setText(value);
        choice.setAllCaps(false);
        choice.setOnClickListener(v -> {
            String next = "Default".equals(value) ? "Follow" : "Default";
            choice.setText(next);
            if (listener != null) listener.onSettingChanged(key, next);
        });
        row.addView(choice, new LinearLayout.LayoutParams(dp(150), dp(42)));
        panel.addView(row, new LinearLayout.LayoutParams(-1, dp(46)));
    }

    public void showResetConfirmation() {
        page = 2;
        clear("Are you sure you want to reset your character?");
        action("Cancel", this::showMain);
        action("Reset Character", () -> {
            if (listener != null) listener.onResetCharacter();
            showMain();
        });
    }

    private void showLeaveConfirmation() {
        clear("Are you sure you want to leave this game?");
        action("Cancel", this::showMain);
        action("Leave", () -> { if (listener != null) listener.onLeaveGame(); });
    }

    private void showReport() {
        page = 3;
        clear("Report Abuse");
        final android.widget.Spinner reason = new android.widget.Spinner(getContext());
        String[] reasons = {"Choose One", "Inappropriate language", "Scamming", "Exploiting", "Other rule violation"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
            getContext(), android.R.layout.simple_spinner_dropdown_item, reasons);
        reason.setAdapter(adapter);
        panel.addView(reason, new LinearLayout.LayoutParams(-1, dp(48)));
        final android.widget.EditText description = new android.widget.EditText(getContext());
        description.setHint("Description");
        description.setTextColor(Color.WHITE);
        description.setHintTextColor(0xFFBBBBBB);
        description.setMinLines(3);
        panel.addView(description, new LinearLayout.LayoutParams(-1, dp(100)));
        action("Submit Report", () -> {
            if (listener != null) listener.onReport(
                String.valueOf(reason.getSelectedItem()), description.getText().toString());
            showMain();
        });
        action("Cancel", this::showMain);
    }
}
