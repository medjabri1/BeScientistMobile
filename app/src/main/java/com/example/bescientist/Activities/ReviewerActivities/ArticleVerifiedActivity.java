package com.example.bescientist.Activities.ReviewerActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;
import com.example.bescientist.Classes.ArticleVerified;

import java.util.Objects;

public class ArticleVerifiedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_verified);

        //Charge article in view
        chargeArticle();
    }

    //My Methods

    //Charge Article
    @SuppressLint("SetTextI18n")
    public void chargeArticle() {

        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        TextView tv_title = (TextView) findViewById(R.id.article_reviewed_title_id);
        TextView tv_domain = (TextView) findViewById(R.id.article_reviewed_domain_id);
        TextView tv_content = (TextView) findViewById(R.id.article_reviewed_content_id);
        TextView tv_observation = (TextView) findViewById(R.id.article_reviewed_observation_id);
        TextView tb_reviewed_at = (TextView) findViewById(R.id.article_reviewed_reviewed_at_id);

        ArticleVerified article = ArticlesVerifiedActivity.getArticleById(id);

        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());
        tv_observation.setText(article.getObservation());
        tb_reviewed_at.setText("Vérifié le : "+ article.getReviewed_at().substring(0, article.getReviewed_at().length() - 3));
    }

    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    //Show toast
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
