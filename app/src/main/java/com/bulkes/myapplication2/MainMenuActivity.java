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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

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

    private int userId;
    private String      nickName;
    private int         userColorNum;
    private boolean     isBlackBG;

    private Date        date;
    private SimpleDateFormat dateFormat;

    private int maxScore;
    private long maxTime;

    private ImageButton iButtonProgressTable;
    private ImageButton iButtonDiagram;
    private ImageButton iButtonSettings;
    private ImageButton iButtonAbout;
    //Game Type Button
    private Button buttonTraining;
    private Button buttonSurvival;

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



    //File saving
    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_USER_NAME = "UserName";
    public static final String APP_SETTINGS_USER_COLOR_NUM = "UserBulkColorNum";
    public static final String APP_SETTINGS_IS_BLACK_BG = "GameIsBlackField";
    public static final String APP_SETTINGS_MAX_SCORE = "MaxScore";
    public static final String APP_SETTINGS_MAX_TIME = "MaxTime";
    public static final String APP_SETTINGS_USER_ID = "UserID";

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
                CriticalData.createRunField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 1);
            }
        });

        iButtonProgressTable = (ImageButton) findViewById(R.id.iButtonProgressTable);
        buttonTraining = (Button) findViewById(R.id.buttonTraining);
        buttonSurvival = (Button) findViewById(R.id.buttonSurvival);
        iButtonDiagram = (ImageButton) findViewById(R.id.iButtonDiagram);
        iButtonAbout = (ImageButton) findViewById(R.id.iButtonAbout);
        iButtonSettings = (ImageButton) findViewById(R.id.iButtonSettings);
        iButtonProgressTable.setOnClickListener(this);
        iButtonDiagram.setOnClickListener(this);
        iButtonSettings.setOnClickListener(this);
        iButtonAbout.setOnClickListener(this);
        buttonTraining.setOnClickListener(this);
        buttonSurvival.setOnClickListener(this);
        final View.OnLongClickListener toolTip = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String info = new String();
                switch (v.getId()) {
                    case R.id.iButtonAbout:
                        info = getResources().getString(R.string.tooltipInfo);
                        break;
                    case R.id.iButtonSettings:
                        info = getResources().getString(R.string.tooltipSetting);
                        break;
                    case R.id.iButtonDiagram:
                        info = getResources().getString(R.string.tooltipDiagram);
                        break;
                    case R.id.iButtonProgressTable:
                        info = getResources().getString(R.string.tooltipTable);
                        break;
                }
                Toast.makeText(v.getContext(), info, Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        iButtonSettings.setOnLongClickListener(toolTip);
        iButtonAbout.setOnLongClickListener(toolTip);
        iButtonProgressTable.setOnLongClickListener(toolTip);
        iButtonDiagram.setOnLongClickListener(toolTip);

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

        nickName = mSettings.getString(APP_SETTINGS_USER_NAME, "Player1");

        userColorNum = mSettings.getInt(APP_SETTINGS_USER_COLOR_NUM, 1);
        Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];

        isBlackBG = mSettings.getBoolean(APP_SETTINGS_IS_BLACK_BG, false);
        Settings.GameFieldColor = (isBlackBG==true?Color.BLACK:Color.WHITE);

        userId = mSettings.getInt(APP_SETTINGS_USER_ID, 0);

        maxScore = (mSettings.contains(APP_SETTINGS_MAX_SCORE) ? mSettings.getInt(APP_SETTINGS_MAX_SCORE, -1) : 0);
        userPoint.setText(String.valueOf(maxScore));

        maxTime = (mSettings.contains(APP_SETTINGS_MAX_TIME) ? mSettings.getLong(APP_SETTINGS_MAX_TIME, -1) : 0);
        date.setTime(maxTime);
        userTime.setText(dateFormat.format(date));
        //-----------------------------------------------------------------------#######
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
        switch (reqCode)
        {
            case 1:// Survival
                int currentScore = data.getIntExtra(GoGaming.USER_SCORES, -1);
                long currentTime = data.getLongExtra(GoGaming.USER_GAMETIME, -1);
                if (currentScore > maxScore) {
                    maxScore = currentScore;
                    maxTime = currentTime;
                    date.setTime(maxTime);
                    userPoint.setText(String.valueOf(maxScore));
                    userTime.setText(dateFormat.format(date));
                    editor.putInt(APP_SETTINGS_MAX_SCORE, maxScore);
                    editor.putLong(APP_SETTINGS_MAX_TIME, maxTime);
                    editor.apply();
                }
                iButtonDiagram.setVisibility(View.VISIBLE);
                break;
            case 2:// GraphActivity
                break;
            case 3:// GameSettingsActivity
                nickName = data.getStringExtra(GameSettings.NICKNAME);
                userColorNum = data.getIntExtra(GameSettings.USER_COLOR_NUM,1);
                isBlackBG = data.getBooleanExtra(GameSettings.BLACK_BG, false);
                if(nickName.length()!=0) {
                    editor.putString(APP_SETTINGS_USER_NAME, nickName);
                    // nameUser.setText(nickName);
                }
                editor.putInt(APP_SETTINGS_USER_COLOR_NUM, userColorNum);
                editor.putBoolean(APP_SETTINGS_IS_BLACK_BG, isBlackBG);
                editor.apply();
                Settings.GameFieldColor = (isBlackBG==true?Color.BLACK : Color.WHITE);
                Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];
                break;
            case 4:// GameTableActivity
                userId = data.getIntExtra(GameTable.ID, 0);
                editor.putInt(APP_SETTINGS_USER_ID, userId);
                editor.apply();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buttonTraining:
                CriticalData.createTrainingField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.buttonSurvival:
                CriticalData.createSurvivalField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.iButtonDiagram:
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, ProgressGraph.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.iButtonSettings:
                intent = new Intent();
                intent.putExtra(GameSettings.NICKNAME,nickName);
                intent.putExtra(GameSettings.USER_COLOR_NUM,userColorNum);
                intent.putExtra(GameSettings.BLACK_BG, isBlackBG);
                intent.setClass(MainMenuActivity.this, GameSettings.class);
                startActivityForResult(intent, 3);
                break;
            case R.id.iButtonProgressTable:
                intent = new Intent();
                intent.putExtra(GameTable.ID, userId);
                intent.putExtra(GameTable.USER_NAME, nickName);
                intent.putExtra(GameTable.USERS_SCORES, maxScore);
                date.setTime(maxTime);
                intent.putExtra(GameTable.GAME_TIME, dateFormat.format(date));
                intent.setClass(MainMenuActivity.this, GameTable.class);
                startActivityForResult(intent, 4);
                break;
            case R.id.iButtonAbout:
                AboutDialog aboutDialog = new AboutDialog(this);
                aboutDialog.show();
                aboutDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                break;
        }
    }



}