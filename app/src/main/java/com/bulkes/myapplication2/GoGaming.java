package com.bulkes.myapplication2;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

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
        display.getSize(size);
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
    private float scaling;
    private Matrix matrix;
    private float downX;
    private float downY;
    private Paint paint;
    private boolean runFlag = false;
    private User user;
    private Bulk bulk;

    Indicator user_indicator;
    //%%%%%%%%%%%%%%%%%%%%%%%%%%
    private Boolean isTouch;
    private float begDownX;
    private float begDownY;
    private float deltaX, deltaY;
    private GameMap gameMap;
    //%%%%%%%%%%%%%%%%%%%%%%%%%
    static long currentTime;//action for pause
    static long startTime;

    private Timer timer;//timer for tick time and generate new food
    private TimerTasks timerTask;

    private Canvas canvas;

    public GameView(Context context,Point size) {
        super(context);
        ScreenWidth = size.x;
        ScreenHeight = size.y;
        paint = new Paint();
        runFlag = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);
        scaling = (float)ScreenHeight / Settings.ScreenHeightDefault;
        Log.v("Scale", String.valueOf(scaling));
        matrix = new Matrix();
        matrix.setScale(scaling, scaling);
        user = new User(ScreenWidth / 2 / scaling, ScreenHeight/2 / scaling, Settings.UserStartSize,Color.RED);
        gameMap = new GameMap();
        isTouch = false;
        deltaX = 0;
        deltaY = 0;
        timer = new Timer();
        timerTask = new TimerTasks();
        timer.schedule(timerTask, 0, 1000);
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
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
        while(runFlag)
        {
            canvas = null;
            try
            {
                canvas = Holder.lockCanvas();
                if(canvas!=null)
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
                        canvas.setMatrix(matrix);
                        Draw();
                    }
                }
            }
            finally
            {
                if(canvas != null)
                {
                    Holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void Draw()
    {
//------------------------Draw Field------------------------------------------------------------
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        drawMap();
        drawScores();
        drawUser();
        drawJoyStick();
    }

    private void drawJoyStick()
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
                if((downX - begDownX) != 0)
                {
                    k = (downY-begDownY)/(downX - begDownX);
                    deltaX = (float)Math.sqrt(Math.pow(joyStickRadiusOut,2)/(Math.pow(k,2)+1f));
                    deltaY = (float)Math.sqrt((Math.pow(joyStickRadiusOut,2) * Math.pow(k,2))/(1f + Math.pow(k,2)));
                }
                else
                {
                    deltaX = 0;
                    deltaY = joyStickRadiusOut;
                }
                deltaX = (downX < begDownX) ? (- deltaX) : (deltaX);
                deltaY = (downY < begDownY) ? (- deltaY) : (deltaY);
            }
            else
            {
                deltaX = downX-begDownX;
                deltaY = downY-begDownY;
            }
            stickX = begDownX + deltaX;
            stickY = begDownY + deltaY;
            canvas.drawCircle(stickX, stickY, joyStickRadiusIn, paint); // stick(small circle with Radius = 40) is less transparent
        }
    }

    private void drawUser()
    {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(user.getX(), user.getY(), user.getRadius(), paint);
        user_indicator = user.getIndicatorPosition(user.getX() + deltaX, user.getY() + deltaY);
        paint.setColor(Color.GRAY);
        paint.setAlpha(240);
        if(user.getIsMoved())
            canvas.drawPath(user.getTriangle(), paint);
    }

    private void drawMap()
    {
        Unit point;
        for(int i = 0; i < gameMap.getSize();i++)
        {
            point = gameMap.getMapUnit(i);
            if(point.is_deleted == false)
            {
                paint.setColor(point.color);
                if(user.isEated(point))
                {
                    user.addMass(((Food) point).getFeed());
                    point.setIsDeleted(true);
                }
                else
                {
                    if(user.getIsMoved())
                        point.move(-deltaX * user.getSpeed(), -deltaY * user.getSpeed());
                    canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), paint);
                }
            }
        }
    }

    private void drawScores()
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setAlpha(150);
        paint.setTextSize(52.0f);
        //Calendar calendar = Calendar.getInstance();
        //GameView.currentTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        Date temp_date = new Date();
        temp_date.setTime(currentTime - startTime);
        canvas.drawText(sdf.format(temp_date), 50f, 100f, paint);
        canvas.drawText(String.valueOf((int)user.mass / 10), Settings.ScreenWidthDefault - 250f, 100f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                user.setIsMoved(true);
                isTouch = true;
                downX = begDownX = event.getX() / scaling;
                downY =  begDownY = event.getY() / scaling;
                break;
            case MotionEvent.ACTION_MOVE:
                downX = event.getX() / scaling;
                downY = event.getY() / scaling;
                user.setIsMoved(true);
                if( Math.abs(downX - begDownX) < 1f && Math.abs(downY - begDownY) < 1f)
                {
                    Log.v("Action Up", "Pst=Pfn");
                    deltaX = 0f;
                    deltaY = 0f;
                    user.setIsMoved(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if(downX == begDownX && downY == begDownY)
                {
                    Log.v("Action Up", "Pst=Pfn");
                    deltaX = 0f;
                    deltaY = 0f;
                    user.setIsMoved(false);
                }
                //  bulk.setIsMoved(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}