package com.acr.landmarks.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.MenuItem;
import android.widget.Switch;

import com.acr.landmarks.R;

public class SettingsActivity extends AppCompatActivity {

    Switch darkTheme;
    boolean darkThemeActivated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setAnimation();
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        darkThemeActivated = preferences.getBoolean("darkTheme", false);

        darkTheme = findViewById(R.id.theme_switch);
        darkTheme.setChecked(darkThemeActivated);
        darkTheme.setOnClickListener(v -> {
            darkThemeActivated = !darkThemeActivated;
            String mapStyle = darkThemeActivated ? "map_style_night" : "map_style_light";
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putBoolean("darkTheme", darkThemeActivated);
            preferencesEditor.putString("mapStyle", mapStyle);
            preferencesEditor.commit();


            Intent newIntent = new Intent(SettingsActivity.this, SettingsActivity.class);

            finish();
            overridePendingTransition(0, 0);
            startActivity(newIntent);
            overridePendingTransition(0, 0);

        });
    }

    private void setAnimation() {
        Fade fade = new Fade();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void loadTheme() {
        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        darkThemeActivated = preferences.getBoolean("darkTheme", false);
        if (darkThemeActivated) {
            setTheme(R.style.NightTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }
}
