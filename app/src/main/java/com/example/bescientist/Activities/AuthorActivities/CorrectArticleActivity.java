package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;
import com.example.bescientist.Classes.Article;

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

public class CorrectArticleActivity extends AppCompatActivity {

    ImageView btnCorrectArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_article);

        btnCorrectArticle = (ImageView) findViewById(R.id.btn_correct_article_id);

        //Charger l'article à corriger
        chargeArticle(btnCorrectArticle);
    }

    //My Methods
    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    public void chargeArticle(View v) {

        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        EditText tv_title = (EditText) findViewById(R.id.correct_article_title_id);
        EditText tv_domain = (EditText) findViewById(R.id.correct_article_domain_id);
        EditText tv_content = (EditText) findViewById(R.id.correct_article_content_id);

        Article article = ArticlesToCorrectActivity.getArticleById(id);

        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());

    }

    //Correct btn click
    @SuppressLint("SetTextI18n")
    public void correctArticleClick(View v) {
        EditText et_title = findViewById(R.id.correct_article_title_id);
        EditText et_content = findViewById(R.id.correct_article_content_id);

        final String id = getIntent().getStringExtra("id");
        final String title = et_title.getText().toString();
        final String content = et_content.getText().toString();

        if(title.equals("") || content.equals("")) {
            showToast("Remplissez tout les champs s'il vous plait!");
            return;
        }

        if(title.length() < 10 || content.length() < 20) {
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

                correctArticle(id, title, content);
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
    public void correctArticle(String id, String title, String content) {

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
        String url = "https://be-scientist.000webhostapp.com/api/author/correctArticle.php";

        RequestBody formBody = new FormBody.Builder()
                .add("article_id", id)
                .add("title", title)
                .add("content", content)
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

    //Make toast message
    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
