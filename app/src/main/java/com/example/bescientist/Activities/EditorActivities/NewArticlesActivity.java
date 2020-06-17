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

import com.example.bescientist.R;
import com.example.bescientist.Adapters.NewArticlesAdapter;
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

public class NewArticlesActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    NewArticlesAdapter myNewArticlesAdapter;
    static List<Article> list;

    SwipeRefreshLayout refreshNewArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_articles);

        list = new ArrayList<Article>();

        //Refreshing data
        refreshNewArticles = (SwipeRefreshLayout) findViewById(R.id.refreshNewArticles);
        refreshNewArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_new_articles_id);
        myNewArticlesAdapter = new NewArticlesAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(myNewArticlesAdapter);

        getData();

    }

    //My Methods

    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshNewArticles.setRefreshing(true);

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
                        refreshNewArticles.setRefreshing(false);
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

                                final JSONArray newArticles = dataGlobal.getJSONArray("new");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(newArticles);
                                        showToast("New Articles updated");
                                        refreshNewArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("New articles not found");
                                        refreshNewArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshNewArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshNewArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray newArticles) {

        for (int i=0; i<newArticles.length(); i++) {

            Article article = new Article();

            try {

                article.setId(newArticles.getJSONObject(i).getInt("id"));
                article.setAuthor_id(newArticles.getJSONObject(i).getInt("author_id"));
                article.setAuthor_name(newArticles.getJSONObject(i).getString("author_name"));
                article.setTitle(newArticles.getJSONObject(i).getString("title"));
                article.setContent(newArticles.getJSONObject(i).getString("content"));
                article.setDomain(newArticles.getJSONObject(i).getString("domain"));
                article.setCreated_at(newArticles.getJSONObject(i).getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        NewArticlesAdapter myNewArticlesAdapter = new NewArticlesAdapter(this, list);
        myRecyclerView.setAdapter(myNewArticlesAdapter);
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

        Intent intent = new Intent(this, NewArticleActivity.class);
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
