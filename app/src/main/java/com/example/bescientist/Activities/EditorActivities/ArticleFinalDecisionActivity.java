package com.example.bescientist.Activities.EditorActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.Activities.AuthorActivities.ArticlesToCorrectActivity;
import com.example.bescientist.Adapters.ArticlesVerifiedAdapter;
import com.example.bescientist.Classes.Article;
import com.example.bescientist.Classes.ArticleVerified;
import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.PreferenceChangeEvent;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ArticleFinalDecisionActivity extends AppCompatActivity {

    private ArticleVerified myArticleVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_final_decision);

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

        TextView tv_title = (TextView) findViewById(R.id.article_verified_title_id);
        TextView tv_domain = (TextView) findViewById(R.id.article_verified_domain_id);
        TextView tv_content = (TextView) findViewById(R.id.article_verified_content_id);

        TextView tv_author_name = (TextView) findViewById(R.id.article_verified_author_name_id);
        TextView tv_reviewer_name = (TextView) findViewById(R.id.article_verified_reviewer_name_id);
        TextView tv_observation = (TextView) findViewById(R.id.article_verified_observation_id);

        TextView tv_created_at = (TextView) findViewById(R.id.article_verified_created_at_id);
        TextView tv_sent_at = (TextView) findViewById(R.id.article_verified_reviewed_at_id);

        ArticleVerified article = EditorReviewedArticlesActivity.getArticleById(id);

        myArticleVerified = article;
        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());
        tv_author_name.setText("Auteur : "+ article.getAuthor_name());
        tv_reviewer_name.setText("Réviseur : "+ article.getReviewer_name());
        tv_observation.setText(article.getObservation());
        tv_created_at.setText("Crée le : "+ article.getCreated_at().substring(0, article.getCreated_at().length() - 3));
        tv_sent_at.setText("Vérifié le : "+ article.getReviewed_at().substring(0, article.getReviewed_at().length() - 3));

    }

    //Confirm Decision btn click
    @SuppressLint("SetTextI18n")
    public void confirmDecisionClick(View v) {

        RadioGroup radioGroup = findViewById(R.id.radio_button_decision_id);
        int decision_id = radioGroup.getCheckedRadioButtonId();
        final RadioButton radioButton = findViewById(decision_id);
        if(radioButton == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(view);

        ((TextView) view.findViewById(R.id.dialog_confirmation_content_id)).setText("Décision : "+ radioButton.getText().toString());
        ((TextView) view.findViewById(R.id.dialog_confirmation_confirm_id)).setText("Confirmer");

        final AlertDialog alertDialog = builder.create();
        final Context myContext = this;
        final Button cancelBtn = view.findViewById(R.id.dialog_confirmation_close_id);

        ((Button) view.findViewById(R.id.dialog_confirmation_confirm_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button confirmBtn = (Button) v;

                int article_id = myArticleVerified.getId();
                int reviewer_id = myArticleVerified.getReviewer_id();
                int editor_id = PreferenceManager.getDefaultSharedPreferences(myContext).getInt("user_id", 0);
                String decision = radioButton.getTag().toString();

                confirmDecision(article_id, reviewer_id, editor_id, decision, confirmBtn, cancelBtn, alertDialog);

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

    @SuppressLint("SetTextI18n")
    private void confirmDecision(int article_id, int reviewer_id, int editor_id, String decision, Button confirmBtn, Button cancelBtn, final AlertDialog alertDialog) {

        cancelBtn.setClickable(false);
        cancelBtn.setTextColor(0x44777777);

        confirmBtn.setText("En cours..");
        confirmBtn.setClickable(false);
        confirmBtn.setTextColor(0x44777777);

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();
        String url = "https://be-scientist.000webhostapp.com/api/editor/articleDecision.php";

        RequestBody formBody = new FormBody.Builder()
                .add("article_id", String.valueOf(article_id))
                .add("reviewer_id", String.valueOf(reviewer_id))
                .add("editor_id", String.valueOf(editor_id))
                .add("decision", decision)
                .build();

        Request request = new Request.Builder().url(url).post(formBody).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                    showToast("Connection error");
                    new Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    alertDialog.dismiss();
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
                        new Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    alertDialog.dismiss();
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
                            new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();
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
