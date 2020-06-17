package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.Activities.EditorActivities.AcceptedArticlesActivity;
import com.example.bescientist.R;
import com.example.bescientist.Classes.Article;

import java.util.Objects;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        //Charge article in view
        chargeArticle();

    }

    //My Methods

    //Charge Article
    @SuppressLint("SetTextI18n")
    public void chargeArticle() {

        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        TextView tv_title = (TextView) findViewById(R.id.my_article_title_id);
        TextView tv_domain = (TextView) findViewById(R.id.my_article_domain_id);
        TextView tv_content = (TextView) findViewById(R.id.my_article_content_id);
        TextView tv_status = (TextView) findViewById(R.id.my_article_status_id);
        TextView tv_created_at = (TextView) findViewById(R.id.my_article_created_at_id);

        String from = getIntent().getStringExtra("from");

        Article article;

        switch (Objects.requireNonNull(from)) {
            case "tocorrect": article = ArticlesToCorrectActivity.getArticleById(id); break;
            case "acceptedarticles": article = AcceptedArticlesActivity.getArticleById(id); break;
            case "myarticles":
            default : article = MyArticlesActivity.getArticleById(id); break;
        }

        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());
        tv_status.setText(article.getRealStatus());
        tv_created_at.setText("Crée le : "+ article.getCreated_at().substring(0, article.getCreated_at().length() - 3));
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
