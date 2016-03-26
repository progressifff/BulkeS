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
            if(!isPause) {
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
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void setFullScreenMode()
    {
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
        if(gameView!=null)
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
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    onFinish = true;
                                    finish();
                                    dialog.cancel();
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

/*
class SavedObject implements Parcelable
{
   ArrayList<Object> objects;
    public SavedObject(ArrayList<Object> object) {
        this.objects = object;
    }
    private SavedObject(Parcel in) {
        objects = new ArrayList<Object>();
        in.readList(objects,Object.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public ArrayList<Object> getObject()
    {
        return objects;
    }

    @Override
    public String toString() {
        return "isOk";
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeList(objects);
    }

    public static final Parcelable.Creator<SavedObject> CREATOR = new Parcelable.Creator<SavedObject>() {
        public SavedObject createFromParcel(Parcel in) {
            return new SavedObject(in);
        }
        public SavedObject[] newArray(int size) {
            return new SavedObject[size];
        }
    };
}
*/


class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable
{
    private SurfaceHolder Holder;
    private Matrix matrix;
    private float downX;
    private float downY;
    private Paint paint;
    private boolean runFlag = false;
    private User user;
    private Enemy enemy;
    private ArrayList<Bulk> bulkesMap;
    //%%%%%%%%%%%%%%%%%%%%%%%%%%
    private Boolean isTouch;
    private float begDownX;
    private float begDownY;
    private GameMap gameMap;
    private JoyStick stick;
    //%%%%%%%%%%%%%%%%%%%%%%%%%
    static long currentTime;//action for pause
    static long startTime;
    private Timer timer;//timer for tick time and generate new food
    private TimerTasks timerTask;

    //%%%%%%%%%%%%%%%%%%%%%%%%%
    private Canvas canvas;
    private boolean isPaused = false;

    public GameView(Context context) {
        super(context);
        paint = new Paint();
        runFlag = true;
        isPaused = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);
        matrix = new Matrix();
        matrix.setScale(CriticalData.scaling, CriticalData.scaling);
        isTouch = false;
  //      Log.v("GameView","GameView");
        timer = new Timer();
        timerTask = new TimerTasks();
        timer.schedule(timerTask, 0, 1000);

        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();

        stick = new JoyStick(120,60);
        gameMap = CriticalData.gameMap;
        user = CriticalData.user;
        enemy = CriticalData.enemy;

        bulkesMap = new ArrayList<>(Settings.CountBulkes + 1);//1 - for user
        bulkesMap.add(user);
        bulkesMap.add(enemy);
    }

    public void pauseGame(boolean isPaused)
    {
        this.isPaused = isPaused;
    }

    public long getLastTime()
    {
        return (currentTime-startTime);
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
        CriticalData.user.setIsMoved(false);
        runFlag = false;
    }

    @Override
    public void run()
    {
        while(runFlag)
        {
        /*    while(runFlag&&isPaused)
            {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            */
            if(!Holder.getSurface().isValid())
                continue;
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
        if(!isPaused)
        {drawMap();
        drawScores();
        drawBulk(user);
        enemy.setTarget(user);
        enemy.updateState(gameMap);
        drawBulk(enemy);

        /*
        enemy.setIsMoved(true);
        enemy.setDx(0.1f);
        enemy.move(5f, 0f);
        */
        drawJoyStick();}
    }

    private void drawJoyStick()
    {
        if(isTouch)
        {
            stick.getParameters(begDownX, begDownY, downX, downY);
            paint.setColor(Color.GRAY);
            paint.setAlpha(125);
            canvas.drawCircle(stick.getX0(), stick.getY0(), stick.getRadiusOut(), paint);
            paint.setAlpha(240);
            canvas.drawCircle(stick.getX(), stick.getY(), stick.getRadiusIn(), paint);
        }
    }

    private void drawBulk(Bulk bulk) {
        if (bulk.getIsDeleted() == false) {
            paint.setColor(bulk.getColor());
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(bulk.getX(), bulk.getY(), bulk.getAnimationRadius(), paint);
            //user_indicator = user.getIndicatorPosition(user.getX() + deltaX, user.getY() + deltaY);
            paint.setColor(Color.GRAY);
            paint.setAlpha(240);
            if (bulk.getIsMoved()) {
                if (bulk == user)
                    canvas.drawPath(bulk.getTriangle(user.getX() + stick.getdX(),user.getY() + stick.getdY()), paint);
                else
                    canvas.drawPath(((Enemy) bulk).getTriangleToTarget(), paint);
            }
        }
    }

    private void drawMap()
    {
        int leftBorder = gameMap.getX0();
        int rightBorder = gameMap.getX0() + Settings.MapSizeX * Settings.ScreenWidthDefault;
        int upBorder = gameMap.getY0();
        int downBorder = gameMap.getY0() + Settings.MapSizeY * Settings.ScreenHeightDefault;
        ListIterator<Unit> iterator = gameMap.getMap().listIterator();
        while(iterator.hasNext())
        {
            Unit point = iterator.next();
            for (Bulk bulk : bulkesMap )
            {
                if(!point.getIsDeleted()) {
                    if (bulk.isEated(point)) {
                        if (bulk.getRadius() > point.getRadius()) {
                            bulk.addMass(point.getFeed());
                            if (bulk == enemy) {
                                if ((bulk.getX() >= Settings.ScreenWidthDefault + bulk.getRadius() || bulk.getX() <= -bulk.getRadius()) && (bulk.getY() >= Settings.ScreenHeightDefault + bulk.getRadius() || bulk.getY() <= -bulk.getRadius()))
                                    gameMap.delFood(iterator);
                                else
                                    point.setIsDeleted(true, bulk);
                            } else
                                point.setIsDeleted(true, bulk);
                        }
                    }
                }
                else
                {
                    if(point.insideBulk(stick.getdX(), stick.getdY()))
                    {
                        gameMap.delFood(iterator);
                        break;
                    }
                }
            }

            gameMap.checkForFoodAdd(iterator);
            if (user.getIsMoved())//previous lopp can change isDeleted
                point.move(-stick.getdX() * user.getSpeed(), -stick.getdY() * user.getSpeed());
            paint.setColor(point.color);

            if (point.getX() >= rightBorder)
                point.setX(point.getX() - Settings.MapSizeX * Settings.ScreenWidthDefault);
            else if (point.getX() <= leftBorder)
                point.setX(Settings.MapSizeX * Settings.ScreenWidthDefault + point.getX());
            if (point.getY() >= downBorder)
                point.setY(point.getY() - Settings.MapSizeY * Settings.ScreenHeightDefault);
            else if (point.getY() <= upBorder)
                point.setY(Settings.MapSizeY * Settings.ScreenHeightDefault + point.getY());

            if (point.getX() <= Settings.ScreenWidthDefault + 80 && point.getX() >= -80 && point.getY() <= Settings.ScreenHeightDefault + 80 && point.getY() >= -80)
                canvas.drawCircle(point.getX(), point.getY(), point.getAnimationRadius(), paint);
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
     //   Log.v("drawScores",String.valueOf(tempTime));
        temp_date.setTime(CriticalData.lastTime + (currentTime - startTime));
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
                downX = begDownX = event.getX() / CriticalData.scaling;
                downY =  begDownY = event.getY() / CriticalData.scaling;
                break;
            case MotionEvent.ACTION_MOVE:
                downX = event.getX() / CriticalData.scaling;
                downY = event.getY() / CriticalData.scaling;
                user.setIsMoved(true);
                if( Math.abs(downX - begDownX) < 1f && Math.abs(downY - begDownY) < 1f)
                {
                    Log.v("Action Up", "Pst=Pfn");
                    user.setIsMoved(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                if(downX == begDownX && downY == begDownY)
                {
                    Log.v("Action Up", "Pst=Pfn");
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