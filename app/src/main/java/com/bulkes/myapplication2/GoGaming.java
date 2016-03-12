package com.bulkes.myapplication2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by progr on 08.03.2016.
 */
public class GoGaming extends AppCompatActivity {
    private Display display;
    private Point size;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getRealSize(size);
        setContentView(new GameView(this, size));
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}

class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable
{
    private SurfaceHolder Holder;
    private int ScreenWidth;
    private int ScreenHeight;
    private float downX;
    private float downY;
    private Paint paint;
    private boolean runFlag = false;
    private Bulk bulk;
    //%%%%%%%%%%%%%%%%%%%%%%%%%%
    private Boolean isTouch;
    private float begDownX;
    private float begDownY;
    float deltaX, deltaY;
    //%%%%%%%%%%%%%%%%%%%%%%%%%

    public GameView(Context context,Point size) {
        super(context);
        ScreenWidth = size.x;
        ScreenHeight = size.y;
        paint = new Paint();
        runFlag = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);


        isTouch = false;
        deltaX = 0;
        deltaY = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        onFinishInflate();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        runFlag = false;
    }

    @Override
    public void run()
    {
        Canvas canva;
        Matrix matrix = new Matrix();
        Log.v("Size(w h)", String.valueOf(ScreenWidth) + " " + String.valueOf(ScreenHeight));
        float scaling = ScreenHeight / 1080f;
        bulk = new Bulk(ScreenWidth/2 / scaling,ScreenHeight/2 / scaling,(float)150.6,Color.RED);
        matrix.setScale(   scaling, scaling);
        while(runFlag)
        {
            canva = null;
            try
            {
                canva = Holder.lockCanvas();
                canva.setMatrix(matrix);
                if(canva!=null)
                {
                    try {
                        Thread.sleep(5);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    synchronized (Holder)
                    {
                        Draw(canva);
                    }
                }
            }
            finally
            {
                if(canva != null)
                {
                    Holder.unlockCanvasAndPost(canva);
                }
            }
        }
    }

    public void Draw(Canvas canvas)
    {
//------------------------Draw Field------------------------------------------------------------
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bulk.X(), bulk.Y(), bulk.Radius(), paint);
        //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        drawBulk(canvas);
        drawJoyStick(canvas);
    }

    public void drawJoyStick(Canvas canvas)
    {
        float stickX;
        float stickY;
        float joyStickRadiusOut = 120;
        float joyStickRadiusIn = 60;
        float k;
        if(isTouch)
        {
            paint.setColor(Color.GRAY);
            paint.setAlpha(125);
            canvas.drawCircle(begDownX, begDownY, joyStickRadiusOut, paint); // foundation (big circle with Radius = 100) is more transparent
            paint.setAlpha(240);
            if(Math.sqrt(Math.pow((downX - begDownX), 2) + Math.pow((downY - begDownY), 2)) > joyStickRadiusOut)
            {
                k = (downY-begDownY)/(downX - begDownX);
                deltaX = (float)Math.sqrt(Math.pow(joyStickRadiusOut,2)/(Math.pow(k,2)+1f));
                deltaY = (float)Math.sqrt((Math.pow(joyStickRadiusOut,2) * Math.pow(k,2))/(1f + Math.pow(k,2)));
            }
            else
            {
                deltaX = Math.abs(downX-begDownX);
                deltaY = Math.abs(downY-begDownY);
            }
            stickX = (downX < begDownX) ? (begDownX - deltaX) : (begDownX + deltaX);
            stickY = (downY < begDownY) ? (begDownY - deltaY) : (begDownY + deltaY);
            canvas.drawCircle(stickX, stickY, joyStickRadiusIn, paint); // stick(small circle with Radius = 40) is less transparent
        }
    }

    public void drawBulk(Canvas canvas)
    {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        if(bulk.getIsMoved())
        {
            if (((bulk.X() - bulk.Radius() - deltaX) < 0 || (bulk.X() + bulk.Radius() + deltaX) > 1920.0f) || ((bulk.Y() - bulk.Radius() - deltaY) < 0 || (bulk.Y() + bulk.Radius() + deltaY) > 1080.0f)) {
                return;
            }
        //    Log.v("GoGaming", String.valueOf(deltaX * (float) 0.1));
            //Здесь сделал проверку на не число NAN. В определенное время Бульк пропадает. В логе показывает NAN. Читал про NAN, оно возникает, при делении на ноль или корня кв. от отрицательного числа.
            if (!Float.isNaN(((downX < begDownX) ? (-deltaX) : (deltaX)) * 0.1f) && !Float.isNaN(((downY < begDownY) ? (-deltaY) : (deltaY)) * 0.1f))
                bulk.Move(((downX < begDownX) ? (-deltaX) : (deltaX)) * 0.1f, ((downY < begDownY) ? (-deltaY) : (deltaY)) * 0.1f);
            canvas.drawCircle(bulk.X(), bulk.Y(), bulk.Radius(), paint);
        }
//-------Move Bulk to point of touch
        /*
        float k;
        float x;
        float y;
        if(bulk.getIsMoved() == true)
        {
            k = (downY-bulk.Y())/(downX - bulk.X());
            if(!Float.isNaN(k))
            {
            //Log.v("GoGaming", String.valueOf(k));
                if(Math.abs(downX - bulk.X()) > Math.abs(downY - bulk.Y())) {
                    x = bulk.getSpeed();
                    y = k * (bulk.X() + x * bulk.getXDirection()) - k * bulk.X() + bulk.Y();
                    bulk.Move(x * bulk.getXDirection(), y - bulk.Y());
                }
                else if (Math.abs(downY - bulk.Y()) > Math.abs(downX - bulk.X()))
                {
                    y = bulk.getSpeed();
                    x = (bulk.Y() +  y* bulk.getYDirection() + k*bulk.X() - bulk.Y())/k;
                    bulk.Move(x - bulk.X(), y* bulk.getYDirection());
                }
            }
        }
        */
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                bulk.setIsMoved(true);
                isTouch = true;
                downX = begDownX = event.getX();
                downY =  begDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                downX = event.getX();
                downY = event.getY();
                /*if(Math.sqrt(Math.pow(event.getX() - bulk.X(), 2) + Math.pow(event.getY() - bulk.Y(), 2))<=bulk.Radius()/2)
                    bulk.setIsMoved(false);
                else
                {
                    bulk.setIsMoved(true);
                    downX = event.getX();
                    downY = event.getY();
                    checkDirection(downX, downY);
                }
                */
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
              //  bulk.setIsMoved(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
// Direction of Bulk
    private void checkDirection(float downX, float downY)
    {
        if(downX > bulk.X())
            bulk.setXDirection(1);
        else if(downX < bulk.X())
            bulk.setXDirection(-1);
        if(downY > bulk.Y())
            bulk.setYDirection(1);
        else if(downY < bulk.Y())
            bulk.setYDirection(-1);
    }
}