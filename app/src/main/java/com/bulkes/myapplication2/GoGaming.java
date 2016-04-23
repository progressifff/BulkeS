package com.bulkes.myapplication2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by progr on 08.03.2016.
 */
public class GoGaming extends AppCompatActivity {
    private static GameView gameView;
    private static boolean isPause;
    public static boolean onFinish;
    private static Activity activity;
    private static Window window;
    public static boolean isDialogOpened;
    private static boolean isEndDialog;
    static PauseGameDialog pauseGameDialog;
    static EndGameDialog endGamedialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "onCreate");
        onFinish = false;
        activity = this;
        isPause = false;
        isDialogOpened = false;
        isEndDialog = false;
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pauseGameDialog = new PauseGameDialog(activity);
        endGamedialog = new EndGameDialog(activity);
        gameView = new GameView(this);
        setContentView(gameView);
        gameView.pauseGame(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullScreenMode();
        if(isPause) {
            gameView = new GameView(this);
            setContentView(gameView);
            gameView.pauseGame(true);
            if(!isDialogOpened) {
                if(isEndDialog)
                    endGamedialog.show();
                else
                    pauseGameDialog.show();
                isDialogOpened = true;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if(!isDialogOpened) {
                gameView.pauseGame(true);
                endGamedialog.show();
                isDialogOpened = true;
                isEndDialog = true;
                CriticalData.lastTime += gameView.getLastTime();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void setFullScreenMode()
    {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //Log.v("onPause", "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(onFinish)
            isPause = false;
        else
            isPause = true;

       // Log.v("onStop", "onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (gameView!=null) {CriticalData.lastTime += gameView.getLastTime();}
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    static void dialogStartGame(int id)
    {
        switch (id)
        {
            case Settings.DialogPauseID:
            case Settings.DialogEndID:
            {
                isPause = false;
                isEndDialog = false;
                gameView.setStartTime();
                break;
            }
            case Settings.DialogGameOverID:
                CriticalData.createNewField();
                gameView = new GameView(activity);
                activity.setContentView(gameView);

                //isEndDialog = false;
                break;
        }
        setFullScreenMode();
        gameView.pauseGame(false);
        isDialogOpened = false;
    }
}

class TimerTasks extends TimerTask
{
    @Override
    public void run()
    {
        Calendar calendar = Calendar.getInstance();
        GameView.currentTime = calendar.getTimeInMillis();
    }
}

class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable
{
    private SurfaceHolder Holder;
 //   private int ScreenWidth;
 //   private int ScreenHeight;
   // private float scaling;
    private Matrix matrix;
    public static float downX;
    public static float downY;
    public static float begDownX;
    public static float begDownY;
    private Boolean isTouch;
    private Paint paint;
    private boolean runFlag = false;
    private User user;
    private ArrayList<Bulk> bulkesMap;
    private SectorHolder sectors;
    private GameMap gameMap;
    private JoyStick stick;
    static long currentTime;//action for pause
    static long startTime;

    private Timer timer;//timer for tick time and generate new food
    private TimerTasks timerTask;

    private boolean gamePaused;
    private Canvas canvas;
    private long previous = -1;//for fps
    //-------------
    private Context context;
    private Thread thread;
    private Handler mainHandler;

    public GameView(Context context) {
        super(context);
       // Log.v("GameView", "GameView");
        this.context = context;
        paint = new Paint();
        runFlag = true;
        gamePaused = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);
    //    scaling = (float)ScreenHeight / Settings.ScreenHeightDefault;
        matrix = new Matrix();
        matrix.setScale(CriticalData.scaling, CriticalData.scaling);
        isTouch = false;


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
        stick = new JoyStick(Settings.JoyStickRadiusOut,Settings.JoyStickRadiusIn);
        Log.v("Bulkes Map", String.valueOf(bulkesMap.size()));
        mainHandler = new Handler();
        /*
        {
            @Override
            public void handleMessage(Message msg) {
                msg.getData();
                synchronized (this)
                {
                stick = t.getStick();}
            //    Log.v("TTTTTTTTTTTTT", String.valueOf(msg.getData()));
            }
        };
        */
    }

    public void pauseGame(boolean isPaused)
    {
        synchronized (this) {
            user.setIsMoved(false);
            gamePaused = isPaused;
            CriticalData.isRun = !isPaused;
        }
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
        Log.v("surfaceCreated", "surfaceCreated");
        this.setDrawingCacheEnabled(true);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        onFinishInflate();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        this.setDrawingCacheEnabled(false);
        Log.v("surfaceDestroyed", "surfaceDestroyed");
        CriticalData.user.setIsMoved(false);
        CriticalData.isRun = false;
        runFlag = false;
        mainHandler.removeCallbacks(this);
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
        //mainHandler.postDelayed(this,2);
        }
    }

    public void Draw()
    {
//------------------------Draw Field------------------------------------------------------------
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        if(!gamePaused) {
            drawMap();
            for (Bulk bulk : bulkesMap) {
                if (bulk instanceof Enemy)
                {
                    drawBulk(bulk);
                    if(!((Enemy) bulk).isDeleted)
                        ((Enemy) bulk).updateState(gameMap, sectors);
                    // drawBulk(bulk);
                }
            }
            drawUser();
            drawJoyStick();
            if(user.getRadius() > Settings.UserMaxRadius && user.getAnimationRadius() >= user.getRadius()) {
                Settings.UserScale -= Settings.UserScaleStep;
                Log.v("User Scale ", String.valueOf(Settings.UserScale));
                for (Unit unit : gameMap.getMap()) {
                    unit.updatePosition(user);
                }
            }
            drawScores();
            drawFPS();
        }
    }

    private void drawFPS() {
        long current = System.currentTimeMillis();
        if (previous != -1)
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
            stick.getParameters(begDownX, begDownY, downX, downY);
            paint.setColor(Color.GRAY);
            paint.setAlpha(125);
            canvas.drawCircle(stick.getX0(), stick.getY0(), stick.getRadiusOut(), paint);
            paint.setAlpha(240);
            canvas.drawCircle(stick.getX(), stick.getY(), stick.getRadiusIn(), paint);
        }
    }
    private void drawBulk(Bulk bulk)
    {
        paint.setColor(bulk.getColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bulk.getX(), bulk.getY(), bulk.getAnimationRadius(), paint);
    }
    private void drawUser()
    {
        drawBulk(user);
        if(user.isMoved) {
            paint.setColor(Color.GRAY);
            paint.setAlpha(240);
            canvas.drawPath(user.getIndicator(user.getX() + stick.getdX(), user.getY() + stick.getdY()), paint);
        }
    }

    private void drawMap() {
        sectors.restartChecking();
        //sectors.checkUnit(user);
        float speedX = stick.getdX() * Settings.UserSpeedCoefficient;
        float speedY = stick.getdY() * Settings.UserSpeedCoefficient;
        user.setSpeed(Math.abs(speedX), Math.abs(speedY));
        speedX = -speedX * user.getSpeedCoefficient();//user stand and other unit moved
        speedY = -speedY * user.getSpeedCoefficient();//user transform base speed to real speed(dependence of radius)
        boolean needMove = user.getIsMoved();
        int leftBorder      = gameMap.getOffsetTopLeftX() + 1;
        int rightBorder     = gameMap.getOffsetTopLeftX() + Settings.MapWidthP - 1;
        int upBorder        = gameMap.getOffsetTopLeftY() + 1;
        int downBorder      = gameMap.getOffsetTopLeftY() + Settings.MapHeightP - 1;
        LinkedList<Unit> map = gameMap.getMap();
        synchronized (map)
        {
            ListIterator<Unit> iterator = map.listIterator();
            gameMap.checkForFoodAdd(iterator);
            while (iterator.hasNext()) {
                Unit point = iterator.next();
                //if (point instanceof User)
                //{Log.v("Map " , "User");continue;}
                if (point.getIsDeleted() == false) {
                    sectors.checkUnit(point);//update: what with deleted items
                    if(point instanceof User)
                        continue;
                    for (Bulk bulk : bulkesMap) {
                        if (point != bulk && !bulk.getIsDeleted() && bulk.isEaten(point)) {
                            if (bulk.getRadius() > point.getRadius()) {
                                bulk.addMass(point.getFeed());
                                point.setIsDeleted(true, bulk);
                                if (bulk instanceof Enemy && ((Enemy) bulk).isTarget(point))
                                    ((Enemy) bulk).setTarget(null);
                            } else
                            if (bulk instanceof User) {
                                    user.setIsMoved(false);
                                    bulk.setIsMoved(false);
                                    //Handler mHandler = new Handler(Looper.getMainLooper());
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            GameOverDialog gameOverDialog = new GameOverDialog(context);
                                            gameOverDialog.show();
                                            GoGaming.isDialogOpened = true;
                                        }
                                    });
                                    surfaceDestroyed(Holder);
                                }
                            }
                    }

                }
                else if(point.getIsDeleted()) {
                    if (point.catchTarget()) {
                        if (point instanceof Bulk)
                            bulkesMap.remove(point);
                        else
                            gameMap.incDeletedFood();
                        iterator.remove();
                    }
                }
                if (needMove)//previous loop can change isDeleted
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
                //2 - correction, if x or y near border
                if (point.isOnMainScreen() && !(point instanceof Enemy))
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
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        Date temp_date = new Date();
        temp_date.setTime(CriticalData.lastTime + (currentTime - startTime));
        canvas.drawText(sdf.format(temp_date), 50f, 100f, paint);
        canvas.drawText(String.valueOf((int) user.mass / 10), Settings.ScreenWidthDefault - 250f, 100f, paint);
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
                    user.setIsMoved(false);
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
