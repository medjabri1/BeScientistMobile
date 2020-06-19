package com.example.bescientist.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.bescientist.Activities.EditorActivities.AcceptedArticlesActivity;
import com.example.bescientist.Activities.EditorActivities.EditorReviewedArticlesActivity;
import com.example.bescientist.Activities.EditorActivities.NewArticlesActivity;
import com.example.bescientist.Activities.EditorActivities.SentArticlesActivity;
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

public class AdminActivity extends AppCompatActivity {

    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //Animation
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

        //Get Admin stats
        getUserStats();

    }

    //My Methods

    //Animation
    private void playAnimation() {
        LinearLayout editor_space = (LinearLayout) findViewById(R.id.editor_space);
        LinearLayout account_space = (LinearLayout) findViewById(R.id.account_space);

        editor_space.setAlpha(0);
        editor_space.setTranslationY(100);
        account_space.setAlpha(0);
        account_space.setTranslationY(100);

        editor_space.animate().alpha(1f).setDuration(600).start();
        editor_space.animate().translationY(0).setDuration(600).start();
        account_space.animate().alpha(1f).setDuration(600).setStartDelay(600).start();
        account_space.animate().translationY(0).setDuration(600).setStartDelay(600).start();
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
        TextView tv_name_admin = findViewById(R.id.name_admin);
        TextView tv_email_admin = findViewById(R.id.email_admin);
        tv_name_admin.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("name", "MJR Dev"));
        tv_email_admin.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("email", "MJR@dev.com"));
    }

    //Get User Stats
    public void getUserStats() {
        int id = PreferenceManager.getDefaultSharedPreferences(this).getInt("user_id", 0);

        refreshLayout.setRefreshing(true);

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();
        String url = "https://be-scientist.000webhostapp.com/api/user/stats.php?editor="+ id;

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
                            final String newArticles = data.getString("new");
                            final String sent = data.getString("sent");
                            final String reviewed = data.getString("reviewed");
                            final String accepted = data.getString("accepted");

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showStats(newArticles, sent, reviewed, accepted);
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
                        showToast("Exception error");
                    }
                }
            }
        });
    }

    //Show User Stats
    private void showStats(String newArticles, String sent, String reviewed, String accepted) {
        TextView tv_new = (TextView) findViewById(R.id.new_nbr_id);
        TextView tv_sent = (TextView) findViewById(R.id.sent_nbr_id);
        TextView tv_reviewed = (TextView) findViewById(R.id.reviewed_nbr_id);
        TextView tv_accepted = (TextView) findViewById(R.id.accepted_nbr_id);

        tv_new.setText(newArticles);
        tv_sent.setText(sent);
        tv_reviewed.setText(reviewed);
        tv_accepted.setText(accepted);
    }

    //GOTOs
    public void menuClicked(View v) {
        String tag = v.getTag().toString();
        switch(tag) {

            //Editor cases
            case "editor_new" : {
                TextView new_nbr_id = (TextView) findViewById(R.id.new_nbr_id);
                String new_nbr = new_nbr_id.getText().toString();

                if(new_nbr.equals("0")) {
                    showToast("Aucun nouveau article trouvé");

                } else if(new_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, NewArticlesActivity.class);
                    startActivity(intent);
                }
                break;
            }

            case "editor_sent" : {
                TextView sent_nbr_id = (TextView) findViewById(R.id.sent_nbr_id);
                String sent_nbr = sent_nbr_id.getText().toString();

                if(sent_nbr.equals("0")) {
                    showToast("Aucun article envoyé");

                } else if(sent_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, SentArticlesActivity.class);
                    startActivity(intent);
                }
                break;
            }

            case "editor_reviewed" : {
                TextView tv_reviewed_nbr = (TextView) findViewById(R.id.reviewed_nbr_id);
                String reviewed_nbr = tv_reviewed_nbr.getText().toString();

                if(reviewed_nbr.equals("0")) {
                    showToast("Aucun article a été revue");

                } else if(reviewed_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, EditorReviewedArticlesActivity.class);
                    startActivity(intent);
                }
                break;
            }
            case "editor_accepted" : {
                TextView tv_accepted_nbr = (TextView) findViewById(R.id.accepted_nbr_id);
                String accepted_nbr = tv_accepted_nbr.getText().toString();

                if(accepted_nbr.equals("0")) {
                    showToast("Aucun article a été accepté");

                } else if(accepted_nbr.equalsIgnoreCase("n/a")) {
                    showToast("Not avalaible");

                } else {
                    Intent intent = new Intent(this, AcceptedArticlesActivity.class);
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
