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
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Re-creation of the classic blue Roblox web-dashboard shown in the
 * supplied reference recording. This is the app's front-end navigation;
 * it is separate from the original in-game TopMenuBar in libroblox.so.
 */
public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(13, 38, 83);
    private static final int BLUE = Color.rgb(22, 69, 139);
    private static final int TILE = Color.rgb(24, 58, 105);
    private static final int TEXT = Color.WHITE;
    private LinearLayout content;
    private String currentPage = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDashboard();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        d.setStroke(dp(1), Color.argb(70, 190, 215, 255));
        return d;
    }

    private TextView text(String value, int size, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(TEXT);
        v.setTextSize(size);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setGravity(Gravity.CENTER_VERTICAL);
        return v;
    }

    private void showDashboard() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(NAVY);

        LinearLayout sidebar = new LinearLayout(this);
        sidebar.setOrientation(LinearLayout.VERTICAL);
        sidebar.setPadding(dp(12), dp(16), dp(12), dp(12));
        sidebar.setBackgroundColor(Color.rgb(9, 29, 65));
        root.addView(sidebar, new LinearLayout.LayoutParams(dp(190), -1));

        TextView brand = text("ROBLOX", 25, true);
        brand.setGravity(Gravity.CENTER);
        brand.setPadding(0, dp(5), 0, dp(18));
        sidebar.addView(brand);

        TextView avatar = text("◉", 46, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(shape(BLUE, 8));
        sidebar.addView(avatar, new LinearLayout.LayoutParams(-1, dp(82)));

        TextView username = text("Player", 17, true);
        username.setGravity(Gravity.CENTER);
        username.setPadding(0, dp(8), 0, dp(12));
        sidebar.addView(username);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setHint("Search");
        search.setHintTextColor(Color.LTGRAY);
        search.setTextColor(TEXT);
        search.setPadding(dp(9), 0, dp(9), 0);
        search.setBackground(shape(Color.rgb(18, 45, 88), 5));
        sidebar.addView(search, new LinearLayout.LayoutParams(-1, dp(42)));

        addSideLink(sidebar, "Home");
        addSideLink(sidebar, "Games");
        addSideLink(sidebar, "Catalog");
        addSideLink(sidebar, "Profile");
        addSideLink(sidebar, "Messages");
        addSideLink(sidebar, "Groups");
        addSideLink(sidebar, "Settings");
        View spacer = new View(this);
        sidebar.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));
        addSideLink(sidebar, "Log Out");

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(18), dp(12), dp(18), dp(14));
        root.addView(main, new LinearLayout.LayoutParams(0, -1, 1));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView heading = text(currentPage, 23, true);
        top.addView(heading, new LinearLayout.LayoutParams(0, dp(48), 1));
        TextView currency = text("R$ 22   ◈ 100", 15, true);
        currency.setGravity(Gravity.CENTER);
        currency.setPadding(dp(10), 0, dp(10), 0);
        top.addView(currency);
        main.addView(top);

        if (!"Home".equals(currentPage)) {
            TextView back = text("‹  Home", 15, true);
            back.setPadding(dp(8), dp(5), 0, dp(10));
            back.setOnClickListener(v -> { currentPage = "Home"; showDashboard(); });
            main.addView(back);
            TextView pageTitle = text(currentPage, 21, true);
            pageTitle.setPadding(dp(10), dp(12), 0, dp(5));
            main.addView(pageTitle);
            TextView description = text(pageDescription(currentPage), 15, false);
            description.setPadding(dp(10), dp(8), 0, dp(8));
            main.addView(description);
        } else {
            TextView welcome = text("Welcome to ROBLOX", 18, true);
            welcome.setPadding(dp(2), dp(8), 0, dp(14));
            main.addView(welcome);
            GridLayout grid = new GridLayout(this);
            grid.setColumnCount(2);
            grid.setRowCount(4);
            String[][] items = {
                {"Games", "🎮", "Play games"},
                {"Catalog", "▦", "Browse items"},
                {"Inventory", "▤", "Your items"},
                {"Builders Club", "♛", "Membership"},
                {"Profile", "▣", "Your profile"},
                {"Messages", "✉", "Inbox"},
                {"Groups", "◉", "Communities"},
                {"Leaderboards", "★", "Top players"}
            };
            for (String[] item : items) {
                LinearLayout tile = new LinearLayout(this);
                tile.setOrientation(LinearLayout.VERTICAL);
                tile.setGravity(Gravity.CENTER);
                tile.setPadding(dp(8), dp(8), dp(8), dp(8));
                tile.setBackground(shape(TILE, 5));
                TextView icon = text(item[1], 35, true);
                icon.setGravity(Gravity.CENTER);
                tile.addView(icon, new LinearLayout.LayoutParams(-1, 0, 1));
                TextView label = text(item[0], 17, true);
                label.setGravity(Gravity.CENTER);
                tile.addView(label);
                TextView hint = text(item[2], 11, false);
                hint.setTextColor(Color.LTGRAY);
                hint.setGravity(Gravity.CENTER);
                tile.addView(hint);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = 0;
                lp.height = dp(112);
                lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                lp.setMargins(dp(5), dp(5), dp(5), dp(5));
                grid.addView(tile, lp);
                tile.setOnClickListener(v -> { currentPage = item[0]; showDashboard(); });
            }
            main.addView(grid, new LinearLayout.LayoutParams(-1, 0, 1));
        }

        setContentView(root);
    }

    private void addSideLink(LinearLayout parent, String label) {
        TextView link = text(label, 15, true);
        link.setPadding(dp(10), 0, 0, 0);
        link.setBackground(shape(Color.TRANSPARENT, 4));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(40));
        lp.topMargin = dp(3);
        parent.addView(link, lp);
        link.setOnClickListener(v -> { currentPage = label; showDashboard(); });
    }

    private String pageDescription(String page) {
        if ("Games".equals(page)) return "Discover and play classic Roblox experiences.";
        if ("Catalog".equals(page)) return "Featured items on ROBLOX";
        if ("Inventory".equals(page)) return "Your hats, gear, models, and other items.";
        if ("Builders Club".equals(page)) return "Membership and account benefits.";
        if ("Profile".equals(page)) return "Your Profile     Active Places     My Friends     Favorites";
        if ("Messages".equals(page)) return "Your messages and notifications.";
        if ("Groups".equals(page)) return "Your groups and communities.";
        if ("Leaderboards".equals(page)) return "Player rankings and leaderboards.";
        if ("Settings".equals(page)) return "Account settings.";
        if ("Log Out".equals(page)) return "Sign-out is not connected in this offline UI prototype.";
        return "Classic Roblox";
    }

    @Override
    public void onBackPressed() {
        if (!"Home".equals(currentPage)) {
            currentPage = "Home";
            showDashboard();
        } else {
            super.onBackPressed();
        }
    }
}
