package com.bulkes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.OutputStream;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener{
    private Point size;
    private EditText nameUser;
    private Intent intent;

    private OutputStream outputStream;
    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_USER_NAME = "UserName";
    private SharedPreferences mSettings;

    private Button graphButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode();
        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        CriticalData.scaling = (float)size.y / Settings.ScreenHeightDefault;
        setContentView(R.layout.activity_crossfade);

        nameUser = (EditText) findViewById(R.id.nameField);
        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_SETTINGS_USER_NAME)) {
            nameUser.setText( mSettings.getString(APP_SETTINGS_USER_NAME, "User Default"));
        }
        nameUser.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_SETTINGS_USER_NAME, nameUser.getText().toString());
                editor.apply();
                Log.v("Setting ", nameUser.getText().toString());
                setFullScreenMode();
                return false;
            }
        });

        findViewById(R.id.play_btn).setOnClickListener(this);
        graphButton = (Button)findViewById(R.id.show_graph_btn);
        graphButton.setOnClickListener(this);
        graphButton.setVisibility(View.INVISIBLE);
        findViewById(R.id.help_btn).setOnClickListener(this);
        findViewById(R.id.about_btn).setOnClickListener(this);
    }

    private void setFullScreenMode()
    {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        setFullScreenMode();
    }

    @Override
    protected void onActivityResult(int ReqCode, int ResCode, Intent data)
    {
        if(ReqCode == 1)
        {
            graphButton.setVisibility(View.VISIBLE);
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
                MainMenuActivity.this.startActivityForResult(intent, 1);
                break;
            case R.id.show_graph_btn:
                intent = new Intent();
                intent.setClass(MainMenuActivity.this, ProgressGraph.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
                break;
            case R.id.help_btn:

                break;
            case R.id.about_btn:

                break;
        }
    }
}