package com.bulkes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Point size;
    private EditText nameUser;
    private Intent intent;
    private Window window;

    private String      nickName;
    private int         userColorNum;
    private boolean     isBlackBG;

    private Date        date;
    private SimpleDateFormat dateFormat;



    private int         trainingScore;
    private long        trainingTime;

    private int         duelLevel;
    private int         duelScore;
    private long        duelTime;

    private int         survivalScore;
    private long        survivalTime;

    private ImageButton iButtonProgressTable;
    private ImageButton iButtonDiagram;
    private ImageButton iButtonSettings;
    private ImageButton iButtonAbout;
    //Game Type Button
    private ImageButton iButtonTraining;
    private ImageButton iButtonBattle;
    private ImageButton iButtonSurvival;

    //Table Result
    private TableLayout tableResult;
    private TableLayout tableHeader;

    //Main Logo
    private TextView    mainLogo;

    //User info
    private TextView    labelTime;
    private TextView    userTime;
    private TextView    labelPoint;
    private TextView    userPoint;

    //Training
    private TextView    trainingLabel;
    private TextView    trainingMaxScoreLabel;
    private TextView    trainingMaxScoreValue;
    private TextView    trainingTotalTimeLabel;
    private TextView    trainingTotalTimeValue;

    //Duel
    private TextView    duelLabel;
    private TextView    duelBestTimeLabel;
    private TextView    duelMaxPointLabel;
    private TextView    duelBestTimeValue;
    private TextView    duelMaxPointValue;

    //Survival
    private TextView    survivalLabel;
    private TextView    survivalBestTimeLabel;
    private TextView    survivalMaxPointLabel;
    private TextView    survivalBestTimeValue;
    private TextView    survivalMaxPointValue;


    //File saving
    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_USER_NAME = "UserName";
    public static final String APP_SETTINGS_USER_COLOR_NUM = "UserBulkColorNum";
    public static final String APP_SETTINGS_IS_BLACK_BG = "GameIsBlackField";

    public static final String APP_SETTINGS_TRAINING_MAX_SCORE = "TrainingMaxScore";
    public static final String APP_SETTINGS_TRAINING_TOTAL_TIME = "TrainingTotaltime";

    public static final String APP_SETTINGS_DUEL_LEVEL = "DuelLevel";
    public static final String APP_SETTINGS_DUEL_MAX_SCORE = "DuelMaxScore";
    public static final String APP_SETTINGS_DUEL_BEST_TIME = "DuelBestTime";

    public static final String APP_SETTINGS_SURVIVAL_MAX_SCORE = "SurvivalMaxScore";
    public static final String APP_SETTINGS_SURVIVAL_BEST_TIME = "SurvivalBestTime";

    private SharedPreferences mSettings;

    //Fonts
    static String fontRubik = "fonts/Rubik/Rubik-Regular.ttf";
    static String fontPhilosopher = "fonts/Philosopher/Philosopher-Regular.ttf";
    static String fontPassionOne = "fonts/Passion_One/PassionOne-Regular.ttf";
    static String fontMain = fontRubik;
    static String fontNumber = fontPhilosopher;
    static String fontLogo = fontPassionOne;


    TableRow getHeader()
    {
        Typeface typefaceLetter = Typeface.createFromAsset(getAssets(), fontMain);
        Typeface typefaceNumber = Typeface.createFromAsset(getAssets(), fontNumber);

        TableRow rowHeader = new TableRow(this);
        rowHeader.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView textViewN = new TextView(this);
        TextView textViewName = new TextView(this);
        TextView textViewTime = new TextView(this);
        TextView textViewPoint = new TextView(this);
        textViewN.setPadding(50,0,50,0);
        textViewName.setPadding(80,0,80,0);
        textViewTime.setPadding(50,0,50,0);
        textViewPoint.setPadding(50,0,50,0);

        textViewN.setText(R.string.tableN);
        textViewName.setText(R.string.tableName);
        textViewTime.setText(R.string.tableTime);
        textViewPoint.setText(R.string.tablePoint);

        textViewN.setTypeface(typefaceNumber);
        textViewName.setTypeface(typefaceLetter);
        textViewTime.setTypeface(typefaceNumber);
        textViewPoint.setTypeface(typefaceNumber);

        rowHeader.addView(textViewN, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewName, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewTime, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewPoint, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return rowHeader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        CriticalData.scaling = (float)size.y / Settings.ScreenHeightDefault;
        setContentView(R.layout.activity_crossfade);
        Typeface typefaceLetter = Typeface.createFromAsset(getAssets(), fontMain);
        Typeface typefaceNumber = Typeface.createFromAsset(getAssets(), fontNumber);
        Typeface typefaceLogo   = Typeface.createFromAsset(getAssets(), fontLogo);
        window = getWindow();
        date = new Date(0);
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //---------------------------------Determine UI changes-------------------------------############
        if(Build.VERSION.SDK_INT>=19 && deviceImmersiveSupport()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_IMMERSIVE) == 0) {
                        setFullScreenMode();
                    }
                }
            });
        }
        //-------------------------------------------------------------------------------------############
        mainLogo = (TextView) findViewById(R.id.mainLogo);
        mainLogo.setTypeface(typefaceLogo);
        mainLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        nameUser = (EditText) findViewById(R.id.nameField);
        nameUser.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (nameUser.getText().toString().length() != 0) {
                        SharedPreferences.Editor editor = mSettings.edit();
                        nickName = nameUser.getText().toString();
                        editor.putString(APP_SETTINGS_USER_NAME, nameUser.getText().toString());
                        editor.apply();
                    }
                    nameUser.clearFocus();
                }
                return false;
            }
        });

        iButtonTraining = (ImageButton)findViewById(R.id.iButtonTraining);
        iButtonBattle   = (ImageButton)findViewById(R.id.iButtonDuel);
        iButtonSurvival = (ImageButton)findViewById(R.id.iButtonSurvival);
        iButtonDiagram = (ImageButton)findViewById(R.id.iButtonDiagram);
        iButtonSettings = (ImageButton)findViewById(R.id.iButtonSettings);
        iButtonDiagram.setOnClickListener(this);
        iButtonSettings.setOnClickListener(this);
        iButtonTraining.setOnClickListener(this);
        iButtonBattle.setOnClickListener(this);
        iButtonSurvival.setOnClickListener(this);
//===================
        iButtonProgressTable = (ImageButton)findViewById(R.id.iButtonProgressTable);
        iButtonProgressTable.setOnClickListener(this);
        //==================

        //Training
        trainingLabel            = (TextView) findViewById(R.id.trainingLabel);
        trainingMaxScoreLabel    = (TextView) findViewById(R.id.trainingMaxScoreLabel);
        trainingMaxScoreValue    = (TextView) findViewById(R.id.trainingMaxScoreValue);
        trainingTotalTimeLabel   = (TextView) findViewById(R.id.trainingTotalTimeLabel);
        trainingTotalTimeValue   = (TextView) findViewById(R.id.trainingTotalTimeValue);

        trainingLabel.setTypeface(typefaceLetter);
        trainingMaxScoreLabel.setTypeface(typefaceLetter);
        trainingTotalTimeLabel.setTypeface(typefaceLetter);
        trainingMaxScoreValue.setTypeface(typefaceNumber);
        trainingTotalTimeValue.setTypeface(typefaceNumber);

        //Duel
        duelLabel           = (TextView) findViewById(R.id.duelLabel);
        duelBestTimeLabel   = (TextView) findViewById(R.id.duelBestTimeLabel);
        duelMaxPointLabel   = (TextView) findViewById(R.id.duelMaxPointLabel);
        duelBestTimeValue   = (TextView) findViewById(R.id.duelBestTimeValue);
        duelMaxPointValue   = (TextView) findViewById(R.id.duelMaxPointValue);

        duelLabel.setTypeface(typefaceLetter);
        duelBestTimeLabel.setTypeface(typefaceLetter);
        duelMaxPointLabel.setTypeface(typefaceLetter);

        duelBestTimeValue.setTypeface(typefaceNumber);
        duelMaxPointValue.setTypeface(typefaceNumber);

        //survival
        survivalLabel           = (TextView) findViewById(R.id.survivalLabel);
        survivalBestTimeLabel   = (TextView) findViewById(R.id.survivalBestTimeLabel);
        survivalMaxPointLabel   = (TextView) findViewById(R.id.survivalMaxPointLabel);
        survivalBestTimeValue   = (TextView) findViewById(R.id.survivalBestTimeValue);
        survivalMaxPointValue   = (TextView) findViewById(R.id.survivalMaxPointValue);

        survivalLabel.setTypeface(typefaceLetter);
        survivalBestTimeLabel.setTypeface(typefaceLetter);
        survivalMaxPointLabel.setTypeface(typefaceLetter);

        survivalBestTimeValue.setTypeface(typefaceNumber);
        survivalMaxPointValue.setTypeface(typefaceNumber);




        //user info
        labelTime   =  (TextView) findViewById(R.id.labelTime);
        userTime    =  (TextView) findViewById(R.id.userTime);
        labelPoint  =  (TextView) findViewById(R.id.labelPoint);
        userPoint   =  (TextView) findViewById(R.id.userPoint);

        labelTime.setTypeface(typefaceLetter);
        labelPoint.setTypeface(typefaceLetter);

        userTime.setTypeface(typefaceNumber);
        userPoint.setTypeface(typefaceNumber);


        //------------------------Read Settings From File------------------------#######
        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);

        nickName = (mSettings.contains(APP_SETTINGS_USER_NAME)? mSettings.getString(APP_SETTINGS_USER_NAME,"User Default"):"");
        nameUser.setText(nickName);

        userColorNum = (mSettings.contains(APP_SETTINGS_USER_COLOR_NUM)? mSettings.getInt(APP_SETTINGS_USER_COLOR_NUM,-1):1);
        Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];

        isBlackBG = (mSettings.contains(APP_SETTINGS_IS_BLACK_BG)? mSettings.getBoolean(APP_SETTINGS_IS_BLACK_BG,false):false);
        Settings.GameFieldColor = (isBlackBG==true?Color.BLACK:Color.WHITE);



        trainingScore = (mSettings.contains(APP_SETTINGS_TRAINING_MAX_SCORE)? mSettings.getInt(APP_SETTINGS_TRAINING_MAX_SCORE,-1):0);
        trainingMaxScoreValue.setText(String.valueOf(trainingScore));

        trainingTime = (mSettings.contains(APP_SETTINGS_TRAINING_TOTAL_TIME)? mSettings.getLong(APP_SETTINGS_TRAINING_TOTAL_TIME,-1):0);
        date.setTime(trainingTime);
        trainingTotalTimeValue.setText(dateFormat.format(date));

        duelLevel = (mSettings.contains(APP_SETTINGS_DUEL_LEVEL)? mSettings.getInt(APP_SETTINGS_DUEL_LEVEL,-1):1);
        duelBestTimeLabel.setText(String.valueOf(duelLevel));

        duelScore = (mSettings.contains(APP_SETTINGS_DUEL_MAX_SCORE)? mSettings.getInt(APP_SETTINGS_DUEL_MAX_SCORE,-1):0);
        duelMaxPointValue.setText(String.valueOf(duelScore));

        duelTime = (mSettings.contains(APP_SETTINGS_DUEL_BEST_TIME)? mSettings.getLong(APP_SETTINGS_DUEL_BEST_TIME,-1):0);
        date.setTime(duelTime);
        duelBestTimeValue.setText(dateFormat.format(date));

        survivalScore = (mSettings.contains(APP_SETTINGS_SURVIVAL_MAX_SCORE)? mSettings.getInt(APP_SETTINGS_SURVIVAL_MAX_SCORE,-1):0);
        survivalMaxPointValue.setText(String.valueOf(trainingScore));

        survivalTime = (mSettings.contains(APP_SETTINGS_SURVIVAL_BEST_TIME)? mSettings.getLong(APP_SETTINGS_SURVIVAL_BEST_TIME,-1):0);
        date.setTime(survivalTime);
        survivalBestTimeValue.setText(dateFormat.format(date));


        //-----------------------------------------------------------------------#######


        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GameTable.class);
                startActivityForResult(intent, 10);
            }
        });
    }

    private boolean deviceImmersiveSupport()
    {
        try{
            int id = getResources().getIdentifier("config_enableTranslucentDecor","bool","android");
            if(id == 0){return false;}
            else{
                boolean enable = getResources().getBoolean(id);
                return  enable;
            }
        }catch(Exception e){return false;}
    }

    private void setFullScreenMode() {
        if(Build.VERSION.SDK_INT < 19) {
            window.getDecorView().setSystemUiVisibility(View.GONE);
        } else if(deviceImmersiveSupport()) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setFullScreenMode();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected  void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        setFullScreenMode();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data)
    {
        SharedPreferences.Editor editor = mSettings.edit();
        //***************
        int resultDuelLevel;
        //****************
        int resultUserScore;
        long resultUserGameTime;
        switch (reqCode)
        {
            case 0:
                resultUserScore = data.getIntExtra(GoGaming.USER_SCORES,-1);
                resultUserGameTime = data.getLongExtra(GoGaming.USER_GAMETIME,-1);
                if(resultUserScore!=-1&&resultUserGameTime!=-1) {
                    trainingTime += resultUserGameTime;
                    date.setTime(trainingTime);
                    trainingTotalTimeValue.setText(dateFormat.format(date));
                    trainingScore = (resultUserScore>trainingScore?resultUserScore:trainingScore);
                    trainingMaxScoreValue.setText(String.valueOf(trainingScore));
                    editor.putInt(APP_SETTINGS_TRAINING_MAX_SCORE, trainingScore);
                    editor.putLong(APP_SETTINGS_TRAINING_TOTAL_TIME, trainingTime);
                    editor.apply();
                    iButtonDiagram.setVisibility(View.VISIBLE);
                }
                break;
            case 1:
                resultUserScore = data.getIntExtra(GoGaming.USER_SCORES,-1);
                resultUserGameTime = data.getLongExtra(GoGaming.USER_GAMETIME,-1);
                if(resultUserScore!=-1&&resultUserGameTime!=-1) {
                    duelTime = (resultUserGameTime<duelTime?resultUserGameTime:duelTime);
                    date.setTime(duelTime);
                    duelBestTimeValue.setText(dateFormat.format(date));
                    duelScore = (resultUserScore>duelScore?resultUserScore:duelScore);
                    duelMaxPointValue.setText(String.valueOf(duelScore));
                    editor.putInt(APP_SETTINGS_DUEL_MAX_SCORE, duelScore);
                    editor.putLong(APP_SETTINGS_DUEL_BEST_TIME, duelTime);
                    editor.apply();
                    iButtonDiagram.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                resultUserScore = data.getIntExtra(GoGaming.USER_SCORES,-1);
                resultUserGameTime = data.getLongExtra(GoGaming.USER_GAMETIME,-1);
                if(resultUserScore!=-1&&resultUserGameTime!=-1) {
                    survivalTime = (resultUserGameTime<survivalTime?resultUserGameTime:survivalTime);
                    date.setTime(survivalTime);
                    survivalBestTimeValue.setText(dateFormat.format(date));
                    survivalScore = (resultUserScore>survivalScore?resultUserScore:survivalScore);
                    duelMaxPointValue.setText(String.valueOf(survivalScore));
                    userPoint.setText(String.valueOf(survivalScore));
                    userTime.setText(dateFormat.format(date));
                    editor.putInt(APP_SETTINGS_SURVIVAL_MAX_SCORE, survivalScore);
                    editor.putLong(APP_SETTINGS_SURVIVAL_BEST_TIME, survivalTime);
                    editor.apply();
                    iButtonDiagram.setVisibility(View.VISIBLE);
                }
                break;
            case 3:// GraphActivity

                break;
            case 4:// GameSettingsActivity
                nickName = data.getStringExtra(GameSettings.NICKNAME);
                userColorNum = data.getIntExtra(GameSettings.USER_COLOR_NUM,1);
                isBlackBG = data.getBooleanExtra(GameSettings.BLACK_BG, false);
                if(nickName.length()!=0) {
                    editor.putString(APP_SETTINGS_USER_NAME, nickName);
                    nameUser.setText(nickName);
                }
                editor.putInt(APP_SETTINGS_USER_COLOR_NUM, userColorNum);
                editor.putBoolean(APP_SETTINGS_IS_BLACK_BG, isBlackBG);
                editor.apply();
                Settings.GameFieldColor = (isBlackBG==true?Color.BLACK : Color.WHITE);
                Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iButtonTraining:
                CriticalData.createTrainingField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.iButtonDuel:
                CriticalData.createBattleField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.iButtonSurvival:
                CriticalData.createSurvivalField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.iButtonDiagram:
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, ProgressGraph.class);
                startActivityForResult(intent, 3);
                break;
            case R.id.iButtonSettings:
                intent = new Intent();
                intent.putExtra(GameSettings.NICKNAME,nickName);
                intent.putExtra(GameSettings.USER_COLOR_NUM,userColorNum);
                intent.putExtra(GameSettings.BLACK_BG, isBlackBG);
                intent.setClass(MainMenuActivity.this, GameSettings.class);
                startActivityForResult(intent, 4);
                break;

            case R.id.iButtonProgressTable:
                Intent intent = new Intent();
                intent.putExtra(TestMySQL.USER_NAME, nickName);
                intent.putExtra(TestMySQL.GAME_SCORE, trainingScore);
                //===========================
                date.setTime(trainingTime);
                intent.putExtra(TestMySQL.GAMETIME, dateFormat.format(date));
                intent.setClass(MainMenuActivity.this, TestMySQL.class);
                startActivityForResult(intent,5);
                break;
            case R.id.iButtonAbout:

                break;



        }
    }
}