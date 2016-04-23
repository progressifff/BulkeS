package com.bulkes.myapplication2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
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
    private     boolean isDialogOpened;
    private     boolean isEndDialog;
    private     PauseGameDialog pauseGameDialog;
    private     EndGameDialog endGamedialog;
    private     View gamePauseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMode();
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if((visibility&View.SYSTEM_UI_FLAG_IMMERSIVE) == 0) {
                    setFullScreenMode();
                }
            }
        });
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
    protected void onResume() {
        super.onResume();
        if(gameView.isPaused()) {
            if(!isDialogOpened) {
                setContentView(gamePauseView);
                if(isEndDialog) {
                    endGamedialog.show();
                    endGamedialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }
                else {
                    pauseGameDialog.show();
                    pauseGameDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                }
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

    private void setFullScreenMode() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void setIsDialogOpened(boolean isDialogOpened)
    {
        this.isDialogOpened = isDialogOpened;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("onPause", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("onStop", "onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v("onSaveInstanceState", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if(!gameView.isPaused()){

            CriticalData.lastTime += gameView.getLastTime();
            gameView.pauseGame(true);
            gamePauseView.setBackground(new BitmapDrawable(getResources(), gameView.getLastScene()));
        }
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
                isEndDialog = false;
                gameView.pauseGame(false);
                break;
            }
            case Settings.DialogGameOverID:
                CriticalData.createNewField();
                gameView = new GameView(this);
                this.setContentView(gameView);
                break;
        }
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

    private ArrayList<Bulk> bulkesMap;
    private SectorHolder sectors;
    private GameMap gameMap;
    private JoyStick stick;
    static long currentTime;//action for pause
    static long startTime;

    private Canvas gameCanvas;
    private long previous = -1;//for fps
    //-------------
    private Context context;
    private Thread thread;
    private Handler mainHandler;
    public Date gameTime;
    public User user;
    long restTime = 2000L;
    private boolean isPause;

    public GameView(Context context) {
        super(context);
        isPause = false;
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
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
        stick = new JoyStick(Settings.JoyStickRadiusOut,Settings.JoyStickRadiusIn);
        setGraphDataTimer();
        mainHandler = new Handler();
    }

    public void setGraphDataTimer()
    {
        final CountDownTimer graphDataTimer = new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(isPause) {
                    this.cancel();
                }
            }
            @Override
            public void onFinish() {
                CriticalData.usersMass.add((int) user.mass / 10);
                start();
            }
        }.start();
    }
/*
    private void startCountDownTimer() {
        graphDataTimer = new CountDownTimer(restTime, 1000) {
            public void onTick(long millisUntilFinished) {
                restTime = millisUntilFinished;
            }
            public void onFinish() {}
        }.start();
    }
*/
    public boolean isPaused() {
        return isPause;
    }

    public void pauseGame(boolean isPaused) {
        if(isPaused) {
            Log.v("pauseGame","pauseGame");
            isPause = true;
            runFlag = false;
        }
        else {
            Log.v("NOpauseGame", "NOpauseGame");
            isPause = false;
            gameMap.startFoodTimer();
            setGraphDataTimer();
        }
    }

    public Bitmap getLastScene()
    {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas sceneCanvas = new Canvas(bitmap);
        setDrawingCacheEnabled(true);
        measure(this.getWidth(), this.getHeight());
        layout(0, 0, this.getWidth(), this.getHeight());
        draw(sceneCanvas);
        return bitmap;
    }

    public long getLastTime() {
        return (currentTime-startTime);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("surfaceCreated", "surfaceCreated");
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        onFinishInflate();
        Log.v("surfaceChanged", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("surfaceDestroyed", "surfaceDestroyed");
        CriticalData.user.setIsMoved(false);
        gameMap.stopFoodTimer();
        runFlag = false;
        mainHandler.removeCallbacks(this);
    }

    @Override
    public void run() {
        while(runFlag)
        {
            gameCanvas = null;
            try
            {
                gameCanvas = Holder.lockCanvas();
                if(gameCanvas!=null)
                {
                    synchronized (Holder)
                    {
                        gameCanvas.setMatrix(matrix);
                        this.draw(gameCanvas);
                    }
                }
            }
            finally
            {
                if(gameCanvas != null) {
                    Holder.unlockCanvasAndPost(gameCanvas);
                }
            }
        //mainHandler.postDelayed(this,2);
        }
    }

    @SuppressLint("MissingSuperCall")
    public void draw(Canvas canvas)
    {
//------------------------Draw Field------------------------------------------------------------
        paint.setColor(Settings.GameFieldColor);
        canvas.drawPaint(paint);

        drawMap(canvas);
        for (Bulk bulk : bulkesMap) {
            if (!bulk.isDeleted && bulk instanceof Enemy)
            {
                drawBulk(bulk,canvas);
                if(!((Enemy) bulk).isDeleted)
                    ((Enemy) bulk).updateState(gameMap, sectors);
            }
        }
        drawUser(canvas);
        drawJoyStick(canvas);
        if(user.getRadius() > Settings.UserMaxRadius && user.getRadius() == user.getAnimationRadius()) {
            Settings.UserScale -= Settings.UserScaleStep;
            Log.v("User Scale ", String.valueOf(Settings.UserScale));
            for (Unit unit : gameMap.getMap()) {
                unit.updatePosition(user);
            }
        }
        drawScores(canvas);
        drawFPS(canvas);
    }

    private void drawFPS(Canvas canvas) {
        long current = System.currentTimeMillis();
        if (previous != -1)
        {
            long fps = 1000 / (current - previous);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(fps), 100f, (float) Settings.ScreenHeightDefault - 100, paint);
        }
        previous = current;
    }

    private void drawJoyStick(Canvas canvas)
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

    private void drawBulk(Bulk bulk,Canvas canvas)
    {
        paint.setColor(bulk.getColor());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bulk.getX(), bulk.getY(), bulk.getAnimationRadius(), paint);
    }

    private void drawUser(Canvas canvas)
    {
        drawBulk(user,canvas);
        if(user.isMoved) {
            paint.setColor(Color.GRAY);
            paint.setAlpha(240);
            canvas.drawPath(user.getIndicator(user.getX() + stick.getdX(), user.getY() + stick.getdY()), paint);
        }
    }

    private void drawMap(Canvas canvas) {
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
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pauseGame(true);
                                        GameOverDialog gameOverDialog = new GameOverDialog(context);
                                        gameOverDialog.show();
                                        gameOverDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
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

    private void drawScores(Canvas canvas)
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

    private void setGameTime()
    {
        Calendar calendar = Calendar.getInstance();
        currentTime = calendar.getTimeInMillis();
        gameTime.setTime(CriticalData.lastTime + (currentTime - startTime));
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