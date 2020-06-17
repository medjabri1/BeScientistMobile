package com.example.bescientist.Activities.EditorActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bescientist.Adapters.ChooseReviewerAdapter;
import com.example.bescientist.Adapters.MyArticlesAdapter;
import com.example.bescientist.Classes.Article;
import com.example.bescientist.Classes.Reviewer;
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
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChooseReviewerActivity extends AppCompatActivity {

    RecyclerView myRecyclerView;
    ChooseReviewerAdapter myReviewersAdapter;
    static List<Reviewer> list;

    SwipeRefreshLayout refreshReviewers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_reviewer);

        list = new ArrayList<Reviewer>();

        //Refreshing data
        refreshReviewers = (SwipeRefreshLayout) findViewById(R.id.refreshChooseReviewers);
        refreshReviewers.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        myRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_choose_reviewers_id);
        myReviewersAdapter = new ChooseReviewerAdapter(this, list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView.setAdapter(myReviewersAdapter);

        getData();

    }

    //My Methods

    @SuppressLint("SetTextI18n")
    private void chargeArticleInfo() {

        TextView tv_article_title = (TextView) findViewById(R.id.choose_reviewer_article_title_id);
        TextView tv_author_name = (TextView) findViewById(R.id.choose_reviewer_author_name_id);

        String article_title = getIntent().getStringExtra("title");
        String author_name = getIntent().getStringExtra("author");

        tv_article_title.setText(article_title);
        tv_author_name.setText("Auteur : "+ author_name);
    }

    //Get Data
    public void getData() {

        refreshReviewers.setRefreshing(true);

        list = new ArrayList<Reviewer>();

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        String url = "https://be-scientist.000webhostapp.com/api/reviewer/getAll.php";

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Failure Error");
                        refreshReviewers.setRefreshing(false);
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

                            final JSONArray reviewers = jsonResponse.getJSONArray("Data");

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    insertData(reviewers);
                                    showToast("Reviewers updated");
                                    refreshReviewers.setRefreshing(false);
                                }
                            });

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Data not found Error");
                                    refreshReviewers.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Exception Error");
                                refreshReviewers.setRefreshing(false);
                            }
                        });
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
    }

    //Insert data
    public void insertData(JSONArray reviewers) {

        //Charge article informations
        chargeArticleInfo();

        final int author_id = getIntent().getIntExtra("author_id", 0);

        for (int i=0; i<reviewers.length(); i++) {

            Reviewer reviewer = new Reviewer();

            try {

                reviewer.setId(reviewers.getJSONObject(i).getInt("id"));
                reviewer.setName(reviewers.getJSONObject(i).getString("name"));
                reviewer.setEmail(reviewers.getJSONObject(i).getString("email"));
                reviewer.setToReviewNbr(reviewers.getJSONObject(i).getInt("toreview"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(author_id == reviewer.getId()) continue;

            list.add(reviewer);
        }

        ChooseReviewerAdapter chooseReviewerAdapter = new ChooseReviewerAdapter(this, list);
        myRecyclerView.setAdapter(chooseReviewerAdapter);
    }

    //Reviewer Click
    @SuppressLint("SetTextI18n")
    public void chooseReviewerClick(View v) {
        TextView tv_reviewer_id = v.findViewById(R.id.reviewer_id_id);
        String article_title = getIntent().getStringExtra("title");
        String reviewer_name = ((TextView) v.findViewById(R.id.reviewer_name_id)).getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(view);

        ((TextView) view.findViewById(R.id.dialog_confirmation_content_id)).setText("Envoyer l'article : "+ article_title + "\nAu réviseur : "+ reviewer_name);
        ((TextView) view.findViewById(R.id.dialog_confirmation_confirm_id)).setText("Envoyer");

        //Data
        final String reviewer_id = tv_reviewer_id.getText().toString();
        final int article_id = getIntent().getIntExtra("id", 0);

        final AlertDialog alertDialog = builder.create();

        ((Button) view.findViewById(R.id.dialog_confirmation_confirm_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;

                btn.setText("En cours..");
                btn.setClickable(false);
                btn.setTextColor(0x44777777);

                final Handler mHandler = new Handler(Looper.getMainLooper());

                OkHttpClient client = new OkHttpClient();
                String url = "https://be-scientist.000webhostapp.com/api/editor/sendToReviewer.php";

                RequestBody formBody = new FormBody.Builder()
                        .add("article_id", String.valueOf(article_id))
                        .add("reviewer_id", reviewer_id)
                        .build();

                Request request = new Request.Builder().url(url).post(formBody).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("Connection error");
                                new Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                alertDialog.dismiss();
                                                finish();
                                            }
                                        },
                                        1000);
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if(response.isSuccessful()) {

                            final String myResponse = response.body().string();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(myResponse);
                                    new Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    alertDialog.dismiss();
                                                    finish();
                                                }
                                            },
                                            1000);
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("Server Error");
                                    new Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    alertDialog.dismiss();
                                                    finish();
                                                }
                                            },
                                            1000);
                                }
                            });
                        }
                    }
                });
            }
        });

        ((Button) view.findViewById(R.id.dialog_confirmation_close_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if(alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.setCancelable(false);
        alertDialog.show();
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
