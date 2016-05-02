package com.bulkes.myapplication2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by progr on 24.04.2016.
 */
public class GameTable extends AppCompatActivity implements View.OnClickListener {

    public static RecyclerView gameTableList;
    private TableListAdapter tableListAdapter;
    public static final String ID = "id";
    public static final String USER_NAME = "username";
    public static final String USERS_SCORES = "usersscores";
    public static final String GAME_TIME = "gametime";
    private String rangeRequest = "http://bulkes.orgfree.com/php/newtable.php?up=%d&down=%d";
    private String currentPositionRequest = "http://bulkes.orgfree.com/php/starttable.php?id=%d&offset=%d";
    private String postRequest = "http://bulkes.orgfree.com/php/setvalue.php";
    private Activity activity;
    private ProgressBar dataGettingProgressBar;
    private ImageButton refreshTableBtn;
    private ImageButton toFirstPositionBtn;
    private ImageButton closeTableBtn;
    private LinearLayoutManager linearLayoutManager;
    private List<PlayersAchievements> playersAchievementsList;
    private Boolean isInsertDownDirection;
    private String internetAccessError;
    private String serverConnectionTimeout;
    private String receiveUserDataError;
    private int dataBaseItemsCount;
    private int maxRecyclerVisibleItemsCount;
    private int firstPosition;
    private float recyclerTableRowHeight;
    private int usersId;
    private String userName;
    private int scores;
    private String gameTime;
    private Window window;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollRecyclerViewToPosition();
    }

    @Override
    public void onCreate(Bundle saveInstance)
    {
        super.onCreate(saveInstance);
        overridePendingTransition(R.animator.activity_down_up_enter, R.animator.activity_down_up_exit);
        setContentView(R.layout.game_table_layout);
        window = getWindow();
        if (Build.VERSION.SDK_INT >= 19 && deviceImmersiveSupport()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setFlags(WindowManager.LayoutParams.FLAGS_CHANGED, WindowManager.LayoutParams.FLAGS_CHANGED);
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_IMMERSIVE) == 0) {
                        setFullScreenMode();
                    }
                }
            });
        }
        Intent userTableData = getIntent();
        usersId = userTableData.getIntExtra(ID, 0);
        userName = userTableData.getStringExtra(USER_NAME);
        scores = userTableData.getIntExtra(USERS_SCORES, 0);
        gameTime = userTableData.getStringExtra(GAME_TIME);

        gameTableList = (RecyclerView)findViewById(R.id.gameTableList);
        dataGettingProgressBar = (ProgressBar) findViewById(R.id.dataGettingProgressBar);
        refreshTableBtn = (ImageButton) findViewById(R.id.refreshTableBtn);
        toFirstPositionBtn = (ImageButton) findViewById(R.id.toFirstPositionBtn);
        closeTableBtn = (ImageButton) findViewById(R.id.closeTableBtn);
        refreshTableBtn.setOnClickListener(this);
        toFirstPositionBtn.setOnClickListener(this);
        closeTableBtn.setOnClickListener(this);
        playersAchievementsList = Collections.synchronizedList(new ArrayList<PlayersAchievements>());
        isInsertDownDirection = true;
        activity = this;
        dataBaseItemsCount = 0;
        firstPosition = 1;//initial position, since first is progressbar
        internetAccessError = getResources().getString(R.string.check_internet_access);
        serverConnectionTimeout = getResources().getString(R.string.connection_time_out);
        receiveUserDataError = getResources().getString(R.string.receive_user_data_error);
        gameTableList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int recyclerViewHeight = gameTableList.getHeight();
                recyclerTableRowHeight = getResources().getDimension(R.dimen.table_row_height);
                maxRecyclerVisibleItemsCount = Math.round(recyclerViewHeight / recyclerTableRowHeight);
                if (isInternetAvailable()) {
                    Boolean serverResult;
                    try {
                        serverResult = new sendUsersDataToServer().execute().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        serverResult = false;
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        serverResult = false;
                    }
                    if (!serverResult) {
                        Toast.makeText(activity, receiveUserDataError, Toast.LENGTH_SHORT).show();
                    } else
                        new ServerDataTask(true, false).execute(String.format(currentPositionRequest, usersId, maxRecyclerVisibleItemsCount + 1));
                } else {
                    Toast.makeText(activity, internetAccessError, Toast.LENGTH_SHORT).show();
                    dataGettingProgressBar.setVisibility(View.INVISIBLE);
                }
                gameTableList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
/*
        playersAchievementsList.add(new PlayersAchievements(10,"fdfsd",1,"fds"));
        playersAchievementsList.add(new PlayersAchievements(11,"dfs",1,"ew"));
        playersAchievementsList.add(new PlayersAchievements(12,"sd",1,"re"));
        playersAchievementsList.add(new PlayersAchievements(13,"fd",1,"gf"));
        playersAchievementsList.add(new PlayersAchievements(14,"gf",1,"bv"));
        playersAchievementsList.add(new PlayersAchievements(15,"hg",1," vb"));
        playersAchievementsList.add(new PlayersAchievements(16,"jh",1,"nmmgh"));
        playersAchievementsList.add(new PlayersAchievements(17,"kj",1,"hgfjyt"));
        playersAchievementsList.add(new PlayersAchievements(18,"uijt",1,"qwqewr"));
*/
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gameTableList.setLayoutManager(linearLayoutManager);
        tableListAdapter = new TableListAdapter(playersAchievementsList);
        gameTableList.setAdapter(tableListAdapter);
        gameTableList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!tableListAdapter.getLoadedState() && dy != 0) {
                    if (dy > 0) isInsertDownDirection = true;
                    else if (dy < 0) isInsertDownDirection = false;
                    int visibleThreshold = 2;
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
                    int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastItemPosition;
                    if (isInsertDownDirection) {
                        lastItemPosition = playersAchievementsList.get(playersAchievementsList.size() - 1).getPositionInTable() + 1;
                        if (totalItemCount <= (lastVisiblePosition + visibleThreshold)) {
                            if (lastItemPosition < dataBaseItemsCount) {
                                tableListAdapter.setLoadedState(true);
                                new ServerDataTask().execute(String.format(rangeRequest, lastItemPosition, lastItemPosition + maxRecyclerVisibleItemsCount));
                            } else {
                                if (!tableListAdapter.getIsRecyclerDownEdge()) {
                                    tableListAdapter.downEdgeAchieved();
                                }
                            }
                        }
                    } else if (!isInsertDownDirection) {
                        lastItemPosition = playersAchievementsList.get(0).getPositionInTable() - 1;
                        if (firstVisiblePosition - visibleThreshold <= 0) {
                            if (lastItemPosition > 0) {
                                tableListAdapter.setLoadedState(true);
                                new ServerDataTask().execute(String.format(rangeRequest, lastItemPosition - maxRecyclerVisibleItemsCount, lastItemPosition));
                            } else {
                                if (!tableListAdapter.getIsRecyclerUpEdge()) {
                                    tableListAdapter.upEdgeAchieved();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void scrollRecyclerViewToPosition() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayoutManager.scrollToPositionWithOffset(firstPosition, 0);
            }
        }, 700);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeTableBtn:
                Intent activityResult = new Intent();
                activityResult.putExtra(ID, usersId);
                activity.setResult(2, activityResult);
                finish();
                break;
            case R.id.refreshTableBtn:
                Boolean serverResult;
                if (isInternetAvailable()) {
                    dataGettingProgressBar.setVisibility(View.VISIBLE);
                    if (usersId == 0) {
                        try {
                            serverResult = new sendUsersDataToServer().execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            serverResult = false;
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            serverResult = false;
                        }
                        if (!serverResult) {
                            Toast.makeText(activity, receiveUserDataError, Toast.LENGTH_SHORT).show();
                            dataGettingProgressBar.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                    new ServerDataTask(true, true).execute(String.format(currentPositionRequest, usersId, maxRecyclerVisibleItemsCount + 1));
                    scrollRecyclerViewToPosition();
                } else {
                    Toast.makeText(activity, internetAccessError, Toast.LENGTH_SHORT).show();
                    dataGettingProgressBar.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.toFirstPositionBtn:
                new ServerDataTask(false, true).execute(String.format(rangeRequest, 1, maxRecyclerVisibleItemsCount + 1));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.animator.activity_down_up_close_enter, R.animator.activity_down_up_close_exit);
    }

    @Override
    protected void onStart() {
        setFullScreenMode();
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent activityResult = new Intent();
            activityResult.putExtra(ID, usersId);
            activity.setResult(2, activityResult);
            finish();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setFullScreenMode();
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean deviceImmersiveSupport() {
        try {
            int id = getResources().getIdentifier("config_enableTranslucentDecor", "bool", "android");
            if (id == 0) {
                return false;
            } else {
                boolean enable = getResources().getBoolean(id);
                return enable;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private void setFullScreenMode() {
        if (Build.VERSION.SDK_INT < 19) {
            window.getDecorView().setSystemUiVisibility(View.GONE);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (deviceImmersiveSupport()) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            java.lang.Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int result = ipProcess.waitFor();
            ipProcess.destroy();
            return (result == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    class sendUsersDataToServer extends AsyncTask<Void, Void, Boolean> {
        private OutputStreamWriter streamWriter;
        private BufferedReader bufferedReader;
        private Boolean flagResult;

        public sendUsersDataToServer() {
            flagResult = true;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                String query = "id=" + usersId + "&name=" + userName + "&gamescore=" + scores + "&gametime=" + gameTime;
                URL url = new URL(postRequest);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(20000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                streamWriter = new OutputStreamWriter(connection.getOutputStream());
                streamWriter.write(query);
                streamWriter.flush();
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                sb.append(bufferedReader.readLine() + "\n");
                String str = sb.toString();
                if (usersId == 0) {
                    try {
                        usersId = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                    } catch (NumberFormatException e) {
                        flagResult = false;
                    }
                }
            } catch (MalformedURLException e) {
                flagResult = false;
                Log.v("MalformedURLException", e.toString());
            } catch (IOException e) {
                flagResult = false;
                Log.v("IOException", e.toString());
            } catch (Exception e) {
                flagResult = false;
                Log.v("Some", e.toString());
            } finally {
                try {
                    streamWriter.close();
                    bufferedReader.close();
                } catch (Exception ex) {
                }
            }
            return flagResult;
        }

        protected void onPostExecute(Boolean result) {
            if (!result) {
            }
        }
    }

    class ServerDataTask extends AsyncTask<String, Boolean, Boolean> {
        private String response;
        private BufferedReader bufferedReader;
        private ArrayList<PlayersAchievements> resultData;
        private HttpURLConnection connection;
        private Boolean flagResult;
        private Boolean isInitialDataGetting;
        private Boolean needClearArray;

        public ServerDataTask() {
            isInitialDataGetting = false;
            needClearArray = false;
            flagResult = true;
        }

        public ServerDataTask(Boolean isInitialDataGetting, Boolean needClearArray) {
            this.isInitialDataGetting = isInitialDataGetting;
            this.needClearArray = needClearArray;
            flagResult = true;
        }

        protected Boolean doInBackground(String... urlPaths) {
            try {
                URL url = new URL(urlPaths[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(20000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                sb.append(bufferedReader.readLine() + "\n");
                String str = sb.toString();
                if (isInitialDataGetting) {
                    dataBaseItemsCount = Integer.parseInt(str.substring(0, str.indexOf('[')).replaceAll("[^0-9]", ""));
                }
                response = str.substring(str.indexOf('['));
                JSONArray array = new JSONArray(response);
                JSONObject jsonData;
                resultData = new ArrayList<>(array.length());
                for (int i = 0; i < array.length(); i++) {
                    jsonData = array.getJSONObject(i);
                    resultData.add(new PlayersAchievements(jsonData.getInt("position"), jsonData.getString("name"), jsonData.getInt("gamescore"), jsonData.getString("gametime")));
                }
            } catch (NumberFormatException ex) {
                Log.v("NumberFormatException", ex.getMessage());
                flagResult = false;
            } catch (MalformedURLException e) {
                flagResult = false;
                Log.v("MalformedURLException", e.toString());
            } catch (IOException e) {
                flagResult = false;
                Log.v("IOException", e.toString());
            } catch (Exception e) {
                flagResult = false;
                Log.v("Exception", e.toString());
            } finally {
                try {
                    bufferedReader.close();
                    connection.disconnect();
                } catch (Exception ex) {
                    Log.v("BufferedReaderClose", ex.toString());
                }
            }
            return flagResult;
        }

        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dataGettingProgressBar.setVisibility(View.GONE);
            if (!result) {
                Toast.makeText(activity, serverConnectionTimeout, Toast.LENGTH_SHORT).show();
                tableListAdapter.setLoadedState(false);
/*
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        if(isInsertDownDirection) {
                            Log.v("AAAAA"," "+ (linearLayoutManager.findFirstVisibleItemPosition()-1));
                            gameTableList.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition()-1);
                        }
                        else {
                            Log.v("FFFFF"," "+ linearLayoutManager.findFirstVisibleItemPosition()+1);
                            gameTableList.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition()+1);
                        }
                        tableListAdapter.setLoadedState(false);
                    }
                },1500);
                */
            } else {
                int startInsertingPosition;
                if (isInsertDownDirection) {
                    startInsertingPosition = playersAchievementsList.size();
                } else {
                    startInsertingPosition = 0;
                }
                if (needClearArray) {
                    playersAchievementsList.clear();
                    playersAchievementsList.addAll(resultData);
                    tableListAdapter.initAdapter();
                    tableListAdapter.notifyDataSetChanged();
                    if (resultData.get(0).getPositionInTable() == 1) {
                        tableListAdapter.upEdgeAchieved();
                        gameTableList.scrollToPosition(0);
                    } else {
                        linearLayoutManager.scrollToPositionWithOffset(1, 0);
                    }
                } else {
                    playersAchievementsList.addAll(startInsertingPosition, resultData);
                    tableListAdapter.notifyItemRangeInserted(startInsertingPosition, resultData.size());
                }
                if (isInitialDataGetting) {
                    tableListAdapter.setPlayerPosition(playersAchievementsList.get(0).getPositionInTable());
                    if (dataBaseItemsCount <= maxRecyclerVisibleItemsCount) {
                        tableListAdapter.upEdgeAchieved();
                        tableListAdapter.downEdgeAchieved();
                    } else linearLayoutManager.scrollToPositionWithOffset(1, 0);
                }
                tableListAdapter.setLoadedState(false);
            }
        }
    }
}

class PlayersAchievements {
    private int positionInTable;
    private String userName;
    private int scores;
    private String gameTime;

    public PlayersAchievements(int positionInTable, String userName, int scores, String gameTime) {
        this.positionInTable = positionInTable;
        this.userName = userName;
        this.scores = scores;
        this.gameTime = gameTime;
    }

    public int getPositionInTable() {
        return positionInTable;
    }

    public String getUserName() {
        return userName;
    }

    public int getScores() {
        return scores;
    }

    public String getGameTime() {
        return gameTime;
    }
}

class TableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<PlayersAchievements> playersAchievementsList;
    private boolean isInLoadedState;
    private byte subValue;
    private boolean isUpEdge;
    private boolean isDownEdge;
    private int progressBarCount;
    private int playerPosition;

    public TableListAdapter(List<PlayersAchievements> playersAchievementsList) {
        this.playersAchievementsList = playersAchievementsList;
        initAdapter();
    }

    public boolean getIsRecyclerUpEdge() {
        return isUpEdge;
    }

    public boolean getIsRecyclerDownEdge() {
        return isDownEdge;
    }

    public void initAdapter() {
        playerPosition = -1;
        subValue = 1;
        isUpEdge = false;
        isDownEdge = false;
        progressBarCount = 2;//2 - two progressBars
    }

    public int getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = playerPosition;
    }

    public void upEdgeAchieved() {
        Log.v("upEdgeAchieved", "upEdgeAchieved");
        isUpEdge = true;
        subValue = 0;
        progressBarCount--;
        notifyDataSetChanged();

    }

    public void downEdgeAchieved() {
        isDownEdge = true;
        progressBarCount--;
        notifyDataSetChanged();
    }

    public boolean getLoadedState() {
        return isInLoadedState;
    }

    public void setLoadedState(boolean isInLoadedState) {
        this.isInLoadedState = isInLoadedState;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_item_layout, parent, false);
            return new ItemsViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_table_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            PlayersAchievements playersAchievements = playersAchievementsList.get(position - subValue);
            ItemsViewHolder itemsViewHolder = (ItemsViewHolder)holder;
            if (playersAchievements.getPositionInTable() == playerPosition) {
                itemsViewHolder.tableRow.setBackgroundColor(Color.parseColor("#ffb6c1"));
            } else {
                if (((position - subValue) & 1) == 0) {
                    itemsViewHolder.tableRow.setBackgroundColor(Color.parseColor("#ffffff"));
                } else itemsViewHolder.tableRow.setBackgroundColor(Color.parseColor("#f8f8ff"));
            }
            itemsViewHolder.firstColumn.setText(String.valueOf(playersAchievements.getPositionInTable()));
            itemsViewHolder.secondColumn.setText(playersAchievements.getUserName());
            itemsViewHolder.thirdColumn.setText(String.valueOf(playersAchievements.getScores()));
            itemsViewHolder.fourthColumn.setText(playersAchievements.getGameTime());
        } else if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position == 0 && !isUpEdge) || ((position == getItemCount() - 1) && !isDownEdge)) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return playersAchievementsList.size() == 0 ? -1 : playersAchievementsList.size() + progressBarCount;
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.gameTableProgressBar);
        }
    }

    class ItemsViewHolder extends RecyclerView.ViewHolder {
        private TextView firstColumn;
        private TextView secondColumn;
        private TextView thirdColumn;
        private TextView fourthColumn;
        private TableRow tableRow;
        public ItemsViewHolder(View itemView) {
            super(itemView);
            tableRow = (TableRow) itemView.findViewById(R.id.tableRowId);
            firstColumn = (TextView) itemView.findViewById(R.id.firstColumnText);
            secondColumn = (TextView) itemView.findViewById(R.id.secondColumnText);
            thirdColumn = (TextView) itemView.findViewById(R.id.thirdColumnText);
            fourthColumn = (TextView) itemView.findViewById(R.id.fourthColumnText);
        }
    }
}
