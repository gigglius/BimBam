package com.gigglius.robloxvr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView view = new TextView(this);
        view.setText("Roblox 2014 VR\nAndroid project initialized.");
        view.setTextSize(24);
        view.setGravity(17);

        setContentView(view);
    }
}
