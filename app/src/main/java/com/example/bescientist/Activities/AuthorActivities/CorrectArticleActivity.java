package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    public void correctArticleClick(View v) {
        EditText et_title = findViewById(R.id.correct_article_title_id);
        EditText et_content = findViewById(R.id.correct_article_content_id);

        String id = getIntent().getStringExtra("id");
        String title = et_title.getText().toString();
        String content = et_content.getText().toString();

        if(title.equals("") || content.equals("")) {
            showToast("Remplissez tout les champs s'il vous plait!");
            return;
        }

        if(title.length() < 10 || content.length() < 20) {
            showToast("Un ou plusieurs champ est très court");
            return;
        }

        btnCorrectArticle.setClickable(false);
        correctArticle(id, title, content);
    }

    //Submit Article to api
    public void correctArticle(String id, String title, String content) {

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
                        btnCorrectArticle.setClickable(true);
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        finish();
                                    }
                                },
                                1000);
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
                            btnCorrectArticle.setClickable(true);
                            new Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
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
                            btnCorrectArticle.setClickable(true);
                            new Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            finish();
                                        }
                                    },
                                    1000);
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
