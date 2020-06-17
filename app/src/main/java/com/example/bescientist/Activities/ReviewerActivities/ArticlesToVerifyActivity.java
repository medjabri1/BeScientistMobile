package com.example.bescientist.Activities.ReviewerActivities;

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
import com.example.bescientist.Adapters.ArticlesToVerifyAdapter;
import com.example.bescientist.Classes.ArticleToVerify;

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

public class ArticlesToVerifyActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    ArticlesToVerifyAdapter articlesToVerifyAdapter;
    static List<ArticleToVerify> list;

    SwipeRefreshLayout refreshToVerifyArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_to_verify);

        list = new ArrayList<ArticleToVerify>();

        //Refreshing data
        refreshToVerifyArticles = (SwipeRefreshLayout) findViewById(R.id.refreshToVerifyArticles);
        refreshToVerifyArticles.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_to_verify_articles_id);
        articlesToVerifyAdapter = new ArticlesToVerifyAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(articlesToVerifyAdapter);

        getData();
    }

    //My Methods
    //Get Data
    public void getData() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshToVerifyArticles.setRefreshing(true);

        list = new ArrayList<ArticleToVerify>();

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
                        refreshToVerifyArticles.setRefreshing(false);
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

                            if(dataGlobal.has("toReview")) {

                                final JSONArray toReview = dataGlobal.getJSONArray("toReview");

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        insertData(toReview);
                                        showToast("Articles to review updated");
                                        refreshToVerifyArticles.setRefreshing(false);
                                    }
                                });
                            } else {

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToast("Articles to review not found");
                                        refreshToVerifyArticles.setRefreshing(false);
                                    }
                                });

                            }

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshToVerifyArticles.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshToVerifyArticles.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray toReview) {

        for (int i=0; i<toReview.length(); i++) {

            ArticleToVerify article = new ArticleToVerify();

            try {

                article.setId(toReview.getJSONObject(i).getInt("id"));
                article.setAuthor_id(toReview.getJSONObject(i).getInt("author_id"));
                article.setReviewer_id(PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0));
                article.setReviewer_name(PreferenceManager.getDefaultSharedPreferences(this).getString("name", "xx"));
                article.setTitle(toReview.getJSONObject(i).getString("title"));
                article.setContent(toReview.getJSONObject(i).getString("content"));
                article.setDomain(toReview.getJSONObject(i).getString("domain"));
                article.setStatus(toReview.getJSONObject(i).getString("status"));
                article.setCreated_at(toReview.getJSONObject(i).getString("created_at"));
                article.setSent_at(toReview.getJSONObject(i).getString("sent_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(article);
        }

        ArticlesToVerifyAdapter articlesToVerifyAdapter = new ArticlesToVerifyAdapter(this, list);
        myRecyclerView.setAdapter(articlesToVerifyAdapter);
    }

    //Get Article By id
    public static ArticleToVerify getArticleById(int id) {
        for (int i=0; i<list.size(); i++) {
            if(id == list.get(i).getId()) {
                return list.get(i);
            }
        }
        return new ArticleToVerify();
    }

    //Click sur l'article
    public void myArticleClick(View v) {

        TextView et_id = (TextView) v.findViewById(R.id.article_to_verify_id_id);

        Intent intent = new Intent(this, VerifyArticleActivity.class);
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
