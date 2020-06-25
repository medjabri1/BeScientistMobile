package com.example.bescientist.Activities.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    //Update preferences data
    public void updatePreferences(String name, String email) {

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("name", name).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("email", email).apply();

    }

    //Update Data from SharedPreferences
    public void updateData() {
        EditText et_name_settings = findViewById(R.id.settings_name_id);
        EditText et_email_settings = findViewById(R.id.settings_email_id);

        et_name_settings.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", "MJR Dev"));
        et_email_settings.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", "MJR@dev.com"));
    }

    //UpdateData Click button
    @SuppressLint("SetTextI18n")
    public void updateDataClick(View v) {

        String old_name = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "MJR Dev");
        String old_email = PreferenceManager.getDefaultSharedPreferences(this).getString("email", "MJR@dev.com");

        final String name = ((TextView) findViewById(R.id.settings_name_id)).getText().toString();
        final String email = ((TextView) findViewById(R.id.settings_email_id)).getText().toString();

        if(old_name.equals(name) && old_email.equals(email)) {
            //Nothing changed
            showToast("Aucun changement détecté");
            return;
        }

        if(name.length() < 4) {
            //Name too short
            showToast("Nom trop court");
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            //Invalid email
            showToast("Adresse email entrée non valide");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        final Context myContext = this;

        ((TextView) view.findViewById(R.id.dialog_confirmation_title_id)).setText("Confirmer");
        ((TextView) view.findViewById(R.id.dialog_confirmation_content_id)).setVisibility(View.GONE);

        ((Button) view.findViewById(R.id.dialog_confirmation_confirm_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUpdateData(name, email);
                alertDialog.dismiss();
            }
        });

        ((Button) view.findViewById(R.id.dialog_confirmation_close_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //UpdatePassword Click button
    @SuppressLint("SetTextI18n")
    public void updatePasswordClick(View v) {

        final String password_old = ((TextView) findViewById(R.id.settings_old_password_id)).getText().toString();
        final String password_new = ((TextView) findViewById(R.id.settings_new_password_id)).getText().toString();
        final String password_confirm = ((TextView) findViewById(R.id.settings_confirm_password_id)).getText().toString();

        if(password_confirm.length() < 8 || password_old.length() < 8 || password_new.length() < 8) {
            //password too short
            showToast("Mot de passe trop court");
            return;
        }

        if(password_old.equals(password_new)) {
            //Old is the same as new password
            showToast("Aucun changement détecté");
            return;
        }

        if(!password_confirm.equals(password_new)) {
            //Confirmation doesn't match
            showToast("Confirmation est incorrecte");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(view);

        final AlertDialog alertDialog = builder.create();
        final Context myContext = this;

        ((TextView) view.findViewById(R.id.dialog_confirmation_title_id)).setText("Confirmer");
        ((TextView) view.findViewById(R.id.dialog_confirmation_content_id)).setVisibility(View.GONE);

        ((Button) view.findViewById(R.id.dialog_confirmation_confirm_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitUpdatePassword(password_old, password_new, password_confirm);
                alertDialog.dismiss();

            }
        });

        ((Button) view.findViewById(R.id.dialog_confirmation_close_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //Submit update data to api
    public void submitUpdateData(final String name, final String email) {

        int user_id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog loadingDialog = builder.create();
        loadingDialog.setCancelable(false);
        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        loadingDialog.show();

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();
        String url = "https://be-scientist.000webhostapp.com/api/user/updateData.php";

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("name", name)
                .add("email", email)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Connection error");
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(myResponse);
                            loadingDialog.dismiss();
                            if(myResponse.contains("Success")) {
                                updatePreferences(name, email);
                            }
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Server Error");
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        });

    }

    //Submit update password to api
    public void submitUpdatePassword(final String password_old, final String password_new, String password_confirm) {

        int user_id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog loadingDialog = builder.create();
        loadingDialog.setCancelable(false);
        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        loadingDialog.show();

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();
        String url = "https://be-scientist.000webhostapp.com/api/user/updatePassword.php";

        RequestBody formBody = new FormBody.Builder()
                .add("user_id", String.valueOf(user_id))
                .add("password_old", password_old)
                .add("password_new", password_new)
                .add("password_confirm", password_confirm)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Connection error");
                        loadingDialog.dismiss();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {

                    final String myResponse = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(myResponse);
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast("Server Error");
                            loadingDialog.dismiss();
                        }
                    });
                }
            }
        });

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
            case "about": layout = (LinearLayout) findViewById(R.id.aboutLayout); break;

            default: return;
        }

        if(layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }

    }
}
