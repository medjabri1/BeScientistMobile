package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class NewArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article);
    }

    //My Methods
    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    //Add new Article Click button
    public void addNewArticleClick(View v) {
        EditText et_title = findViewById(R.id.new_article_title_id);
        EditText et_domain = findViewById(R.id.new_article_domain_id);
        EditText et_content = findViewById(R.id.new_article_content_id);

        final int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);
        final String title = et_title.getText().toString();
        final String domain = et_domain.getText().toString();
        final String content = et_content.getText().toString();

        if(title.equals("") || content.equals("") || domain.equals("")) {
            showToast("Remplissez tout les champs s'il vous plait!");
            return;
        }

        if(title.length() < 10 || content.length() < 20 || domain.length() < 4) {
            showToast("Un ou plusieurs champ est très court");
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

                addNewArticle(id, title, domain, content);
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

    //Submit Article to api
    public void addNewArticle(int id, String title, String domain, String content) {

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
        String url = "https://be-scientist.000webhostapp.com/api/author/addArticle.php";

        RequestBody formBody = new FormBody.Builder()
                .add("author_id", String.valueOf(id))
                .add("title", title)
                .add("content", content)
                .add("domain", domain)
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
                            new Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            loadingDialog.dismiss();
                                            finish();
                                        }
                                    },
                                    1000);
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

    //reset Article Click button
    public void resetNewArticleClick(View v) {
        EditText et_title = findViewById(R.id.new_article_title_id);
        EditText et_domain = findViewById(R.id.new_article_domain_id);
        EditText et_content = findViewById(R.id.new_article_content_id);

        et_title.setText("");
        et_domain.setText("");
        et_content.setText("");

        Toast.makeText(this, "Valeurs initialisés", Toast.LENGTH_SHORT).show();
    }

    //Make toast message
    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
