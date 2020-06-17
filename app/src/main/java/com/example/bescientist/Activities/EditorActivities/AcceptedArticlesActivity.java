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

import com.example.bescientist.Activities.AuthorActivities.ArticleActivity;
import com.example.bescientist.Adapters.NewArticlesAdapter;
import com.example.bescientist.Classes.Article;
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

public class AcceptedArticlesActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    NewArticlesAdapter acceptedArticlesAdapter;
    static List<Article> list;

    SwipeRefreshLayout refreshAcceptedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_articles);

        list = new ArrayList<Article>();

        //Refreshing data
        refreshAcceptedArticles = (SwipeRefreshLayout) findViewById(R.id.refreshAcceptedArticles);
        refreshAcceptedArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_accepted_articles_id);
        acceptedArticlesAdapter = new NewArticlesAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(acceptedArticlesAdapter);

        getData();


    }

    //My Methods

    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshAcceptedArticles.setRefreshing(true);

        list = new ArrayList<Article>();

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
                        refreshAcceptedArticles.setRefreshing(false);
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

                            if(dataGlobal.has("new")) {

                                final JSONArray accepted = dataGlobal.getJSONArray("accepted");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(accepted);
                                        showToast("Accepted Articles updated");
                                        refreshAcceptedArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Accepted articles not found");
                                        refreshAcceptedArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshAcceptedArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshAcceptedArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray acceptedArticles) {

        for (int i=0; i<acceptedArticles.length(); i++) {

            Article article = new Article();

            try {

                article.setId(acceptedArticles.getJSONObject(i).getInt("id"));
                article.setAuthor_id(acceptedArticles.getJSONObject(i).getInt("author_id"));
                article.setAuthor_name(acceptedArticles.getJSONObject(i).getString("author_name"));
                article.setTitle(acceptedArticles.getJSONObject(i).getString("title"));
                article.setContent(acceptedArticles.getJSONObject(i).getString("content"));
                article.setDomain(acceptedArticles.getJSONObject(i).getString("domain"));
                article.setStatus("A");
                article.setCreated_at(acceptedArticles.getJSONObject(i).getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        NewArticlesAdapter acceptedArticlesAdapter = new NewArticlesAdapter(this, list);
        myRecyclerView.setAdapter(acceptedArticlesAdapter);
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

        TextView et_id = (TextView) v.findViewById(R.id.item_new_article_id_id);

        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("from", "acceptedarticles");
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
