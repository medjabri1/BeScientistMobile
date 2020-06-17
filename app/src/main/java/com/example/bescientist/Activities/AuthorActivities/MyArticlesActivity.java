package com.example.bescientist.Activities.AuthorActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.R;
import com.example.bescientist.Adapters.MyArticlesAdapter;
import com.example.bescientist.Classes.Article;

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

public class MyArticlesActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    MyArticlesAdapter myArticlesAdapter;
    static List<Article> list;

    SwipeRefreshLayout refreshMyArticles;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles);

        list = new ArrayList<Article>();

        //Refreshing data
        refreshMyArticles = (SwipeRefreshLayout) findViewById(R.id.refreshMyArticles);
        refreshMyArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_my_articles_id);
        myArticlesAdapter = new MyArticlesAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(myArticlesAdapter);

        getData();

    }

    //My Methods

    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshMyArticles.setRefreshing(true);

        list = new ArrayList<Article>();

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        String url = "https://be-scientist.000webhostapp.com/api/user/data.php?user="+ id;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Failure Error");
                        refreshMyArticles.setRefreshing(false);
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

                            if(dataGlobal.has("mine")) {

                                final JSONArray mine = dataGlobal.getJSONArray("mine");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(mine);
                                        showToast("My Articles updated");
                                        refreshMyArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("My articles not found");
                                        refreshMyArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshMyArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshMyArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray mine) {

        for (int i=0; i<mine.length(); i++) {

            Article article = new Article();

            try {

                article.setId(mine.getJSONObject(i).getInt("id"));
                article.setAuthor_id(mine.getJSONObject(i).getInt("author_id"));
                article.setTitle(mine.getJSONObject(i).getString("title"));
                article.setContent(mine.getJSONObject(i).getString("content"));
                article.setDomain(mine.getJSONObject(i).getString("domain"));
                article.setStatus(mine.getJSONObject(i).getString("status"));
                article.setCreated_at(mine.getJSONObject(i).getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        MyArticlesAdapter myArticlesAdapter = new MyArticlesAdapter(this, list);
        myRecyclerView.setAdapter(myArticlesAdapter);
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

        TextView et_id = (TextView) v.findViewById(R.id.article_id_id);

        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("from", "myarticles");
        intent.putExtra("id", et_id.getText().toString());

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
