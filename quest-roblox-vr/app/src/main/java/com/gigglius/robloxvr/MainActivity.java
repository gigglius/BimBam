package com.gigglius.robloxvr;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Classic 2014 Roblox mobile dashboard recreation.
 * The dashboard is separate from the original in-game native TopMenuBar.
 */
public class MainActivity extends Activity {
    private static final int BLUE = Color.rgb(27, 91, 164);
    private static final int DARK_BLUE = Color.rgb(18, 65, 126);
    private static final int PANEL = Color.rgb(35, 105, 180);
    private static final int LIGHT = Color.rgb(222, 238, 255);
    private String page = "Home";
    private FrameLayout frame;

    private int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    private GradientDrawable bg(int color, int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(3));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }

    private TextView label(String s, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(Color.WHITE);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }

    private Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setPadding(dp(8), 0, dp(8), 0);
        b.setBackground(bg(DARK_BLUE, Color.rgb(135, 184, 230)));
        return b;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        render();
    }

    private void render() {
        frame = new FrameLayout(this);
        frame.setBackground(new GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{Color.rgb(54,145,220), Color.rgb(20,73,143), Color.rgb(10,48,103)}));

        LinearLayout pageRoot = new LinearLayout(this);
        pageRoot.setOrientation(LinearLayout.VERTICAL);
        pageRoot.setPadding(dp(12), dp(7), dp(12), dp(10));
        frame.addView(pageRoot, new FrameLayout.LayoutParams(-1, -1));

        // Header: old Roblox wordmark and account currency strip.
        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(10), 0, dp(10), 0);
        header.setBackground(bg(Color.rgb(14,58,119), Color.rgb(112,174,225)));
        pageRoot.addView(header, new LinearLayout.LayoutParams(-1, dp(48)));

        TextView logo = label("ROBLOX", 25, true);
        logo.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        header.addView(logo, new LinearLayout.LayoutParams(0, -1, 1));
        TextView money = label("R$ 0     ◆ 100", 16, true);
        money.setGravity(Gravity.CENTER);
        header.addView(money);

        TextView title = label(page.equals("Home") ? "Welcome to ROBLOX" : page, 21, true);
        title.setPadding(dp(4), dp(4), 0, dp(4));
        pageRoot.addView(title, new LinearLayout.LayoutParams(-1, dp(43)));

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        pageRoot.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));

        if (page.equals("Home")) {
            buildProfile(body);
            buildTiles(body);
        } else {
            buildSubpage(body);
        }

        // Small footer reminiscent of the old blue mobile shell.
        TextView footer = label("ⓘ   ROBLOX 2014", 11, false);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(Color.rgb(205,226,248));
        pageRoot.addView(footer, new LinearLayout.LayoutParams(-1, dp(22)));
        setContentView(frame);
    }

    private void buildProfile(LinearLayout body) {
        LinearLayout profile = new LinearLayout(this);
        profile.setOrientation(LinearLayout.VERTICAL);
        profile.setPadding(dp(12), dp(12), dp(12), dp(10));
        profile.setBackground(bg(Color.rgb(19,76,145), Color.rgb(112,174,225)));
        LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams(0, -1, 0.88f);
        pp.setMargins(0, 0, dp(10), 0);
        body.addView(profile, pp);

        TextView avatar = label("◉", 66, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(bg(Color.rgb(34,111,185), Color.rgb(145,197,239)));
        profile.addView(avatar, new LinearLayout.LayoutParams(-1, 0, 1.1f));

        TextView name = label("1234", 20, true);
        name.setGravity(Gravity.CENTER);
        profile.addView(name, new LinearLayout.LayoutParams(-1, dp(38)));

        TextView balances = label("R$ 0     TIX 100", 14, true);
        balances.setGravity(Gravity.CENTER);
        balances.setBackground(bg(Color.rgb(12,58,119), 0));
        profile.addView(balances, new LinearLayout.LayoutParams(-1, dp(34)));

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setHint("Search");
        search.setHintTextColor(Color.rgb(215,230,247));
        search.setTextColor(Color.WHITE);
        search.setPadding(dp(8), 0, dp(8), 0);
        search.setBackground(bg(Color.rgb(11,55,112), Color.rgb(117,174,224)));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, dp(40));
        sp.topMargin = dp(12);
        profile.addView(search, sp);

        Button logout = button("Log Out");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(42));
        lp.topMargin = dp(8);
        profile.addView(logout, lp);
        logout.setOnClickListener(v -> showNotice("Log out", "Account sign-out needs the original Roblox service."));
    }

    private void buildTiles(LinearLayout body) {
        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(2);
        grid.setRowCount(2);
        body.addView(grid, new LinearLayout.LayoutParams(0, -1, 1.55f));
        String[][] data = {
            {"Games", "▶"}, {"Catalog", "▦"},
            {"Inventory", "▤"}, {"Builders Club", "♛"}
        };
        for (String[] item : data) {
            LinearLayout tile = new LinearLayout(this);
            tile.setOrientation(LinearLayout.VERTICAL);
            tile.setGravity(Gravity.CENTER);
            tile.setBackground(bg(Color.rgb(27,91,158), Color.rgb(133,190,236)));
            tile.setPadding(dp(7), dp(6), dp(7), dp(6));
            TextView glyph = label(item[1], 45, true);
            glyph.setGravity(Gravity.CENTER);
            tile.addView(glyph, new LinearLayout.LayoutParams(-1, 0, 1));
            TextView name = label(item[0], 17, true);
            name.setGravity(Gravity.CENTER);
            tile.addView(name, new LinearLayout.LayoutParams(-1, dp(36)));
            GridLayout.LayoutParams p = new GridLayout.LayoutParams();
            p.width = 0;
            p.height = 0;
            p.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            p.setMargins(dp(4), dp(4), dp(4), dp(4));
            grid.addView(tile, p);
            tile.setOnClickListener(v -> { page = item[0]; render(); });
        }
    }

    private void buildSubpage(LinearLayout body) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(14), dp(16), dp(14));
        panel.setBackground(bg(Color.rgb(20,77,145), Color.rgb(112,174,225)));
        body.addView(panel, new LinearLayout.LayoutParams(-1, -1));
        TextView heading = label(page, 22, true);
        panel.addView(heading);
        TextView message = label(description(page), 15, false);
        message.setPadding(0, dp(12), 0, dp(18));
        panel.addView(message);
        Button back = button("‹  Back to Home");
        panel.addView(back, new LinearLayout.LayoutParams(-1, dp(46)));
        back.setOnClickListener(v -> { page = "Home"; render(); });
    }

    private String description(String p) {
        if (p.equals("Games")) return "Games available on the classic Roblox client.";
        if (p.equals("Catalog")) return "Browse hats, gear, faces, and other catalog items.";
        if (p.equals("Inventory")) return "Your owned Roblox items.";
        if (p.equals("Builders Club")) return "Builders Club membership.";
        return "Classic Roblox " + p;
    }

    private void showNotice(String title, String message) {
        new android.app.AlertDialog.Builder(this)
            .setTitle(title).setMessage(message).setPositiveButton("OK", null).show();
    }

    @Override public void onBackPressed() {
        if (!page.equals("Home")) { page = "Home"; render(); }
        else super.onBackPressed();
    }
}
