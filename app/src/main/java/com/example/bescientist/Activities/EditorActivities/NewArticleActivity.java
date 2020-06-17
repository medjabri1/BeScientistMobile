package com.example.bescientist.Activities.EditorActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.Classes.Article;
import com.example.bescientist.R;

import java.util.Objects;

public class NewArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_new_article);

        //Charger l'article
        chargeArticle();
    }

    //My Methods

    //Charge Article
    @SuppressLint("SetTextI18n")
    public void chargeArticle() {

        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));

        TextView tv_title = (TextView) findViewById(R.id.editor_new_article_title_id);
        TextView tv_author_name = (TextView) findViewById(R.id.editor_new_article_author_name_id);
        TextView tv_domain = (TextView) findViewById(R.id.editor_new_article_domain_id);
        TextView tv_content = (TextView) findViewById(R.id.editor_new_article_content_id);
        TextView tv_created_at = (TextView) findViewById(R.id.editor_new_article_created_at_id);

        Article article = NewArticlesActivity.getArticleById(id);

        if(article.getTitle() == null) return;

        tv_title.setText(article.getTitle());
        tv_author_name.setText("Crée par : "+ article.getAuthor_name());
        tv_domain.setText(article.getDomain());
        tv_content.setText(article.getContent());
        tv_created_at.setText(article.getCreated_at().substring(0, article.getCreated_at().length() - 3));
    }

    public void sendToReviewer(View v) {
        int id = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("id")));
        Article article = NewArticlesActivity.getArticleById(id);

        Intent intent = new Intent(this, ChooseReviewerActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("title", article.getTitle());
        intent.putExtra("author", article.getAuthor_name());
        intent.putExtra("author_id", article.getAuthor_id());
        startActivity(intent);
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
