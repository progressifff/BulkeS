package com.bulkes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainMenuActivity extends AppCompatActivity{

    private boolean mContentLoaded;
    private View mContentView;
    private View mLoadingView;
    private CardView cardAbout;
    private CardView cardHelp;
    private int mShortAnimationDuration;
    private Animation anim;
    private Point size;
    private EditText nameUser;

    //File saving
    public static final String APP_SETTINGS = "mysettings";
    public static final String APP_SETTINGS_USER_NAME = "UserName";
    private SharedPreferences mSettings;

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
        CriticalData.scaling = (float)size.y / Settings.ScreenHeightDefault;

        setContentView(R.layout.activity_crossfade);
        cardAbout = (CardView)findViewById(R.id.card_about);
        cardHelp = (CardView)findViewById(R.id.card_help);
        cardAbout.setVisibility(View.INVISIBLE);
        cardHelp.setVisibility(View.INVISIBLE);

        nameUser = (EditText) findViewById(R.id.nameField);
        // nameUser.requestFocus();//for non started focus
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
                return false;
            }
        });

        //CriticalData
        findViewById(R.id.play_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CriticalData.createNewField();
                Intent intent = new Intent();
                intent.setClass(MainMenuActivity.this, GoGaming.class);
                MainMenuActivity.this.startActivityForResult(intent, 1);
            }
        });
        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                TranslateAnimation animation = new TranslateAnimation(cardHelp.getWidth(), 0, 0, 0);
                animation.setDuration(500);



                animation.setAnimationListener(new Animation.AnimationListener() {
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
        });
        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        });
    }


    @Override
    protected  void onResume()
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
    protected  void onActivityResult(int ReqCode, int ResCode, Intent data)
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