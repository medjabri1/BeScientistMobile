package com.example.bescientist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bescientist.Activities.AuthorActivities.ArticlesToCorrectActivity;
import com.example.bescientist.Activities.AuthorActivities.MyArticlesActivity;
import com.example.bescientist.Activities.AuthorActivities.NewArticleActivity;
import com.example.bescientist.Activities.ReviewerActivities.ArticlesToVerifyActivity;
import com.example.bescientist.Activities.ReviewerActivities.ArticlesVerifiedActivity;
import com.example.bescientist.Activities.Settings.SettingsActivity;
import com.example.bescientist.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Play animation
        playAnimation();

        //Changer le nom et l'email dans la page d'acceuil
        updateInfoBar();

        //On refresh dashboard
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshDashboard);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateInfoBar();
                getUserStats();
            }
        });

        //Getting User Statistics
        getUserStats();

    }


    //My Methods

    //Animation
    private void playAnimation() {
        LinearLayout author_space = (LinearLayout) findViewById(R.id.author_space);
        LinearLayout reviewer_space = (LinearLayout) findViewById(R.id.reviewer_space);
        LinearLayout account_space = (LinearLayout) findViewById(R.id.account_space);

        author_space.setAlpha(0);
        author_space.setTranslationY(100);
        reviewer_space.setAlpha(0);
        reviewer_space.setTranslationY(100);
        account_space.setAlpha(0);
        account_space.setTranslationY(100);

        author_space.animate().alpha(1f).setDuration(600).start();
        author_space.animate().translationY(0).setDuration(600).start();
        reviewer_space.animate().alpha(1f).setDuration(600).setStartDelay(600).start();
        reviewer_space.animate().translationY(0).setDuration(600).setStartDelay(600).start();
        account_space.animate().alpha(1f).setDuration(600).setStartDelay(1200).start();
        account_space.animate().translationY(0).setDuration(600).setStartDelay(1200).start();
    }

    //Logout
    public void logout(View v) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("user_id").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("name").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("email").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("job").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("type").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("created_at").apply();
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    //Update Info bar
    private void updateInfoBar() {
        TextView tv_name_home = findViewById(R.id.name_home);
        TextView tv_email_home = findViewById(R.id.email_home);
        tv_name_home.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", "MJR Dev"));
        tv_email_home.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", "MJR@dev.com"));
    }

    //Get User Stats
    public void getUserStats() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshLayout.setRefreshing(true);

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();
        String url = "https://be-scientist.000webhostapp.com/api/user/stats.php?user="+ id;

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("Connection Error");
                        refreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if(response.isSuccessful()) {

                    final String myResponse = response.body().string();

                    try {
                        JSONObject jsonResponse = new JSONObject(myResponse);

                        if(jsonResponse.has("Data")) {
                            JSONObject data = jsonResponse.getJSONObject("Data");
                            final String mine = data.getString("mine");
                            final String toCorrect = data.getString("toCorrect");
                            final String toReview = data.getString("toReview");
                            final String reviewed = data.getString("reviewed");

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showStats(mine, toCorrect, toReview, reviewed);
                                    refreshLayout.setRefreshing(false);
                                }
                            });

                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showToast("No Stats found!");
                                    refreshLayout.setRefreshing(false);
                                }
                            });
                        }
                    }catch (JSONException err){
                        Log.d("Error", err.toString());
                        refreshLayout.setRefreshing(false);
                    }
                }
            }
        });
    }

    //Show User Stats
    private void showStats(String mine, String toCorrect, String toReview, String reviewed) {
        TextView tv_mine = (TextView) findViewById(R.id.suivi_nbr_id);
        TextView tv_toCorrect = (TextView) findViewById(R.id.correct_nbr_id);
        TextView tv_toReview = (TextView) findViewById(R.id.verify_nbr_id);
        TextView tv_reviewed = (TextView) findViewById(R.id.verified_nbr_id);

        tv_mine.setText(mine);
        tv_toCorrect.setText(toCorrect);
        tv_toReview.setText(toReview);
        tv_reviewed.setText(reviewed);
    }

    //GOTOs
    public void menuClicked(View v) {
        String tag = v.getTag().toString();
        switch(tag) {

            //Author cases
            case "author_new" : {
                Intent intent = new Intent(this, NewArticleActivity.class);
                startActivity(intent);
                break;
            }

            case "author_articles" : {
                TextView tv_suivi_nbr = (TextView) findViewById(R.id.suivi_nbr_id);
                String suivi_nbr = tv_suivi_nbr.getText().toString();

                if(suivi_nbr.equals("0")) {
                    showToast("Vous n'avez créer aucun article");

                } else if(suivi_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, MyArticlesActivity.class);
                    startActivity(intent);
                }
                break;
            }

            case "author_correct" : {
                TextView tv_correct_nbr = (TextView) findViewById(R.id.correct_nbr_id);
                String correct_nbr = tv_correct_nbr.getText().toString();

                if(correct_nbr.equals("0")) {
                    showToast("Vous n'avez aucun article à corriger");

                } else if(correct_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, ArticlesToCorrectActivity.class);
                    startActivity(intent);
                }
                break;
            }

            //Reviewer cases
            case "reviewer_verify" : {
                TextView tv_verify_nbr = (TextView) findViewById(R.id.verify_nbr_id);
                String verify_nbr = tv_verify_nbr.getText().toString();

                if(verify_nbr.equals("0")) {
                    showToast("Vous n'avez aucun article à vérifier");

                } else if(verify_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, ArticlesToVerifyActivity.class);
                    startActivity(intent);
                }
                break;
            }

            case "reviewer_verified" : {
                TextView tv_verified_nbr = (TextView) findViewById(R.id.verified_nbr_id);
                String verified_nbr = tv_verified_nbr.getText().toString();

                if(verified_nbr.equals("0")) {
                    showToast("Vous n'avez vérifier aucun article");

                } else if(verified_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");
                } else {
                    Intent intent = new Intent(this, ArticlesVerifiedActivity.class);
                    startActivity(intent);
                }
                break;
            }

            //Account cases
            case "settings" : {

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            }

            default : {
                showToast(tag);
            }
        }
    }

    //Toast
    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
