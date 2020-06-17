package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewArticleActivity extends AppCompatActivity {

    ImageView btnAddNewArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_article);

        btnAddNewArticle = (ImageView) findViewById(R.id.btn_add_new_article_id);
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

        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);
        String title = et_title.getText().toString();
        String domain = et_domain.getText().toString();
        String content = et_content.getText().toString();

        if(title.equals("") || content.equals("") || domain.equals("")) {
            showToast("Remplissez tout les champs s'il vous plait!");
            return;
        }

        if(title.length() < 10 || content.length() < 20 || domain.length() < 4) {
            showToast("Un ou plusieurs champ est très court");
            return;
        }

        btnAddNewArticle.setClickable(false);
        addNewArticle(id, title, domain, content);
    }

    //Submit Article to api
    public void addNewArticle(int id, String title, String domain, String content) {

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
                        btnAddNewArticle.setClickable(true);
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
                            btnAddNewArticle.setClickable(true);
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
                            btnAddNewArticle.setClickable(true);
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
