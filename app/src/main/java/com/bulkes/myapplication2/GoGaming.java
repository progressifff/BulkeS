package com.bulkes.myapplication2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by progr on 08.03.2016.
 */
public class GoGaming extends AppCompatActivity {
    private     GameView gameView;
    private     Window window;
    private     boolean isPause;
    private     boolean isDialogOpened;
    private     boolean isEndDialog;
    private     PauseGameDialog pauseGameDialog;
    private     EndGameDialog endGamedialog;
    private     View gamePauseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("onCreate", "onCreate");
        isPause = false;
        isDialogOpened = false;
        isEndDialog = false;
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pauseGameDialog = new PauseGameDialog(this);
        endGamedialog = new EndGameDialog(this);
        gameView = new GameView(this);
        setContentView(gameView);
        gamePauseView = new View(this);
        gamePauseView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setFullScreenMode();
        if(isPause) {
            gameView.pauseGame(true);
            if(!isDialogOpened) {
                gamePauseView.setBackground(new BitmapDrawable(getResources(), gameView.getLastScene()));
                setContentView(gamePauseView);
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
                CriticalData.lastTime += gameView.getLastTime();
                gameView.pauseGame(true);
                gamePauseView.setBackground(new BitmapDrawable(getResources(), gameView.getLastScene()));
                setContentView(gamePauseView);
                endGamedialog.show();
                isDialogOpened = true;
                isEndDialog = true;
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setFullScreenMode()
    {
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void setIsDialogOpened(boolean isDialogOpened)
    {
        this.isDialogOpened = isDialogOpened;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.v("onPause", "onPause");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        isPause = true;
        Log.v("onStop", "onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.v("onSaveInstanceState", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (gameView!=null) {CriticalData.lastTime += gameView.getLastTime();}
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void dialogStartGame(int id)
    {
        switch (id)
        {
            case Settings.DialogPauseID:
            case Settings.DialogEndID:
            {
                gameView = new GameView(this);
                this.setContentView(gameView);
                isPause = false;
                isEndDialog = false;
                gameView.setStartTime();
                gameView.pauseGame(false);
                break;
            }
            case Settings.DialogGameOverID:
                CriticalData.createNewField();
                gameView = new GameView(this);
                this.setContentView(gameView);
                break;
        }
        setFullScreenMode();
        isDialogOpened = false;
    }
}

class GameView extends SurfaceView implements SurfaceHolder.Callback,Runnable
{
    private SurfaceHolder Holder;
    private Matrix matrix;
    private float downX;
    private float downY;
    private float begDownX;
    private float begDownY;
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

    private Canvas canvas;
    private long previous = -1;//for fps
    //-------------
    private Context context;
    private Thread thread;
    private Handler mainHandler;
    private CountDownTimer graphDataTimer;
    private Date gameTime;


    public GameView(Context context) {
        super(context);
        Log.v("GameView", "GameView");
        this.context = context;
        paint = new Paint();
        runFlag = true;
        Holder = this.getHolder();
        Holder.addCallback(this);
        this.setFocusable(true);
        matrix = new Matrix();
        matrix.setScale(CriticalData.scaling, CriticalData.scaling);
        isTouch = false;
        gameMap = CriticalData.gameMap;
        bulkesMap = CriticalData.bulkesMap;
        user = CriticalData.user;
        gameTime = new Date(0);
        sectors = new SectorHolder();
        sectors.setOffsets(-gameMap.getOffsetTopLeftX(), - gameMap.getOffsetTopLeftY());
        isTouch = false;
        setStartTime();
        stick = new JoyStick(Settings.JoyStickRadiusOut,Settings.JoyStickRadiusIn);
        startGraphDataTimer();
        mainHandler = new Handler();
    }

    private void startGraphDataTimer()
    {
        CriticalData.graphPoints.add(new GraphPoint(gameTime.getTime(),((int) user.mass / 10)));
        graphDataTimer = new CountDownTimer(2000,2000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                CriticalData.graphPoints.add(new GraphPoint(gameTime.getTime(),((int) user.mass / 10)));
                start();
            }
        }.start();
    }

    public void pauseGame(boolean isPaused)
    {
        synchronized (this) {
            if(isPaused)
            {
                runFlag = false;
                synchronized(this) {
                    notify();
                }
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                gameMap.stopFoodTimer();
                graphDataTimer.cancel();
            }
            else
            {
                gameMap.startFoodTimer();
                graphDataTimer.start();
            }
        }
    }

    public void setStartTime()
    {
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
    }

    public Bitmap getLastScene()
    {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        setDrawingCacheEnabled(true);
        measure(this.getWidth(), this.getHeight());
        layout(0, 0, this.getWidth(), this.getHeight());
        draw();
        return bitmap;
    }

    public long getLastTime()
    {
        return (currentTime-startTime);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("surfaceCreated", "surfaceCreated");
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        onFinishInflate();
        Log.v("surfaceChanged", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        Log.v("surfaceDestroyed", "surfaceDestroyed");
        CriticalData.user.setIsMoved(false);
        gameMap.stopFoodTimer();
        graphDataTimer.cancel();
        runFlag = false;
        mainHandler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        Log.v("pauseGame","pauseGame");
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
                        this.draw();
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

    public void draw()
    {
//------------------------Draw Field------------------------------------------------------------
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
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

    private void drawMap() {
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
        synchronized (map)
        {
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
                                if (bulk instanceof Enemy && ((Enemy) bulk).isTarget(point))
                                    ((Enemy) bulk).setTarget(null);
                            } else
                            if (bulk instanceof User)
                            {
                                user.setIsMoved(false);
                                bulk.setIsMoved(false);
                                mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            GameOverDialog gameOverDialog = new GameOverDialog(context);
                                            gameOverDialog.show();
                                            ((GoGaming)context).setIsDialogOpened(true);
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
        SimpleDateFormat sdf = new SimpleDateFormat("m:ss");
        Calendar calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        gameTime.setTime(CriticalData.lastTime + (currentTime - startTime));
        canvas.drawText(sdf.format(gameTime), 50f, 100f, paint);
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

class GraphPoint
{
    public long gameTime;
    public int userMass;
    public GraphPoint(long gameTime, int userMass)
    {
        this.gameTime = gameTime;
        this.userMass = userMass;
    }
}