package com.bulkes.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


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
            // dismiss progress dialog and update ui
        }
    }

    class ServerDataTask extends AsyncTask<Void,Void,Void>
    {
        private String result;
        private OutputStreamWriter streamWriter;
        private BufferedReader buferedReader;
        private JSONArray jArray;

        String jName;
        int jGamescore;
        String jGametime;

        protected Void doInBackground(Void... params) {
            try {
                int id = 10000;
                String query = "id=" + id +"&name=" + name + "&gamescore=" + gamescore + "&gametime=" + gametime;

                URL url = new URL("http://bulkes.orgfree.com/php/get.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                streamWriter = new OutputStreamWriter(connection.getOutputStream());
                streamWriter.write(query);
                streamWriter.flush();
                buferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Gson gson = new Gson();
             //   JsonArray jsonArray = new JsonParser().parse(buferedReader).getAsJsonArray();

               // JsonParser jsonElement = new JsonParser();
               // JsonObject result = jsonElement.parse(buferedReader).getAsJsonObject();

                StringBuilder sb = new StringBuilder();
                // sb.append(buferedReader.readLine() + "\n");
                String line="0";

                while ((line = buferedReader.readLine()) != null) {
                    //Log.v("EEE",line);
                    sb.append(line + "\n");
                }
                JsonReader reader = new JsonReader(new StringReader(sb.toString()));
                reader.setLenient(true);
                reader.beginArray();
                while (reader.hasNext()) {
                    Log.v("GGGGGG",reader.getPath());
                }
                reader.endArray();
                //Log.v("GGGGGG",reader.toString());
               // reader.
               // jName = gson.fromJson(reader, String.class);
               // jGamescore = gson.fromJson(reader,Integer.class);
              //  jGametime = gson.fromJson(reader,String.class);

/*
                Set<Map.Entry<String, JsonElement>> entrySet = result.entrySet();
                for(Map.Entry<String, JsonElement> entry : entrySet) {
                    Log.v("RRRRRR",entry.getKey());
                   /* jName = gson.fromJson(result(entry.getKey()), String.class);
                    jGamescore = gson.fromJson(jsonElement,Integer.class);
                    jGametime = gson.fromJson(jsonElement,String.class);
                    Log.v("JSONArray",jName+" "+jGamescore+" "+jGametime);
                    */
             //   }
/*
                Set<Map.Entry<String, JsonElement>> entrySet = result.entrySet();
                for(Map.Entry<String, JsonElement> entry : entrySet) {
                    User newUser = gson.fromJson(p.getAsJsonObject(entry.getKey()), User.class);
                    newUser.username = entry.getKey();
                    //code...
                }

                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement jsonElement = jsonArray.get(i);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                    for(Map.Entry<String, JsonElement> entry : entrySet) {
                        User newUser = gson.fromJson(p.getAsJsonObject(entry.getKey()), User.class);
                        newUser.username = entry.getKey();
                        //code...
                    }


                }
                StringBuilder sb = new StringBuilder();
               // sb.append(buferedReader.readLine() + "\n");
                String line="0";
                while ((line = buferedReader.readLine()) != null) {
                    Log.v("EEE",line);
                    sb.append(line + "\n");
                }
                */
                streamWriter.close();
                //result = sb.toString();
               // Log.v("RESPONSEEEEEEE",sb.toString());
            } catch (MalformedURLException e) {
                Log.v("MalformedURLException", e.toString());
            } catch (IOException e) {
                Log.v("IOException", e.toString());
            } catch (Exception e) {
                Log.v("Some", e.toString());
            }
            finally {
                try {
                   // streamWriter.close();
                    buferedReader.close();
                }
                catch(Exception ex) {}
            }



/*
            String jName;
            int jGamescore;
            String jGametime;
            try{
            //    JSONArray jsonArray = new JsonParser().parse(br).getAsJsonArray();
                jArray = new JSONArray(result);
                JSONObject jsonData;
                for(int i=0;i<jArray.length();i++){
                    jsonData = jArray.getJSONObject(i);
                    jName = jsonData.getString("name");
                    jGamescore = jsonData.getInt("gamescore");
                    jGametime = jsonData.getString("gamescore");
                    Log.v("JSONArray",jName+" "+jGamescore+" "+jGametime);
                }

            }catch(JSONException e1){
                Log.v("JSONException", e1.toString());
            }catch (ParseException e1){
                Log.v("ParseException", e1.toString());
            }
*/
            return null;
        }
        protected void onPostExecute(Void result) {
            // dismiss progress dialog and update ui
        }
    }

    @Override
    public void onRefresh()
    {
        Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(true);

       /* swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {

                }

        }, 3000);
*/
    }
}
