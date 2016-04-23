package com.bulkes.myapplication2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by progr on 23.04.2016.
 */
public class FocusableImageButton extends ImageButton {
    private boolean touchFlag;
    private Rect buttonRect;
    public FocusableImageButton(Context context) {
        super(context);
        buttonRect = new Rect();
        touchFlag = false;
    }
    public FocusableImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        buttonRect = new Rect();
        touchFlag = false;
    }

    public FocusableImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        buttonRect = new Rect();
        touchFlag = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                buttonRect.set(this.getLeft(), this.getTop(), this.getRight(), this.getBottom());
                this.setColorFilter(Color.rgb(138,149,151), PorterDuff.Mode.MULTIPLY);
                touchFlag = true;
                Log.v("ACTION_DOWN","ACTION_DOWN");
                break;
            case MotionEvent.ACTION_UP:
                if(touchFlag) {
                    this.clearColorFilter();
                    touchFlag = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_MOVE:
                if (!buttonRect.contains(this.getLeft() + (int) event.getX(), this.getTop() + (int) event.getY())&&touchFlag) {
                    this.clearColorFilter();
                    touchFlag = false;
                    break;
                }
        }
        return super.onTouchEvent(event);
    }
}