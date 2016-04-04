package com.bulkes.myapplication2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Timer;

/**
 * Created by progr on 08.03.2016.
 */
public class GoGaming extends AppCompatActivity {
    private GameView gameView;
    private static boolean isPause;
    private boolean onFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onFinish = false;
        int orientation=this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!isPause) {
                gameView = new GameView(this);
                setContentView(gameView);
                gameView.pauseGame(false);
                Log.v("onCreate", "onCreate");
            }
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume()
    {
        //  Log.v("onResume", "onResume");
        super.onResume();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setFullScreenMode();
        if(isPause) {
            gameView = new GameView(this);
            setContentView(gameView);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Game is on pause")
                    .setCancelable(false)
                    .setPositiveButton("Start",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    gameView.pauseGame(false);
                                    isPause = false;
                                    dialog.cancel();
                                    gameView.setStartTime();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void setFullScreenMode()
    {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
       /*         | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public void onPause()
    {
        isPause = true;
        super.onPause();
        Log.v("onPause", "onPause");
    }

    @Override
    public void onStop()
    {
        if(onFinish)
            isPause = false;
        super.onStop();
        Log.v("onStop", "onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (gameView!=null)
        {
            CriticalData.lastTime += gameView.getLastTime();
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            gameView.pauseGame(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Would you like to finish the game?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    onFinish = true;
                                    finish();
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gameView.pauseGame(false);
                            setFullScreenMode();
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
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
    private ArrayList<Bulk> bulkesMap;
    private SectorHolder    sectors;
    private Boolean isTouch;
    private float begDownX;
    private float begDownY;
    private GameMap gameMap;
    private JoyStick stick;
    static long currentTime;//action for pause
    static long startTime;
    private Timer timer;//timer for tick time and generate new food
    private TimerTasks timerTask;
    private boolean isPaused = false;
    private Canvas canvas;
    private  long previous = -1;//for fps

    public GameView(Context context) {
        super(context);
        paint = new Paint();
        runFlag = true;
        isPaused = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);
        scaling = (float)ScreenHeight / Settings.ScreenHeightDefault;
        //   Log.v("Scale", String.valueOf(scaling));
        matrix = new Matrix();
        matrix.setScale(CriticalData.scaling, CriticalData.scaling);
        isTouch = false;
        stick = new JoyStick(Settings.JoyStickRadiusOut,Settings.JoyStickRadiusIn);
        gameMap = CriticalData.gameMap;
        bulkesMap = CriticalData.bulkesMap;
        user = CriticalData.user;
        sectors = new SectorHolder();
        sectors.setOffsets(-gameMap.getOffsetTopLeftX(), - gameMap.getOffsetTopLeftY());
        isTouch = false;
        timer = new Timer();
        timerTask = new TimerTasks();
        timer.schedule(timerTask, 0, 1000);
        setStartTime();

        //enemy = new Enemy(1250f, 500f, 100f);
        //gameMap.addUnit(enemy);
        //enemy = CriticalData.enemy;
        // bulkesMap = new ArrayList<>(Settings.CountBulkes + 1);//1 - for user
        // bulkesMap.add(user);
        //bulkesMap.add(enemy);
    }

    public void pauseGame(boolean isPaused)
    {
        this.isPaused = isPaused;
    }

    public void setStartTime()
    {
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
    }

    public long getLastTime()
    {
        return (currentTime-startTime);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Settings.UserScale = 1f;
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
        CriticalData.user.setIsMoved(false);
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
        if(!isPaused) {
            drawMap();
            for (Bulk bulk : bulkesMap) {
                if (!bulk.isDeleted && bulk instanceof Enemy)
                {
                    ((Enemy) bulk).updateState(gameMap, sectors);
                   // drawBulk(bulk);
                }
            }
            drawUser();
            drawJoyStick();
            if(user.getRadius() > Settings.UserMaxRadius && user.getRadius() == user.getAnimationRadius()) {
                Settings.UserScale /= 2f;
                Log.v("User Scale ", String.valueOf(Settings.UserScale));
                for (Unit unit : gameMap.getMap()) {
                    unit.updatePosition(user);
                }
            }
            drawScores();
            drawFPS();
        }
    }
    private void drawFPS()
    {
        long current = System.currentTimeMillis();
        if( previous != -1)
        {
            long fps = 1000 / (current - previous);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(fps), 100f, (float) Settings.ScreenHeightDefault - 100, paint);
        }
        previous = current;
    }
    private void drawJoyStick()
    {
        if(isTouch)
        {
            //Log.v("Stick ", " OK");
            stick.getParameters(begDownX, begDownY, downX, downY);
            paint.setColor(Color.GRAY);
            paint.setAlpha(125);
            canvas.drawCircle(stick.getX0(), stick.getY0(), stick.getRadiusOut(), paint);
            paint.setAlpha(240);
            canvas.drawCircle(stick.getX(), stick.getY(), stick.getRadiusIn(), paint);
        }
    }

    private void drawBulk(Bulk bulk) {
            paint.setColor(bulk.getColor());
            paint.setStyle(Paint.Style.FILL);
            //update if bulk is not in screen area
            if (bulk.isOnMainScreen())
                canvas.drawCircle(bulk.getX(), bulk.getY(), bulk.getAnimationRadius(), paint);
    }
    private void drawUser()
    {
        drawBulk(user);
        paint.setColor(Color.GRAY);
        paint.setAlpha(240);
        canvas.drawPath(user.getIndicator(user.getX() + stick.getdX(), user.getY() + stick.getdY()), paint);
    }


private void drawMap()
{
    sectors.restartChecking();
    //sectors.checkUnit(user);
    float speedX = stick.getdX() * Settings.UserSpeedCoefficient;
    float speedY = stick.getdY() * Settings.UserSpeedCoefficient;
    user.setSpeed(Math.abs(speedX), Math.abs(speedY));
    speedX = -speedX;//user stand and other unit moved
    speedY = -speedY;
    int leftBorder      = gameMap.getOffsetTopLeftX() + 1;
    int rightBorder     = gameMap.getOffsetTopLeftX() + Settings.MapWidthP - 1;
    int upBorder        = gameMap.getOffsetTopLeftY() + 1;
    int downBorder      = gameMap.getOffsetTopLeftY() + Settings.MapHeightP - 1;
    LinkedList<Unit> map = gameMap.getMap();
    synchronized (map) {
        ListIterator<Unit> iterator = map.listIterator();
        while (iterator.hasNext()) {
            Unit point = iterator.next();


            if (point.getIsDeleted() == false) {
                sectors.checkUnit(point);//update: what with deleted items
                if (point instanceof User)
                    continue;
                for (Bulk bulk : bulkesMap) {
                    if (point != bulk && !bulk.getIsDeleted() && bulk.isEaten(point)) {
                        if (bulk.getRadius() > point.getRadius()) {
                            bulk.addMass(point.getFeed());
                            point.setIsDeleted(true, bulk);
                            //point.setSpeed(bulk.getSpeedX() * Settings.UnitToTargetCoefficient, bulk.getSpeedY() * Settings.UnitToTargetCoefficient);
                            if (bulk instanceof Enemy && ((Enemy) bulk).isTarget(point))
                                ((Enemy) bulk).setTarget(null);

                        } else {
                            if (bulk instanceof User)
                                drawEndGame();//update
                        }
                        break;
                    }
                }
            } if(point.getIsDeleted()) {
                //point.setTargetSpeed();
                if (point.catchTarget()) {
                    if (point instanceof Bulk)
                        bulkesMap.remove(point);
                    else
                        gameMap.incDeletedFood();
                    iterator.remove();
                }
            }
            gameMap.checkForFoodAdd(iterator);
            if (user.getIsMoved())//previous loop can change isDeleted
                point.move( speedX,  speedY);
            paint.setColor(point.color);

            if (point.getX() >= rightBorder)
                point.setX(point.getX() - Settings.MapWidthP    + 2);
            else if (point.getX() <= leftBorder)
                point.setX(point.getX() + Settings.MapWidthP    - 2);
            if (point.getY() >= downBorder)
                point.setY(point.getY() - Settings.MapHeightP   + 2);
            else if (point.getY() <= upBorder)
                point.setY(point.getY() + Settings.MapHeightP   - 2);
            //5 - correction, if x or y near border

            if(point.getX() >= 2 * Settings.ScreenWidthDefault || point.getY() >= 2 * Settings.ScreenHeightDefault )
                Log.e("Moving ", point.toString());
            if (point.isOnMainScreen())
                canvas.drawCircle(point.getX(), point.getY(), point.getAnimationRadius(), paint);

        }

    }
}
    private void drawScores()
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setAlpha(170);
        paint.setTextSize(52.0f);
        //Calendar calendar = Calendar.getInstance();
        //GameView.currentTime = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        Date temp_date = new Date();
        temp_date.setTime(CriticalData.lastTime + (currentTime - startTime));
        canvas.drawText(sdf.format(temp_date), 50f, 100f, paint);
        canvas.drawText(String.valueOf((int) user.mass / 10), Settings.ScreenWidthDefault - 250f, 100f, paint);
    }
    private void drawEndGame()
    {
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setTextSize(52.0f);
        canvas.drawText("End Game", 900f, 500f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                user.setIsMoved(true);
                isTouch = true;
                downX = begDownX = event.getX() / CriticalData.scaling;
                downY =  begDownY = event.getY() / CriticalData.scaling;
                break;
            case MotionEvent.ACTION_MOVE:
                downX = event.getX() / CriticalData.scaling;
                downY = event.getY() / CriticalData.scaling;
                user.setIsMoved(true);
                if( Math.abs(downX - begDownX) < 1f && Math.abs(downY - begDownY) < 1f) {user.setIsMoved(false);}
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if(downX == begDownX && downY == begDownY) {user.setIsMoved(false);}
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}