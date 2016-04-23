package com.bulkes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity {

    private boolean mContentLoaded;
    private View mContentView;
    private View mLoadingView;
    private CardView cardAbout;
    private CardView cardHelp;
    private int mShortAnimationDuration;
    private Animation anim;
    private Point size;
    private EditText nameUser;

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

        //textViewN.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        textViewN.setText(R.string.tableN);
        textViewName.setText(R.string.tableName);
        textViewTime.setText(R.string.tableTime);
        textViewPoint.setText(R.string.tablePoint);

        textViewN.setTypeface(typefaceNumber);
        textViewName.setTypeface(typefaceLetter);
        textViewTime.setTypeface(typefaceNumber);
        textViewPoint.setTypeface(typefaceNumber);
        /*rowHeader.addView(textViewN);
        rowHeader.addView(textViewName);
        rowHeader.addView(textViewTime);
        rowHeader.addView(textViewPoint);
        */
        rowHeader.addView(textViewN, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewName, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewTime, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        rowHeader.addView(textViewPoint, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return rowHeader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                /*| View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                */
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    // Removes notification bar

        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        CriticalData.scaling = (float) size.y / Settings.ScreenHeightDefault;

        setContentView(R.layout.activity_crossfade);
        //    cardAbout = (CardView)findViewById(R.id.card_about);
        //     cardHelp = (CardView)findViewById(R.id.card_help);
//        cardAbout.setVisibility(View.INVISIBLE);
        //       cardHelp.setVisibility(View.INVISIBLE);

        Typeface typefaceLetter = Typeface.createFromAsset(getAssets(), fontMain);
        Typeface typefaceNumber = Typeface.createFromAsset(getAssets(), fontNumber);
        Typeface typefaceLogo   = Typeface.createFromAsset(getAssets(), fontLogo);

        mainLogo = (TextView) findViewById(R.id.mainLogo);
        mainLogo.setTypeface(typefaceLogo);
        mainLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        nameUser = (EditText) findViewById(R.id.nameField);
        // nameUser.requestFocus();//for non started focus
        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_SETTINGS_USER_NAME)) {
            nameUser.setText(mSettings.getString(APP_SETTINGS_USER_NAME, "User Default"));
        }
        nameUser.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(APP_SETTINGS_USER_NAME, nameUser.getText().toString());
                editor.apply();
                Log.v("Setting ", nameUser.getText().toString());
                return false;
            }
        });

        iButtonTraining = (ImageButton)findViewById(R.id.iButtonTraining);
        iButtonBattle   = (ImageButton)findViewById(R.id.iButtonDuel);
        iButtonSurvival = (ImageButton)findViewById(R.id.iButtonSurvival);


        iButtonTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CriticalData.createTrainingField();
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
            }
        });

        iButtonBattle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CriticalData.createBattleField();
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
            }
        });

        iButtonSurvival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CriticalData.createSurvivalField();
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
            }
        });

        /*
        //table creating fields
        tableResult = (TableLayout) findViewById(R.id.tableResult);
        tableHeader = (TableLayout) findViewById(R.id.tableHeader);

        //TableRow rowHidden = (TableRow) findViewById(R.id.headerHidden);
        tableResult.removeAllViews();

        tableHeader.addView(getHeader());
        TableRow templateRow = getHeader();
        templateRow.setLayoutParams(new TableRow.LayoutParams(
                0,
                0));
       // TableRow headRow = getHeader();
        //tableResult.removeView(headRow);
        tableResult.addView(templateRow);

        for(int i = 0; i < 150; ++i) {
            TableRow tableRow = new TableRow(this);
            //tableRow.setMinimumHeight(50);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView textViewN = new TextView(this);
            TextView textViewName = new TextView(this);
            TextView textViewTime = new TextView(this);
            TextView textViewPoint = new TextView(this);

            //textViewN.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            textViewN.setText(String.valueOf(i * 10 + 1) + " " + String.valueOf(i * 10 + 1));
            textViewName.setText("Vladislav");
            textViewTime.setText("12:23:11");
            textViewPoint.setText("50432");

            textViewN.setTypeface(typefaceNumber);
            textViewName.setTypeface(typefaceLetter);
            textViewTime.setTypeface(typefaceNumber);
            textViewPoint.setTypeface(typefaceNumber);


            textViewN.setGravity(Gravity.CENTER);
            textViewName.setGravity(Gravity.CENTER);
            textViewTime.setGravity(Gravity.CENTER);
            textViewPoint.setGravity(Gravity.CENTER);


           // textViewN.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //textViewName.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            //textViewTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //textViewPoint.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


            tableRow.addView(textViewN, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.addView(textViewName, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.addView(textViewTime, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tableRow.addView(textViewPoint, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            tableResult.addView(tableRow);
        }
        */

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

        //CriticalData
/*        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Settings.UserScale = 1f;
                CriticalData.createNewField();
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
            }
        });
        */
        //findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
        //  @Override
        //public void onClick(View v) {

              /*  cardAbout.animate()
                        .alpha(0f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cardAbout.setVisibility(View.INVISIBLE);     }
                        });
*/

         /*       if(cardAbout.getVisibility()==View.VISIBLE)
                    cardAbout.setVisibility(View.INVISIBLE);
*/
        // TranslateAnimation animation = new TranslateAnimation(cardHelp.getWidth(), 0, 0, 0);
        // animation.setDuration(500);



               /* animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        cardAbout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cardHelp.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animation.setFillBefore(true);
                cardHelp.startAnimation(animation);
                //  cardHelp.setVisibility(View.VISIBLE);

                //  cardAbout.setVisibility(View.INVISIBLE);
            }
        });*/
        /*
        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
*/
           /*     cardHelp.animate()
                        .alpha(0f)
                        .setDuration(mShortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                cardHelp.setVisibility(View.INVISIBLE);     }
                        });
*/
               /* if(cardHelp.getVisibility()==View.VISIBLE)
                    cardHelp.setVisibility(View.INVISIBLE);
                    */
        /*
                TranslateAnimation animation = new TranslateAnimation(cardAbout.getWidth(),0,0,0);
                animation.setDuration(500);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        cardHelp.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        cardAbout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                animation.setFillBefore(true);
                cardAbout.startAnimation(animation);

            }
        });*/
    }


        @Override
        protected void onResume ()
        {
            super.onResume();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
               /* | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                */
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }


        @Override
        protected void onActivityResult ( int ReqCode, int ResCode, Intent data)
        {

        }



  /*  private void showContentOrLoadingIndicator(boolean contentLoaded)
    {
        final View showView = contentLoaded ? mContentView : mLoadingView;
        final View hideView = contentLoaded ? mLoadingView : mContentView;
        showView.setAlpha(0f);
        showView.setVisibility(View.VISIBLE);
        showView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);
        hideView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hideView.setVisibility(View.GONE);
                    }
                });
    }
    */
    }
