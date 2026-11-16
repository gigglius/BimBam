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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Full-screen, layout-first 2014 Roblox dashboard shell for the Quest port.
 * It opens directly on Home. Page navigation is local until services are connected.
 */
public class MainActivity extends Activity {
    private static final int NAVY = Color.rgb(10, 54, 111);
    private static final int BLUE = Color.rgb(25, 105, 180);
    private static final int PANEL = Color.rgb(17, 75, 143);
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
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setMinHeight(dp(38));
        b.setBackground(panel(NAVY, EDGE));
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
        FrameLayout root = new FrameLayout(this);
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
            new int[]{Color.rgb(39, 112, 188), Color.rgb(11, 59, 119)}));

        LinearLayout shell = new LinearLayout(this);
        shell.setOrientation(LinearLayout.HORIZONTAL);
        shell.setPadding(dp(12), dp(10), dp(12), dp(10));
        root.addView(shell, new FrameLayout.LayoutParams(-1, -1));

        buildSidebar(shell);

        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, -1, 3.15f);
        shell.addView(right, rightParams);
        buildTopNavigation(right);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        right.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout pageBody = new LinearLayout(this);
        pageBody.setOrientation(LinearLayout.VERTICAL);
        pageBody.setPadding(dp(8), dp(6), dp(8), dp(8));
        scroll.addView(pageBody);

        if ("Home".equals(page)) buildHome(pageBody);
        else buildPage(pageBody);

        TextView info = text("ⓘ", 18, true);
        info.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams ip = new FrameLayout.LayoutParams(dp(32), dp(28),
            Gravity.BOTTOM | Gravity.RIGHT);
        root.addView(info, ip);
        setContentView(root);
    }

    private void buildSidebar(LinearLayout parent) {
        LinearLayout side = new LinearLayout(this);
        side.setOrientation(LinearLayout.VERTICAL);
        side.setPadding(dp(9), dp(9), dp(9), dp(9));
        side.setBackground(panel(Color.rgb(13, 65, 128), EDGE));
        LinearLayout.LayoutParams sideParams = new LinearLayout.LayoutParams(0, -1, 0.92f);
        sideParams.rightMargin = dp(9);
        parent.addView(side, sideParams);

        FrameLayout avatar = new FrameLayout(this);
        avatar.setBackground(panel(Color.rgb(31, 98, 163), EDGE));
        TextView avatarLabel = text("AVATAR", 12, true);
        avatarLabel.setGravity(Gravity.CENTER);
        avatar.addView(avatarLabel, new FrameLayout.LayoutParams(-1, -1));
        side.addView(avatar, new LinearLayout.LayoutParams(-1, 0, 1.0f));

        TextView username = text("1234", 21, true);
        username.setGravity(Gravity.CENTER);
        side.addView(username, new LinearLayout.LayoutParams(-1, dp(38)));

        LinearLayout money = new LinearLayout(this);
        money.setBackground(panel(NAVY, 0));
        TextView robux = text("R$ 0", 14, true);
        TextView tix = text("TIX 100", 14, true);
        robux.setGravity(Gravity.CENTER);
        tix.setGravity(Gravity.CENTER);
        money.addView(robux, new LinearLayout.LayoutParams(0, dp(36), 1));
        money.addView(tix, new LinearLayout.LayoutParams(0, dp(36), 1));
        side.addView(money);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextSize(14);
        search.setTextColor(Color.WHITE);
        search.setHintTextColor(Color.rgb(205, 224, 244));
        search.setHint("Search");
        search.setPadding(dp(8), 0, dp(8), 0);
        search.setBackground(panel(Color.rgb(9, 52, 105), EDGE));
        LinearLayout.LayoutParams searchParams = new LinearLayout.LayoutParams(-1, dp(40));
        searchParams.topMargin = dp(9);
        side.addView(search, searchParams);

        String[] links = {"Home", "Profile", "Messages", "Friends", "Groups"};
        for (String link : links) {
            Button nav = button(link);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(38));
            lp.topMargin = dp(5);
            side.addView(nav, lp);
            nav.setOnClickListener(v -> go(link));
        }
        TextView status = text("2014 ACCOUNT", 10, true);
        status.setGravity(Gravity.CENTER);
        side.addView(status, new LinearLayout.LayoutParams(-1, dp(26)));
    }

    private void buildTopNavigation(LinearLayout parent) {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(5), dp(3), dp(5), dp(3));
        bar.setBackground(panel(Color.rgb(14, 69, 133), EDGE));
        parent.addView(bar, new LinearLayout.LayoutParams(-1, dp(48)));

        String[] links = {"Home", "Games", "Catalog", "Develop", "Groups"};
        for (String link : links) {
            Button b = button(link);
            b.setTextSize(12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(38), 1);
            lp.setMargins(dp(2), 0, dp(2), 0);
            bar.addView(b, lp);
            b.setOnClickListener(v -> go(link));
        }
    }

    private void heading(LinearLayout parent, String value) {
        TextView h = text(value.toUpperCase(), 19, true);
        h.setPadding(dp(8), 0, 0, 0);
        parent.addView(h, new LinearLayout.LayoutParams(-1, dp(38)));
    }

    private void section(LinearLayout parent, String title, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(10), dp(7), dp(10), dp(7));
        card.setBackground(panel(PANEL, EDGE));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.setMargins(dp(4), dp(4), dp(4), dp(4));
        parent.addView(card, cp);
        TextView t = text(title, 16, true);
        card.addView(t, new LinearLayout.LayoutParams(-1, dp(28)));
        TextView d = text(detail, 12, false);
        card.addView(d, new LinearLayout.LayoutParams(-1, dp(34)));
    }

    private LinearLayout tile(String name, boolean large, String subtitle) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(5), dp(5), dp(5), dp(5));
        tile.setBackground(panel(large ? Color.rgb(26, 96, 164) : PANEL, EDGE));

        // Artwork slot reserved for the verified original 2014 APK image mapping.
        FrameLayout artwork = new FrameLayout(this);
        artwork.setBackgroundColor(Color.TRANSPARENT);
        tile.addView(artwork, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView caption = text(name, large ? 17 : 12, true);
        caption.setGravity(Gravity.CENTER);
        tile.addView(caption, new LinearLayout.LayoutParams(-1, dp(28)));
        if (subtitle != null) {
            TextView sub = text(subtitle, 10, false);
            sub.setGravity(Gravity.CENTER);
            tile.addView(sub, new LinearLayout.LayoutParams(-1, dp(20)));
        }
        return tile;
    }

    private void buildHome(LinearLayout body) {
        heading(body, "Games");
        GridLayout main = new GridLayout(this);
        main.setColumnCount(2);
        main.setRowCount(2);
        body.addView(main, new LinearLayout.LayoutParams(-1, 0, 1));
        String[] names = {"Games", "Catalog", "Inventory", "Builders Club"};
        String[] subtitles = {"Play games", "Browse items", "Your items", "Membership"};
        for (int i = 0; i < names.length; i++) {
            final String target = names[i];
            LinearLayout item = tile(target, true, subtitles[i]);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = 0;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(4), dp(4), dp(4), dp(4));
            main.addView(item, lp);
            item.setOnClickListener(v -> go(target));
        }

        heading(body, "Community");
        GridLayout community = new GridLayout(this);
        community.setColumnCount(4);
        body.addView(community, new LinearLayout.LayoutParams(-1, dp(100)));
        String[] communityNames = {"Profile", "Messages", "Groups", "Leaderboards"};
        for (String name : communityNames) {
            LinearLayout item = tile(name, false, null);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = dp(92);
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(dp(3), dp(3), dp(3), dp(3));
            community.addView(item, lp);
            item.setOnClickListener(v -> go(name));
        }
    }

    private void buildPage(LinearLayout body) {
        heading(body, page);
        if ("Games".equals(page)) {
            section(body, "Featured Games", "Browse the classic games directory.");
            section(body, "Popular", "Popular games from the 2014-era layout.");
            section(body, "Recently Played", "Your recently played games will appear here.");
            section(body, "Favorites", "Your favorite games will appear here.");
        } else if ("Catalog".equals(page)) {
            addTabs(body, new String[]{"All Items", "Hats", "Gear", "Faces", "Shirts", "Pants"});
            section(body, "Catalog", "Browse avatar items and classic catalog categories.");
            section(body, "Recently Updated", "Catalog items will be loaded when the item service is connected.");
        } else if ("Inventory".equals(page)) {
            addTabs(body, new String[]{"All", "Hats", "Gear", "Faces", "Shirts", "Pants"});
            section(body, "My Inventory", "Items owned by 1234 will appear here.");
        } else if ("Builders Club".equals(page)) {
            section(body, "Builders Club", "Classic membership information and benefits.");
            section(body, "Membership Status", "No membership is connected to this local dashboard yet.");
        } else if ("Profile".equals(page)) {
            section(body, "1234", "Profile");
            section(body, "About Me", "This is the profile page for account 1234.");
            section(body, "Currently Wearing", "Avatar items will appear here.");
            section(body, "Friends", "Your friends list will appear here.");
        } else if ("Messages".equals(page)) {
            addTabs(body, new String[]{"Inbox", "Sent", "Archived"});
            section(body, "Messages", "Your messages will appear here after account services are connected.");
        } else if ("Groups".equals(page)) {
            section(body, "My Groups", "Groups joined by account 1234 will appear here.");
            section(body, "Find Groups", "Browse the classic groups directory.");
        } else if ("Leaderboards".equals(page)) {
            section(body, "Leaderboards", "Classic player rankings and score lists.");
        } else if ("Develop".equals(page)) {
            section(body, "Create", "Your places and development tools.");
            section(body, "My Creations", "Created games and assets will appear here.");
        } else if ("Friends".equals(page)) {
            section(body, "Friends", "Your friends list and friend requests will appear here.");
        } else {
            section(body, page, "This dashboard section is ready for its original content and service connection.");
        }
        Button home = button("‹  Back to Home");
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(dp(170), dp(42));
        hp.setMargins(dp(4), dp(8), 0, dp(8));
        body.addView(home, hp);
        home.setOnClickListener(v -> go("Home"));
    }

    private void addTabs(LinearLayout parent, String[] labels) {
        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        parent.addView(tabs, new LinearLayout.LayoutParams(-1, dp(42)));
        for (String label : labels) {
            Button b = button(label);
            b.setTextSize(10);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(36), 1);
            lp.setMargins(dp(2), 0, dp(2), 0);
            tabs.addView(b, lp);
        }
    }

    @Override public void onBackPressed() {
        if (!"Home".equals(page)) go("Home");
        else super.onBackPressed();
    }
}
