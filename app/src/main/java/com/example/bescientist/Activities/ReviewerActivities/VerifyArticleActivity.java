package com.example.bescientist.Activities.ReviewerActivities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;
import com.example.bescientist.Classes.ArticleToVerify;

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

public class VerifyArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_article);

        //Charger l'article à corriger
        chargeArticle();
    }

    //My Methods
    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    public void chargeArticle() {

        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        TextView tv_title = (TextView) findViewById(R.id.verify_article_title_id);
        TextView tv_domain = (TextView) findViewById(R.id.verify_article_domain_id);
        TextView tv_content = (TextView) findViewById(R.id.verify_article_content_id);
        TextView tv_sent_at = (TextView) findViewById(R.id.verify_article_sent_at_id);

        ArticleToVerify article = ArticlesToVerifyActivity.getArticleById(id);

        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());
        tv_sent_at.setText("Envoyé le : "+ article.getSent_at().substring(0, article.getSent_at().length() - 3));

    }

    //send observation btn click
    @SuppressLint("SetTextI18n")
    public void sendObservationClick(View v) {
        EditText et_observation = findViewById(R.id.verify_article_observation_id);

        final String id = getIntent().getStringExtra("id");
        final String reviewer_id = String.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0));
        final String observation = et_observation.getText().toString();

        if(observation.equals("")) {
            showToast("Remplissez l'observation s'il vous plait!");
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

                sendObservation(id, reviewer_id, observation);
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

    //Submit Observation to api
    public void sendObservation(String id, String reviewer_id, String observation) {

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
        String url = "https://be-scientist.000webhostapp.com/api/reviewer/reviewArticle.php";

        RequestBody formBody = new FormBody.Builder()
                .add("article_id", id)
                .add("reviewer_id", reviewer_id)
                .add("observation", observation)
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
