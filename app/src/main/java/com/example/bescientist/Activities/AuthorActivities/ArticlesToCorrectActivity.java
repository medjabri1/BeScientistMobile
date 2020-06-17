package com.example.bescientist.Activities.AuthorActivities;

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

import com.example.bescientist.R;
import com.example.bescientist.Adapters.ArticlesToCorrectAdapter;
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

public class ArticlesToCorrectActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    ArticlesToCorrectAdapter articlesToCorrectAdapter;
    static List<Article> list;

    SwipeRefreshLayout refreshArticlesToCorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_to_correct);

        list = new ArrayList<Article>();

        //Refreshing data
        refreshArticlesToCorrect = (SwipeRefreshLayout) findViewById(R.id.refreshArticlesToCorrect);
        refreshArticlesToCorrect.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_articles_to_correct_id);
        articlesToCorrectAdapter = new ArticlesToCorrectAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(articlesToCorrectAdapter);

        getData();

    }

    //My Methods

    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshArticlesToCorrect.setRefreshing(true);

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
                        refreshArticlesToCorrect.setRefreshing(false);
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

                            if(dataGlobal.has("toCorrect")) {

                                final JSONArray toCorrect = dataGlobal.getJSONArray("toCorrect");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(toCorrect);
                                        showToast("Articles to correct updated");
                                        refreshArticlesToCorrect.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Article to correct not found");
                                        refreshArticlesToCorrect.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshArticlesToCorrect.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshArticlesToCorrect.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray toCorrect) {

        for (int i=0; i<toCorrect.length(); i++) {

            Article article = new Article();

            try {

                article.setId(toCorrect.getJSONObject(i).getInt("id"));
                article.setAuthor_id(toCorrect.getJSONObject(i).getInt("author_id"));
                article.setTitle(toCorrect.getJSONObject(i).getString("title"));
                article.setContent(toCorrect.getJSONObject(i).getString("content"));
                article.setDomain(toCorrect.getJSONObject(i).getString("domain"));
                article.setStatus(toCorrect.getJSONObject(i).getString("status"));
                article.setCreated_at(toCorrect.getJSONObject(i).getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        ArticlesToCorrectAdapter articlesToCorrectAdapter = new ArticlesToCorrectAdapter(this, list);
        myRecyclerView.setAdapter(articlesToCorrectAdapter);
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

        Intent intent = new Intent(this, CorrectArticleActivity.class);
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
