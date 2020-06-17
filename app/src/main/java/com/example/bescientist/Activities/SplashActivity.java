package com.example.bescientist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.TimeInterpolator;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bescientist.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Animation
        TextView tv_app_name = (TextView) findViewById(R.id.splash_app_name_id);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.fakeLoadingProgress);

        tv_app_name.setAlpha(0f);
        tv_app_name.setTranslationY(-140);
        tv_app_name.animate().alpha(1f).setDuration(1000).start();
        tv_app_name.animate().translationY(0).setDuration(600).setStartDelay(400).start();

        progressBar.setAlpha(0f);
        progressBar.setTranslationY(80);
        progressBar.animate().alpha(1f).setDuration(600).setStartDelay(400).start();
        progressBar.animate().translationY(0).setDuration(300).setStartDelay(700).start();

        boolean is_logged_in = PreferenceManager.getDefaultSharedPreferences(this).contains("user_id");
        final Intent intent;

        if(is_logged_in) {
            //User already logged in
            String type = PreferenceManager.getDefaultSharedPreferences(this).getString("type", "A");
            if(type.equalsIgnoreCase("e")) {
                //User is an admin
                intent = new Intent(this, AdminActivity.class);
            } else {
                //User is not an admin
                intent = new Intent(this, HomeActivity.class);
            }
        } else {
            //user not logged in
            intent = new Intent(this, LoginActivity.class);
        }

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            },
        3000);
    }
}
