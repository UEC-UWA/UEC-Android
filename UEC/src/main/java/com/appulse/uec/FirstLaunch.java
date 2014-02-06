package com.appulse.uec;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.appulse.uec.helpers.ManagedEntity;
import com.appulse.uec.helpers.MySQLHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

public class FirstLaunch extends Activity {

    final String PREFS_NAME = "MyPrefsFile";
private Button b;

    private int entityPulled;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_launch);

        entityPulled = 0;

        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                determineAction();
            }
        });

        determineAction();

    }

    private void determineAction() {
        if (isNetworkAvailable()) {
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            b.setVisibility(View.GONE);
            pullData();
        } else {
            ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.GONE);
            b.setVisibility(View.VISIBLE);
            entityPulled = 0;
        }
    }
    private void pullData() {
        Resources res = getResources();
        String[] column;
        if (entityPulled == 0) {
            column = res.getStringArray(R.array.sponsors_entity);
            get_json(column, "Sponsors","sponsor");
        } else if (entityPulled == 1) {
            column = res.getStringArray(R.array.committee_entity);
            get_json(column, "Committee","person");
        } else if (entityPulled == 2) {
            column = res.getStringArray(R.array.events_entity);
            get_json(column, "Events","event");
        } else if (entityPulled == 3) {

            column = res.getStringArray(R.array.news_entity);
            get_json(column, "News","newsarticle");
        } else if (entityPulled == 4) {
            column = res.getStringArray(R.array.torques_entity);
            get_json(column, "Torques","torque");
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            settings.edit().putBoolean("my_first_time", false).commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo n = manager.getActiveNetworkInfo();
        return n != null && n.isAvailable();
    }

    public void get_json(final String[] column, final String entity, final String postFix) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://www.appulse.com.au/uec/app_scripts.php?script=" + postFix, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                JSONArray result = null;
                try {
                    result = new JSONArray(response);
                } catch (JSONException e) {

                }
                handleResponse(result,column,entity);
            }
        });
    }

    public void handleResponse(JSONArray jsonPosts,String[] column, String entity) {
        if (jsonPosts == null) {

        } else {
            try {
                MySQLHelper db = new MySQLHelper(this);
                for (int i = 0; i < jsonPosts.length(); i++) {
                    ManagedEntity item = new ManagedEntity(entity);

                    int id = jsonPosts.getJSONObject(i).getInt("id");

                    for (int j = 1; j < column.length; j++) {
                        item.setValue(column[j], jsonPosts.getJSONObject(i).getString(column[j]));

                    }
                    item.setId(id);

                    db.addEntity(entity, item, column);
                }

                db.close();
            } catch (JSONException e) {

            }
        }
        entityPulled++;
        pullData();
    }
}