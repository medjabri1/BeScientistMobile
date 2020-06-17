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

import com.example.bescientist.Adapters.NewArticlesAdapter;
import com.example.bescientist.Adapters.SentArticlesAdapter;
import com.example.bescientist.Classes.Article;
import com.example.bescientist.Classes.ArticleToVerify;
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

public class SentArticlesActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    SentArticlesAdapter sentArticlesAdapter;
    static List<ArticleToVerify> list;

    SwipeRefreshLayout refreshSentArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_articles);

        list = new ArrayList<ArticleToVerify>();

        //Refreshing data
        refreshSentArticles = (SwipeRefreshLayout) findViewById(R.id.refreshSentArticles);
        refreshSentArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_sent_articles_id);
        sentArticlesAdapter = new SentArticlesAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(sentArticlesAdapter);

        getData();
    }

    //My Methods

    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshSentArticles.setRefreshing(true);

        list = new ArrayList<ArticleToVerify>();

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
                        refreshSentArticles.setRefreshing(false);
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

                            if(dataGlobal.has("sent")) {

                                final JSONArray sent = dataGlobal.getJSONArray("sent");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(sent);
                                        showToast("Sent Articles updated");
                                        refreshSentArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Sent articles not found");
                                        refreshSentArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshSentArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshSentArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray sent) {

        for (int i=0; i<sent.length(); i++) {

            ArticleToVerify article = new ArticleToVerify();

            try {

                article.setId(sent.getJSONObject(i).getInt("id"));
                article.setAuthor_id(sent.getJSONObject(i).getInt("author_id"));
                article.setAuthor_name(sent.getJSONObject(i).getString("author_name"));

                article.setReviewer_id(sent.getJSONObject(i).getInt("reviewer_id"));
                article.setReviewer_name(sent.getJSONObject(i).getString("reviewer_name"));
                article.setSent_at(sent.getJSONObject(i).getString("sent_at"));

                article.setTitle(sent.getJSONObject(i).getString("title"));
                article.setContent(sent.getJSONObject(i).getString("content"));
                article.setDomain(sent.getJSONObject(i).getString("domain"));
                article.setCreated_at(sent.getJSONObject(i).getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        SentArticlesAdapter sentArticlesAdapter = new SentArticlesAdapter(this, list);
        myRecyclerView.setAdapter(sentArticlesAdapter);
    }

    //Get Article By id
    public static Article getArticleById(int id) {
        for (int i=0; i<list.size(); i++) {
            if(id == list.get(i).getId()) {
                return list.get(i);
            }
        }
        return new Article();
    }

    //Click sur l'article
    public void myArticleClick(View v) {

        TextView et_id = (TextView) v.findViewById(R.id.article_to_verify_id_id);

        //Intent intent = new Intent(this, NewArticleActivity.class);
        //intent.putExtra("id", et_id.getText().toString());
        //startActivity(intent);

        showToast(et_id.getText().toString());

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
