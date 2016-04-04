package com.bulkes.myapplication2;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;


/**
 * Created by progr on 31.03.2016.
 */
public class GameOverDialog extends Dialog {
    private Button closeGameBtn;
    private Button repeatGameBtn;
    AppCompatActivity activity;

    public GameOverDialog(Context context) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        activity = (AppCompatActivity)context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   GoGaming.isDialogOpened = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_over_layout);
        getWindow().getAttributes().windowAnimations = R.style.GameDialogAnimation;
        closeGameBtn = (Button) findViewById(R.id.close_btn);
        closeGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                GoGaming.onFinish = true;
                activity.finish();
            }
        });
        repeatGameBtn = (Button) findViewById(R.id.repeate_btn);
        repeatGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoGaming.dialogStartGame(Settings.DialogGameOverID);
                cancel();
            }
        });
    }
}
