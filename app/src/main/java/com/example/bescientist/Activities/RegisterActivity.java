package com.example.bescientist.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    EditText et_name, et_email, et_password;
    Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_name = (EditText) findViewById(R.id.register_et_name);
        et_email = (EditText) findViewById(R.id.register_et_email);
        et_password = (EditText) findViewById(R.id.register_et_password);
        btn_register = (Button) findViewById(R.id.register_btn_register);

        final Context myContext = this;

        btn_register.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {

                final String name = et_name.getText().toString();
                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();

                if(name.length() < 1 || email.length() < 1 || password.length() < 1) {
                    showToast("Remplissez tout les champs");
                    return;
                }

                if(name.trim().length() < 4 || email.trim().length() < 5 || password.trim().length() < 5) {
                    showToast("Un champs ou plusieurs est trop court");
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                    showToast("Adresse email entrée non valide");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(myContext);
                @SuppressLint("InflateParams") View view = LayoutInflater.from(myContext).inflate(R.layout.dialog_confirmation, null);
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();

                ((TextView) view.findViewById(R.id.dialog_confirmation_title_id)).setText("Confirmer");
                ((TextView) view.findViewById(R.id.dialog_confirmation_content_id)).setVisibility(View.GONE);

                ((Button) view.findViewById(R.id.dialog_confirmation_confirm_id)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitSignUp(name, email, password);
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
        });
    }

    public void submitSignUp(String name, String email, String password) {

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
        String url = "https://be-scientist.000webhostapp.com/api/user/register.php";

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
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
                            if(myResponse.equalsIgnoreCase("registered")) {
                                //Logged in
                                showToast(myResponse);
                                gotoConnectNow();
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

    private void gotoConnectNow() {

        final Intent intent = new Intent(this, LoginActivity.class);

        new Handler().postDelayed(
            new Runnable() {
                public void run() {
                    showToast("Connect now");
                    startActivity(intent);
                    finish();
                }
            },
        1000);
    }

    public void finishView(View v) {
        finish();
    }

    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
