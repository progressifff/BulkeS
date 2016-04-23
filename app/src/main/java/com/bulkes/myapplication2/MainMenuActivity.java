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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Point size;
    private EditText nameUser;
    private Intent intent;
    private Window window;


    private Button graphButton;

    private String nickName;
    private int userColorNum;
    private boolean isBlackBG;

    private ImageButton iButtonSettings;
    private ImageButton iButtonAbout;

    private ImageButton iButtonDiagram;

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
        //------------------------Read Settings From File------------------------#######
        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_SETTINGS_USER_NAME)) {
            nickName = mSettings.getString(APP_SETTINGS_USER_NAME, "User Default");
            nameUser.setText(nickName);
        }
        else nickName = "";
        if (mSettings.contains(APP_SETTINGS_USER_COLOR_NUM)) {
            userColorNum = mSettings.getInt(APP_SETTINGS_USER_COLOR_NUM,1);
            Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];
        }
        else{
            userColorNum = 1;
            Settings.UserDefaultColor = Settings.UsersBulkColors[userColorNum-1];
        }
        if (mSettings.contains(APP_SETTINGS_IS_BLACK_BG)) {
            isBlackBG = mSettings.getBoolean(APP_SETTINGS_IS_BLACK_BG,false);
            Settings.GameFieldColor = (isBlackBG==true?Color.BLACK:Color.WHITE);
        }
        else{
            isBlackBG = false;
            Settings.GameFieldColor = Color.WHITE;
        }
        //-----------------------------------------------------------------------#######

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
            case 0:
            case 1:
            case 2:
                iButtonDiagram.setVisibility(View.VISIBLE);
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
                MainMenuActivity.this.startActivityForResult(intent, 0);
                break;
            case R.id.iButtonDuel:
                CriticalData.createBattleField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
                break;
            case R.id.iButtonSurvival:
                CriticalData.createSurvivalField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 2);
                break;
            case R.id.iButtonDiagram:
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, ProgressGraph.class);
                MainMenuActivity.this.startActivityForResult(intent, 3);
                break;
            case R.id.iButtonSettings:
                intent = new Intent();
                intent.putExtra(GameSettings.NICKNAME,nickName);
                intent.putExtra(GameSettings.USER_COLOR_NUM,userColorNum);
                intent.putExtra(GameSettings.BLACK_BG, isBlackBG);
                intent.setClass(MainMenuActivity.this, GameSettings.class);
                MainMenuActivity.this.startActivityForResult(intent, 4);
                break;

            case R.id.iButtonAbout:

                break;

        }
    }
}