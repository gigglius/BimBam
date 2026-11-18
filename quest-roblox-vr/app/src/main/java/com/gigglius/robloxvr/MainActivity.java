package com.gigglius.robloxvr;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Layout-first recreation of the classic 2014 Roblox mobile home menu.
 * Artwork is intentionally omitted so original assets can be added later.
 */
public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(10, 54, 111);
    private static final int BLUE = Color.rgb(20, 91, 164);
    private static final int EDGE = Color.rgb(91, 158, 216);
    private String page = "Home";

    private int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    private GradientDrawable panel(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        if (stroke != 0) d.setStroke(dp(1), stroke);
        d.setCornerRadius(dp(2));
        return d;
    }

    private TextView text(String value, int size, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(Color.WHITE);
        v.setTextSize(size);
        v.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setBackground(panel(NAVY, EDGE));
        return b;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Log.i("BimBamEngine", LegacyEngineBootstrap.inspect(this));
        Log.i("BimBamEngine", LegacyEngineBootstrap.inspect(this));
        render();
    }

    private void render() {
        FrameLayout root = new FrameLayout(this);
        // Temporary blue field. The city background and menu artwork are deliberately
        // left out and can be installed later without changing this layout.
        root.setBackground(new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{Color.rgb(38, 112, 190), Color.rgb(12, 65, 130)}));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setPadding(dp(14), dp(12), dp(14), dp(12));
        root.addView(content, new FrameLayout.LayoutParams(-1, -1));

        if ("Home".equals(page)) {
            buildProfile(content);
            buildHomeTiles(content);
        } else {
            buildSubpage(content);
        }

        // Small info control retained at the lower-right, as in the old shell.
        TextView info = text("ⓘ", 19, true);
        info.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(dp(38), dp(32),
            Gravity.BOTTOM | Gravity.RIGHT);
        ip.setMargins(0, 0, dp(10), dp(5));
        root.addView(info, ip);
        setContentView(root);
    }

    private void buildProfile(LinearLayout parent) {
        LinearLayout profile = new LinearLayout(this);
        profile.setOrientation(LinearLayout.VERTICAL);
        profile.setPadding(dp(10), dp(10), dp(10), dp(10));
        profile.setBackground(panel(Color.rgb(15, 70, 137), EDGE));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 0.78f);
        params.rightMargin = dp(10);
        parent.addView(profile, params);

        // Reserved avatar area: original avatar artwork will be inserted here later.
        FrameLayout avatarSlot = new FrameLayout(this);
        avatarSlot.setBackground(panel(Color.rgb(24, 91, 158), Color.rgb(73, 143, 205)));
        profile.addView(avatarSlot, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        TextView username = text("1234", 21, true);
        username.setGravity(Gravity.CENTER);
        profile.addView(username, new LinearLayout.LayoutParams(-1, dp(40)));

        LinearLayout balances = new LinearLayout(this);
        balances.setGravity(Gravity.CENTER);
        balances.setBackground(panel(NAVY, 0));
        TextView robux = text("R$ 0", 15, true);
        robux.setGravity(Gravity.CENTER);
        TextView tix = text("TIX 100", 15, true);
        tix.setGravity(Gravity.CENTER);
        balances.addView(robux, new LinearLayout.LayoutParams(0, -1, 1));
        balances.addView(tix, new LinearLayout.LayoutParams(0, -1, 1));
        profile.addView(balances, new LinearLayout.LayoutParams(-1, dp(38)));

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(15);
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.rgb(205, 224, 244));
        search.setHint("Search");
        search.setPadding(dp(9), 0, dp(9), 0);
        search.setBackground(panel(Color.rgb(11, 57, 112), EDGE));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(42));
        searchParams.topMargin = dp(10);
        profile.addView(search, searchParams);

        View spacer = new View(this);
        profile.addView(spacer, new LinearLayout.LayoutParams(1, 0, .12f));

        Button logout = button("Log Out");
        profile.addView(logout, new LinearLayout.LayoutParams(-1, dp(42)));
        logout.setOnClickListener(v -> new android.app.AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Account sign-out requires the original Roblox service.")
            .setPositiveButton("OK", null).show());
    }

    private void buildHomeTiles(LinearLayout parent) {
        GridLayout tiles = new GridLayout(this);
        tiles.setColumnCount(2);
        tiles.setRowCount(2);
        parent.addView(tiles, new LinearLayout.LayoutParams(0, -1, 1.72f));

        String[] names = {"Games", "Catalog", "Inventory", "Builders Club"};
        for (String name : names) {
            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackground(panel(Color.rgb(24, 91, 158), Color.rgb(99, 165, 220)));
            tile.setPadding(dp(5), dp(5), dp(5), dp(5));

            // Empty image well reserved for the original tile artwork.
            FrameLayout artworkSlot = new FrameLayout(this);
            artworkSlot.setBackgroundColor(Color.TRANSPARENT);
            tile.addView(artworkSlot, new LinearLayout.LayoutParams(-1, 0, 1));

            TextView caption = text(name, 18, true);
            caption.setGravity(Gravity.CENTER);
            tile.addView(caption, new LinearLayout.LayoutParams(-1, dp(42)));

            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(5), dp(5), dp(5), dp(5));
            tiles.addView(tile, lp);
            tile.setOnClickListener(v -> { page = name; render(); });
        }
    }

    private void buildSubpage(LinearLayout parent) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(18), dp(16), dp(18), dp(16));
        panel.setBackground(panel(Color.rgb(15, 70, 137), EDGE));
        parent.addView(panel, new LinearLayout.LayoutParams(-1, -1));

        TextView heading = text(page, 24, true);
        panel.addView(heading, new LinearLayout.LayoutParams(-1, dp(48)));
        Button back = button("‹  Back");
        panel.addView(back, new LinearLayout.LayoutParams(dp(150), dp(44)));
        back.setOnClickListener(v -> { page = "Home"; render(); });
    }

    @Override public void onBackPressed() {
        if (!"Home".equals(page)) { page = "Home"; render(); }
        else super.onBackPressed();
    }
}
