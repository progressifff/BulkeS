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
    Indicator user_indicator;
    //%%%%%%%%%%%%%%%%%%%%%%%%%%
    private Boolean isTouch;
    private float begDownX;
    private float begDownY;
    private GameMap gameMap;
    private JoyStick stick;
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
        scaling = (float)ScreenHeight / Settings.ScreenHeightDefault;
        matrix = new Matrix();
        matrix.setScale(scaling, scaling);
        user = new User(ScreenWidth / 2 / scaling, ScreenHeight/2 / scaling, Settings.StartSizeUser,Color.RED);
        stick = new JoyStick(120/scaling,60/scaling);
        gameMap = new GameMap(3,3);

        gameMap.setRelativeUnit(ScreenWidth / 2 / scaling, ScreenHeight / 2 / scaling);

     //   gameMap.setMapAxis(ScreenWidth, ScreenHeight);
        isTouch = false;
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
        while(runFlag)
        {
            canva = null;
            try
            {
                canva = Holder.lockCanvas();
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
                        canva.setMatrix(matrix);
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
        drawMap(canvas);
        //drawScores(canvas);
        drawUser(canvas);
        drawJoyStick(canvas);
    }

    private void drawJoyStick(Canvas canvas)
    {
        if(isTouch)
        {
            stick.getParameters(begDownX,begDownY,downX,downY);
            paint.setColor(Color.GRAY);
            paint.setAlpha(125);
            canvas.drawCircle(stick.getX0(), stick.getY0(), stick.getRadiusOut(), paint);
            paint.setAlpha(240);
            canvas.drawCircle(stick.getX(), stick.getY(), stick.getRadiusIn(), paint);
        }
    }

    private void drawUser(Canvas canvas)
    {
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(user.getX(), user.getY(), user.getRadius(), paint);
        user_indicator = user.getIndicatorPosition(user.getX() + stick.getdX(), user.getY() + stick.getdY());
        paint.setColor(Color.GRAY);
        paint.setAlpha(240);
        canvas.drawPath(user.getTriangle(), paint);
    //    gameMap.moveRelativeUnit(-stick.getdX() * user.getSpeed(), -stick.getdY() * user.getSpeed());
     //   Log.v("FDFDFSDFDS", String.valueOf(gameMap.getY0()));
    }

    private void drawMap(Canvas canvas)
    {
        Unit point;
        float X = 0, Y = 0;
        Boolean turn = false;
        gameMap.changeMapOfs(-stick.getdX() * user.getSpeed(), -stick.getdY() * user.getSpeed());
        if(gameMap.getY0() <= -gameMap.getMapSize().k*ScreenHeight)
        {
            Y = gameMap.getY0();
            gameMap.setY0(0);
            turn = true;
        }
        else if(gameMap.getY0()>= 0)
        {
            Y = gameMap.getMapSize().k*ScreenHeight;
            turn = true;
            gameMap.setY0(-gameMap.getMapSize().k * ScreenHeight);
        }
        if(gameMap.getX0() <= -gameMap.getMapSize().m*ScreenWidth)
        {
            X = gameMap.getX0();
            turn = true;
            gameMap.setX0(0);
        }
        else if(gameMap.getX0()>= 0)
        {
           // Log.v("DDSDSSDD", String.valueOf(gameMap.getX0()));
            X = (gameMap.getMapSize().m) * ScreenWidth;
            turn = true;
            gameMap.setX0(-gameMap.getMapSize().m * ScreenWidth);
        }
        for(int i = 0; i < gameMap.getUnitsCount();i++)
        {
            point = gameMap.getMapUnit(i);
            paint.setColor(point.color);
            //  if(user.isOverlapped(point) || Math.abs(user.getX() - point.getY())>= 4*Settings.ScreenWidthDefault  / Settings.CountSectorX / scaling
            //          || Math.abs(user.getY() - point.getY()) >= 4*Settings.ScreenHeightDefault  / Settings.CountSectorY /scaling)
            if(user.isOverlapped(point))
                gameMap.removeUnit(i);
            if(!turn)
                point.move(-stick.getdX() * user.getSpeed(),-stick.getdY() * user.getSpeed());
            else
            {
                point.setX(point.getX() - X);
                point.setY(point.getY() - Y);
            }
            canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), paint);
        }
    }

    private void drawScores(Canvas canvas)
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setTextSize(52.0f);
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawText("12:55", 35f, 35f, paint);
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
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}