package com.example.bescientist.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText et_email, et_password;
    Button btn_login;
    TextView btn_goto_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = (EditText) findViewById(R.id.login_et_email);
        et_password = (EditText) findViewById(R.id.login_et_password);
        btn_login = (Button) findViewById(R.id.login_btn_login);
        btn_goto_register = (TextView) findViewById(R.id.login_btn_register);

        final Intent registerIntent = new Intent(this, RegisterActivity.class);

        btn_goto_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            String email = et_email.getText().toString().trim();
            String password = et_password.getText().toString();

            if(email.length() < 1 || password.length() < 1) {
                showToast("Remplissez tout les champs");
                return;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Adresse email entrée non valide");
                return;
            }

            submitLogin(email, password);

            }
        });
    }

    public void submitLogin(String email, String password) {

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
        String url = "https://be-scientist.000webhostapp.com/api/user/login.php";

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
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
                            if(myResponse.equalsIgnoreCase("logged in")) {
                                //Logged in
                                showToast(myResponse);
                                insertUserData(et_email.getText().toString());
                            } else {
                                //Not logged in
                                showToast(myResponse);
                                loadingDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void insertUserData(String email) {

        final Handler mHandler = new Handler(Looper.getMainLooper());

        String url = "https://be-scientist.000webhostapp.com/api/user/user.php?email=" + email;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                showToast("Server error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String myResponse = response.body().string();

                if(response.isSuccessful()) {

                    try {
                        JSONObject jsonResponse = new JSONObject(myResponse);

                        if(jsonResponse.has("Data")) {
                            JSONObject data = jsonResponse.getJSONObject("Data");
                            final int id = data.getInt("id");
                            final String name = data.getString("name");
                            final String email = data.getString("email");
                            final String job = data.getString("job");
                            final String type = data.getString("type");
                            final String created_at = data.getString("created_at");

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    insertSharedPreferencesAndRedirect(id, name, email, job, type, created_at);
                                }
                            });

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(myResponse);
                                }
                            });
                        }
                    }catch (JSONException err){
                        Log.d("Error", err.toString());
                    }
                }
            }
        });

        //Admin or not an admin
        //Redirection
        /*final Intent intent = new Intent(this, SplashActivity.class);

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    startActivity(intent);
                    finish();
                }
            },
        1000);*/
    }

    public void insertSharedPreferencesAndRedirect(int id, String name, String email, String job, String type, String created_at) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("user_id", id).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("name", name).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("email", email).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("job", job).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("type", type).apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("created_at", created_at).apply();

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
