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
public class PauseGameDialog extends Dialog {
    private Button resumeGameBtn;
    AppCompatActivity activity;

    public PauseGameDialog(Context context) {
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
        setContentView(R.layout.pause_game_dialog);
        getWindow().getAttributes().windowAnimations = R.style.GameDialogAnimation;
        resumeGameBtn = (Button) findViewById(R.id.resume_btn);
        resumeGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoGaming.dialogStartGame(Settings.DialogPauseID);
                cancel();
            }
        });
    }
}
