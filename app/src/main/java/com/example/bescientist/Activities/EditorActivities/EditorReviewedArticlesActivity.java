package com.example.bescientist.Activities.EditorActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.Activities.ReviewerActivities.ArticleVerifiedActivity;
import com.example.bescientist.Adapters.ArticlesVerifiedAdapter;
import com.example.bescientist.Classes.ArticleVerified;
import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EditorReviewedArticlesActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    ArticlesVerifiedAdapter articlesVerifiedAdapter;
    static List<ArticleVerified> list;

    SwipeRefreshLayout refreshReviewedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_reviewed_articles);

        list = new ArrayList<ArticleVerified>();

        //Refreshing data
        refreshReviewedArticles = (SwipeRefreshLayout) findViewById(R.id.refreshReviewed);
        refreshReviewedArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviewed_articles_id);
        articlesVerifiedAdapter = new ArticlesVerifiedAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(articlesVerifiedAdapter);

        getData();
    }

    //My Methods
    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshReviewedArticles.setRefreshing(true);

        list = new ArrayList<ArticleVerified>();

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        String url = "https://be-scientist.000webhostapp.com/api/user/data.php?editor="+ id;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Failure Error");
                        refreshReviewedArticles.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if(response.isSuccessful()) {

                    final String myResponse = Objects.requireNonNull(response.body()).string();

                    try {
                        final JSONObject jsonResponse = new JSONObject(myResponse);

                        if(jsonResponse.has("Data")) {

                            final JSONObject dataGlobal = jsonResponse.getJSONObject("Data");

                            if(dataGlobal.has("reviewed")) {

                                final JSONArray verified = dataGlobal.getJSONArray("reviewed");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(verified);
                                        showToast("Articles reviewed updated");
                                        refreshReviewedArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Articles reviewed not found");
                                        refreshReviewedArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshReviewedArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshReviewedArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray verified) {

        for (int i=0; i<verified.length(); i++) {

            ArticleVerified article = new ArticleVerified();

            try {

                article.setId(verified.getJSONObject(i).getInt("id"));
                article.setAuthor_id(verified.getJSONObject(i).getInt("author_id"));
                article.setAuthor_name(verified.getJSONObject(i).getString("author_name"));

                article.setReviewer_id(verified.getJSONObject(i).getInt("reviewer_id"));
                article.setReviewer_name(verified.getJSONObject(i).getString("reviewer_name"));

                article.setTitle(verified.getJSONObject(i).getString("title"));
                article.setContent(verified.getJSONObject(i).getString("content"));
                article.setDomain(verified.getJSONObject(i).getString("domain"));

                article.setObservation(verified.getJSONObject(i).getString("observation"));
                article.setCreated_at(verified.getJSONObject(i).getString("created_at"));
                article.setReviewed_at(verified.getJSONObject(i).getString("reviewed_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        ArticlesVerifiedAdapter articlesVerifiedAdapter = new ArticlesVerifiedAdapter(this, list);
        myRecyclerView.setAdapter(articlesVerifiedAdapter);
    }

    //Get Article By id
    public static ArticleVerified getArticleById(int id) {
        for (int i=0; i<list.size(); i++) {
            if(id == list.get(i).getId()) {
                return list.get(i);
            }
        }
        return new ArticleVerified();
    }

    //Click sur l'article
    public void myArticleClick(View v) {

        TextView et_id = (TextView) v.findViewById(R.id.article_id_id);

        Intent intent = new Intent(this, ArticleFinalDecisionActivity.class);
        intent.putExtra("id", et_id.getText().toString());

        startActivity(intent);
    }

    //Back arrow click
    public void backArrowClick(View v) {
        finish();
    }

    //Show toast
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
