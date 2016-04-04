package com.bulkes.myapplication2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by progr on 31.03.2016.
 */
public class EndGameDialog extends Dialog {
    private Button agreeGameBtn;
    private Button disagreeGameBtn;
    AppCompatActivity activity;


    public EndGameDialog(Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        activity = (AppCompatActivity)context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    GoGaming.isDialogOpened = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.end_game_dialog);

        FrameLayout endGameLayout = (FrameLayout)findViewById(R.id.end_game_layout);
        if(endGameLayout == null)
            Log.v("endGameLayout", "null");

     //  endGameLayout.setBackground(frameDrawable);
        getWindow().getAttributes().windowAnimations = R.style.GameDialogAnimation;
        agreeGameBtn = (Button) findViewById(R.id.yes_btn);
        agreeGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoGaming.onFinish = true;
                activity.finish();
                cancel();
            }
        });
        disagreeGameBtn = (Button) findViewById(R.id.no_btn);
        disagreeGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoGaming.dialogStartGame(Settings.DialogEndID);
                cancel();
            }
        });
    }
}
