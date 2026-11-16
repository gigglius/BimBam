package com.gigglius.robloxvr;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Quest-friendly port of the 2014 Roblox Android dashboard.
 * Artwork is imported directly from the supplied 2014 APK by
 * tools/import_2014_apk_assets.py. Service-backed data is still local placeholder data.
 */
public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(20, 64, 112);
    private static final int TEXT = Color.rgb(45, 55, 65);
    private static final int MUTED = Color.rgb(105, 116, 128);
    private String page = "Home";

    private int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    private TextView text(String value, int size, boolean bold) {
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(TEXT);
        v.setTextSize(size);
        v.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return v;
    }

    private Drawable image(int id) {
        try { return getResources().getDrawable(id, getTheme()); }
        catch (Exception ignored) { return null; }
    }

    private int iconFor(String label) {
        switch (label) {
            case "Games": return R.drawable.sb_games;
            case "Catalog": return R.drawable.sb_catalog;
            case "Community": return R.drawable.sb_community;
            case "My Roblox": case "Profile": return R.drawable.sb_my_roblox;
            case "Develop": return R.drawable.sb_develop;
            case "Messages": return R.drawable.messages2x;
            case "Groups": return R.drawable.groups2x;
            case "Friends": return R.drawable.community2x;
            default: return R.drawable.sb_account;
        }
    }

    private Button navButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(TEXT);
        b.setTextSize(13);
        b.setGravity(Gravity.CENTER_VERTICAL);
        b.setAllCaps(false);
        b.setPadding(dp(8), 0, dp(8), 0);
        b.setMinHeight(dp(42));
        b.setBackgroundColor(Color.TRANSPARENT);
        Drawable icon = image(iconFor(label));
        if (icon != null) {
            icon.setBounds(0, 0, dp(26), dp(26));
            b.setCompoundDrawables(icon, null, null, null);
            b.setCompoundDrawablePadding(dp(10));
        }
        b.setOnClickListener(v -> go(label.equals("My Roblox") ? "Profile" : label));
        return b;
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (state != null) page = state.getString("page", "Home");
        render();
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putString("page", page);
        super.onSaveInstanceState(out);
    }

    private void go(String destination) {
        page = destination;
        render();
    }

    private void render() {
        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.HORIZONTAL);
        shell.setBackgroundColor(Color.rgb(235, 239, 244));

        buildSidebar(shell);

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        right.setBackground(image(R.drawable.home_bkg));
        shell.addView(right, new LinearLayout.LayoutParams(0, -1, 4.1f));
        buildTopNavigation(right);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.TRANSPARENT);
        right.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(16), dp(10), dp(16), dp(16));
        body.setBackgroundColor(Color.argb(238, 247, 249, 252));
        scroll.addView(body);

        if ("Home".equals(page)) buildHome(body);
        else buildPage(body);

        setContentView(shell);
    }

    private void buildSidebar(LinearLayout parent) {
        LinearLayout side = new LinearLayout(this);
        side.setOrientation(LinearLayout.VERTICAL);
        side.setPadding(dp(10), dp(12), dp(10), dp(10));
        side.setBackground(image(R.drawable.lefthand_navigation_background2x));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(0, -1, 1.05f);
        sp.rightMargin = dp(1);
        parent.addView(side, sp);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.roblox_logo);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        side.addView(logo, new LinearLayout.LayoutParams(-1, dp(48)));

        View line = new View(this);
        line.setBackgroundColor(Color.rgb(220, 225, 231));
        LinearLayout.LayoutParams lineP = new LinearLayout.LayoutParams(-1, dp(1));
        lineP.topMargin = dp(8);
        lineP.bottomMargin = dp(8);
        side.addView(line, lineP);

        TextView account = text("1234", 20, true);
        account.setGravity(Gravity.CENTER);
        side.addView(account, new LinearLayout.LayoutParams(-1, dp(40)));

        LinearLayout balances = new LinearLayout(this);
        balances.setGravity(Gravity.CENTER);
        balances.setPadding(dp(4), 0, dp(4), 0);
        ImageView robuxIcon = new ImageView(this);
        robuxIcon.setImageResource(R.drawable.robux_symbol2x);
        robuxIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        balances.addView(robuxIcon, new LinearLayout.LayoutParams(dp(18), dp(18)));
        TextView robux = text("0", 13, true);
        robux.setGravity(Gravity.CENTER_VERTICAL);
        balances.addView(robux, new LinearLayout.LayoutParams(0, dp(34), 1));
        ImageView tixIcon = new ImageView(this);
        tixIcon.setImageResource(R.drawable.tix_symbol2x);
        tixIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        balances.addView(tixIcon, new LinearLayout.LayoutParams(dp(18), dp(18)));
        TextView tix = text("0", 13, true);
        tix.setGravity(Gravity.CENTER_VERTICAL);
        balances.addView(tix, new LinearLayout.LayoutParams(0, dp(34), 1));
        side.addView(balances);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(13);
        search.setTextColor(TEXT);
        search.setHintTextColor(MUTED);
        search.setHint("Search");
        search.setPadding(dp(8), 0, dp(8), 0);
        search.setBackgroundColor(Color.rgb(244, 246, 248));
        LinearLayout.LayoutParams searchP = new LinearLayout.LayoutParams(-1, dp(40));
        searchP.topMargin = dp(8);
        searchP.bottomMargin = dp(8);
        side.addView(search, searchP);

        String[] links = {"Home", "My Roblox", "Games", "Catalog", "Community", "Develop"};
        for (String link : links) side.addView(navButton(link),
            new LinearLayout.LayoutParams(-1, dp(43)));

        View spacer = new View(this);
        side.addView(spacer, new LinearLayout.LayoutParams(1, 0, 1));
        TextView footer = text("2014", 11, false);
        footer.setTextColor(MUTED);
        footer.setGravity(Gravity.CENTER);
        side.addView(footer, new LinearLayout.LayoutParams(-1, dp(28)));
    }

    private void buildTopNavigation(LinearLayout parent) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(12), dp(4), dp(12), dp(4));
        bar.setBackgroundColor(Color.rgb(250, 251, 253));
        parent.addView(bar, new LinearLayout.LayoutParams(-1, dp(54)));
        String[] links = {"Home", "Games", "Catalog", "Develop", "Groups"};
        for (String link : links) {
            TextView item = text(link, 14, "Home".equals(link) && "Home".equals(page));
            item.setGravity(Gravity.CENTER);
            item.setPadding(dp(8), 0, dp(8), 0);
            bar.addView(item, new LinearLayout.LayoutParams(0, -1, 1));
            item.setOnClickListener(v -> go(link));
        }
    }

    private void heading(LinearLayout parent, String value) {
        TextView h = text(value, 21, true);
        h.setTextColor(NAVY);
        h.setPadding(dp(2), dp(4), 0, dp(6));
        parent.addView(h, new LinearLayout.LayoutParams(-1, dp(42)));
    }

    private LinearLayout tile(String name, int imageId, boolean large, String subtitle) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setBackgroundColor(Color.WHITE);
        tile.setElevation(dp(2));

        FrameLayout artFrame = new FrameLayout(this);
        ImageView art = new ImageView(this);
        art.setImageResource(imageId);
        art.setScaleType(ImageView.ScaleType.FIT_XY);
        artFrame.addView(art, new FrameLayout.LayoutParams(-1, -1));

        TextView caption = text(name, large ? 15 : 11, true);
        caption.setTextColor(Color.WHITE);
        caption.setGravity(Gravity.CENTER);
        caption.setShadowLayer(2f, 1f, 1f, Color.BLACK);
        FrameLayout.LayoutParams captionParams = new FrameLayout.LayoutParams(
            -1, dp(large ? 30 : 24), Gravity.BOTTOM);
        artFrame.addView(caption, captionParams);
        tile.addView(artFrame, new LinearLayout.LayoutParams(-1, dp(large ? 174 : 132)));

        if (subtitle != null) {
            TextView sub = text(subtitle, 10, false);
            sub.setGravity(Gravity.CENTER);
            sub.setTextColor(MUTED);
            tile.addView(sub, new LinearLayout.LayoutParams(-1, dp(22)));
        }
        return tile;
    }

    private void addTile(GridLayout grid, String name, int imageId, boolean large, String subtitle) {
        LinearLayout item = tile(name, imageId, large, subtitle);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = large ? dp(230) : dp(145);
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.setMargins(dp(5), dp(5), dp(5), dp(5));
        grid.addView(item, lp);
        item.setOnClickListener(v -> go(name));
    }

    private void buildHome(LinearLayout body) {
        heading(body, "Games");
        GridLayout main = new GridLayout(this);
        main.setColumnCount(2);
        body.addView(main, new LinearLayout.LayoutParams(-1, -2));
        addTile(main, "Games", R.drawable.games2x, true, "Play games");
        addTile(main, "Catalog", R.drawable.catalog2x, true, "Browse avatar items");
        addTile(main, "Inventory", R.drawable.inventory2x, true, "Your items");
        addTile(main, "Builders Club", R.drawable.builders_club2x, true, "Membership");

        heading(body, "Community");
        GridLayout community = new GridLayout(this);
        community.setColumnCount(4);
        body.addView(community, new LinearLayout.LayoutParams(-1, -2));
        addTile(community, "Profile", R.drawable.profile2x, false, null);
        addTile(community, "Messages", R.drawable.messages2x, false, null);
        addTile(community, "Groups", R.drawable.groups2x, false, null);
        addTile(community, "Leaderboards", R.drawable.leaderboards2x, false, null);
    }

    private void section(LinearLayout parent, String title, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(12), dp(8), dp(12), dp(8));
        card.setBackgroundColor(Color.WHITE);
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.setMargins(0, dp(5), 0, dp(5));
        parent.addView(card, cp);
        TextView t = text(title, 16, true);
        card.addView(t, new LinearLayout.LayoutParams(-1, dp(30)));
        TextView d = text(detail, 12, false);
        d.setTextColor(MUTED);
        card.addView(d, new LinearLayout.LayoutParams(-1, dp(34)));
    }

    private void buildPage(LinearLayout body) {
        heading(body, page);
        if ("Games".equals(page)) {
            section(body, "Featured Games", "Featured experiences from the classic games directory.");
            section(body, "Popular", "Popular games.");
            section(body, "Recently Played", "Your recently played games.");
            section(body, "Favorites", "Your favorite games.");
        } else if ("Catalog".equals(page)) {
            section(body, "Catalog", "Browse avatar items and classic catalog categories.");
            section(body, "Hats  •  Gear  •  Faces  •  Shirts  •  Pants", "Select a category to browse items.");
        } else if ("Inventory".equals(page)) {
            section(body, "My Inventory", "Items owned by 1234.");
            section(body, "Hats  •  Gear  •  Faces  •  Shirts  •  Pants", "Your avatar items.");
        } else if ("Builders Club".equals(page)) {
            section(body, "Builders Club", "Classic membership information and benefits.");
            section(body, "Membership Status", "Membership details.");
        } else if ("Profile".equals(page)) {
            section(body, "1234", "Profile");
            section(body, "About Me", "Profile information.");
            section(body, "Currently Wearing", "Avatar items.");
            section(body, "Friends", "Friends list.");
        } else if ("Messages".equals(page)) {
            section(body, "Inbox", "Messages received by 1234.");
            section(body, "Sent", "Messages sent by 1234.");
            section(body, "Archived", "Archived messages.");
        } else if ("Groups".equals(page)) {
            section(body, "My Groups", "Groups joined by account 1234.");
            section(body, "Find Groups", "Browse the groups directory.");
        } else if ("Leaderboards".equals(page)) {
            section(body, "Leaderboards", "Classic player rankings and score lists.");
        } else if ("Develop".equals(page)) {
            section(body, "Create", "Your places and development tools.");
            section(body, "My Creations", "Created games and assets.");
        } else if ("Friends".equals(page)) {
            section(body, "Friends", "Friends and friend requests.");
        } else if ("Community".equals(page)) {
            section(body, "Profile", "Your profile and avatar.");
            section(body, "Messages", "Your messages.");
            section(body, "Groups", "Your groups.");
            section(body, "Leaderboards", "Player rankings.");
        }
        Button home = new Button(this);
        home.setText("Back to Home");
        home.setAllCaps(false);
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(dp(160), dp(44));
        hp.topMargin = dp(10);
        body.addView(home, hp);
        home.setOnClickListener(v -> go("Home"));
    }

    @Override public void onBackPressed() {
        if (!"Home".equals(page)) go("Home");
        else super.onBackPressed();
    }
}
