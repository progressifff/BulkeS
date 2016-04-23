package com.bulkes.myapplication2;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

/**
 * Created by progr on 23.04.2016.
 */
public class GameEditText extends EditText{
    public GameEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameEditText(Context context) {
        super(context);
    }

    public GameEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.clearFocus();
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT){
                 setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
