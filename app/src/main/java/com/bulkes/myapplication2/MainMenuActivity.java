package com.bulkes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
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

    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_USER_NAME = "UserName";
    public static final String APP_SETTINGS_USER_COLOR_NUM = "UserBulkColorNum";
    public static final String APP_SETTINGS_IS_BLACK_BG = "GameIsBlackField";
    private SharedPreferences mSettings;
    private Button graphButton;

    private String nickName;
    private int userColorNum;
    private boolean isBlackBG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        CriticalData.scaling = (float)size.y / Settings.ScreenHeightDefault;
        setContentView(R.layout.activity_crossfade);
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
        nameUser = (EditText) findViewById(R.id.nameField);
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
        findViewById(R.id.play_btn).setOnClickListener(this);
        graphButton = (Button)findViewById(R.id.show_graph_btn);
        graphButton.setOnClickListener(this);
        graphButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.help_btn).setOnClickListener(this);
        findViewById(R.id.about_btn).setOnClickListener(this);
        findViewById(R.id.closeSettingsBtn).setOnClickListener(this);
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

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v("onBackPressed","onBackPressed");
    }
    */

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
            case 0:// GameActivity
                graphButton.setVisibility(View.VISIBLE);

                break;
            case 1:// GraphActivity

                break;
            case 2:// GameSettingsActivity
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
            case R.id.play_btn:
                CriticalData.createNewField();
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 0);
                break;
            case R.id.show_graph_btn:
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, ProgressGraph.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
                break;
            case R.id.closeSettingsBtn:
                intent = new Intent();
                intent.putExtra(GameSettings.NICKNAME,nickName);
                intent.putExtra(GameSettings.USER_COLOR_NUM,userColorNum);
                intent.putExtra(GameSettings.BLACK_BG, isBlackBG);
                intent.setClass(MainMenuActivity.this, GameSettings.class);
                MainMenuActivity.this.startActivityForResult(intent, 2);
                break;
            case R.id.help_btn:

                break;
            case R.id.about_btn:

                break;
        }
    }
}