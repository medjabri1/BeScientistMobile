package com.example.bescientist.Activities.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Play starting animation
        playAnimation();

        //Get Data from SharedPreferences
        updateData();

    }

    //My METHODS

    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    //Starting Animation
    public void playAnimation() {
        LinearLayout account_layout = findViewById(R.id.account_setting_layout);
        LinearLayout general_layout = findViewById(R.id.general_setting_layout);

        account_layout.setAlpha(0);
        general_layout.setAlpha(0);

        account_layout.setTranslationY(200);
        general_layout.setTranslationY(200);

        account_layout.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(200).start();
        general_layout.animate().alpha(1).translationY(0).setDuration(500).setStartDelay(500).start();
    }

    //Update Data from SharedPreferences
    public void updateData() {
        EditText et_name_settings = findViewById(R.id.settings_name_id);
        EditText et_email_settings = findViewById(R.id.settings_email_id);

        et_name_settings.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", "MJR Dev"));
        et_email_settings.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", "MJR@dev.com"));
    }

    //Show toast
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //Item click
    public void itemClicked(View view) {

        String tag = view.getTag().toString();
        LinearLayout layout;

        switch (tag) {
            case "data": layout = (LinearLayout) findViewById(R.id.dataLayout); break;
            case "password": layout = (LinearLayout) findViewById(R.id.passwordLayout); break;

            default: return;
        }

        if(layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }

    }
}
