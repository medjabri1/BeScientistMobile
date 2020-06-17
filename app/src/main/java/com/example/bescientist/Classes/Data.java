package com.example.bescientist.Classes;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Data {

    private Context context;
    private JSONObject jsonData;
    private String userType;
    private boolean dataFound = false;

    public Data(Context context, String userType) {
        this.context = context;
        this.userType = userType;
    }

    public boolean getDataFound() { return this.dataFound; }

    public JSONObject getData() {

        int id = PreferenceManager.getDefaultSharedPreferences(context).getInt("user_id", 0);

        final Handler mHandler = new Handler(Looper.getMainLooper());

        OkHttpClient client = new OkHttpClient();

        String url = "";

        if(userType.equalsIgnoreCase("U")) {
            url = "https://be-scientist.000webhostapp.com/api/user/data.php?user="+ id;
        } else if(userType.equalsIgnoreCase("e")) {
            url = "https://be-scientist.000webhostapp.com/api/user/data.php?editor="+ id;
        } else {
            return new JSONObject();
        }

        Request request = new Request.Builder().url(url).get().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        jsonData = new JSONObject();
                        showToast("Connection Error");
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

                            jsonData = jsonResponse;

                        } else {
                            jsonData = new JSONObject();
                        }
                    }catch (JSONException err){
                        jsonData = new JSONObject();
                        Log.d("Error", err.toString());
                    }
                }
            }
        });
        dataFound = true;
        return jsonData;
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}
