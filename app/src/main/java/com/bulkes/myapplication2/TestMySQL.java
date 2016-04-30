package com.bulkes.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Created by progr on 24.04.2016.
 */
public class TestMySQL extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    //  private URL url;
    private final String urlPost = "http://bulkes.orgfree.com/php/update.php";
    public static final String USER_NAME = "nickname";
    public static final String GAME_SCORE = "userscore";
    public static final String GAMETIME = "usergametime";
    private String name;
    private int gamescore;
    private String gametime;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_mysql);

        Intent deceivedData = this.getIntent();
        name = deceivedData.getStringExtra(USER_NAME);
        gamescore = deceivedData.getIntExtra(GAME_SCORE,-1);
        gametime = deceivedData.getStringExtra(GAMETIME);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetServerDataTask().execute();
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ServerDataTask().execute();
            }
        });
        activity = this;
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshBaseData);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    class GetServerDataTask extends AsyncTask<Void,Void,Void>
    {
        private OutputStreamWriter streamWriter;
        private BufferedReader buferedReader;
        protected Void doInBackground(Void... params) {
            try {
                int id = 2;
                String query = "id=" + id +"&name=" + name + "&gamescore=" + gamescore + "&gametime=" + gametime;
                URL url = new URL(urlPost);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                streamWriter = new OutputStreamWriter(connection.getOutputStream());
                streamWriter.write(query);
                streamWriter.flush();
                buferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = buferedReader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                Log.v("RESPONSE",sb.toString());
            } catch (MalformedURLException e) {
                Log.v("MalformedURLException", e.toString());
            } catch (IOException e) {
                Log.v("IOException", e.toString());
            } catch (Exception e) {
                Log.v("Some", e.toString());
            }
            finally {
                try {
                    streamWriter.close();
                    buferedReader.close();
                }
                catch(Exception ex) {}
            }
            return null;
        }
        protected void onPostExecute(Void result) {

        }
    }

    class ServerDataTask extends AsyncTask<Void,Void,Void>
    {
        private String result;
        private String response;
        private BufferedReader buferedReader;
        String jName;
        int jGamescore;
        String jGametime;
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://bulkes.orgfree.com/php/get.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                buferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(buferedReader.readLine() + "\n");
                //  while ((line = buferedReader.readLine()) != null) {
                //      //Log.v("EEE",line);b
                //       sb.append(line + "\n");
                //   }
                response = sb.toString();
                result = response.substring(response.indexOf('['));
                JSONArray array = new JSONArray(result);
                JSONObject jsonData;
                for (int i = 0; i < array.length(); i++) {
                    jsonData = array.getJSONObject(i);
                    jName = jsonData.getString("name");
                    jGamescore = jsonData.getInt("gamescore");
                    jGametime = jsonData.getString("gamescore");
                    Log.v("JSONArray", jName + " " + jGamescore + " " + jGametime);
                }
            } catch (MalformedURLException e) {
                Log.v("MalformedURLException", e.toString());
            } catch (IOException e) {
                Log.v("IOException", e.toString());
            } catch (Exception e) {
                Log.v("Some", e.toString());
            } finally {
                try {
                    buferedReader.close();
                } catch (Exception ex) {
                }
            }
            return null;
        }
        protected void onPostExecute(Void result) {

        }
    }

    @Override
    public void onRefresh()
    {
        Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 3000);

    }
}