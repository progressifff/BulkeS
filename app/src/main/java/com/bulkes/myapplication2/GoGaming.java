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

import java.util.Iterator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
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
        //saving here
        //savedInstanceState.putFloat("val", 5f);
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
    private ArrayList<Bulk> bulkesMap;
    private SectorHolder    sectors;

    Indicator user_indicator;
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

    private Canvas canvas;

    private  long previous = -1;//for fps

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
        user = new User(ScreenWidth / 2 / scaling, ScreenHeight/2 / scaling, Settings.UserStartSize, Settings.UserDefaultColor);
        stick = new JoyStick(Settings.JoyStickRadiusOut, Settings.JoyStickRadiusIn);
        gameMap = new GameMap(3,3);
        sectors = new SectorHolder(gameMap.getLines(), gameMap.getColumns());
        sectors.setOffsets(-gameMap.getX0(), - gameMap.getY0());

     //   gameMap.setMapAxis(ScreenWidth, ScreenHeight);
        isTouch = false;

        timer = new Timer();
        timerTask = new TimerTasks();
        timer.schedule(timerTask, 0, 1000);
        Calendar calendar = Calendar.getInstance();
        startTime = calendar.getTimeInMillis();
        //enemy = new Enemy(1250f, 500f, 100f);
        //gameMap.addUnit(enemy);
        bulkesMap = new ArrayList<Bulk>(Settings.CountBulkes + 1);//1 - for user
        bulkesMap.add(user);
        gameMap.addUnit(user);
        Random random = new Random();
        for(int i = 0; i < Settings.CountBulkes; ++i) {
            Enemy enemy = new Enemy(random.nextInt(1000), random.nextInt(1000), random.nextInt(50) + 50);
            bulkesMap.add(enemy);
            gameMap.addUnit(enemy);
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Thread drawThread = new Thread(this);
        drawThread.setPriority(Thread.MAX_PRIORITY);
        drawThread.start();
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
                   // try {
                      //  Thread.sleep(5);
                   // }
                   // catch (InterruptedException e)
                   // {
                     //   e.printStackTrace();
                   // }
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
        for ( Bulk bulk : bulkesMap  )
        {
            if( !bulk.is_deleted ) {
                if (bulk instanceof Enemy)
                    ((Enemy) bulk).updateState(gameMap, sectors);
                drawBulk(bulk);
            }
        }
        drawJoyStick();
        drawScores();
        drawFPS();
    }
    private void drawFPS()
    {
        long current = System.currentTimeMillis();
        if( previous != -1)
        {
            long fps = 1000 / (current - previous);
            paint.setColor(Color.BLACK);
            canvas.drawText(String.valueOf(fps), 100f, (float)Settings.ScreenHeightDefault - 100, paint);
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
        if (bulk.getIsDeleted() == false) {
            paint.setColor(bulk.getColor());
            paint.setStyle(Paint.Style.FILL);
            //update if bulk is not in screen area
            canvas.drawCircle(bulk.getX(), bulk.getY(), bulk.getRadius(), paint);
            paint.setColor(Color.GRAY);
            paint.setAlpha(240);
            if (bulk.getIsMoved()) {
                if (bulk instanceof User)
                    canvas.drawPath(bulk.getTriangle(user.getX() + stick.getdX(),user.getY() + stick.getdY()), paint);
                else
                    canvas.drawPath(((Enemy) bulk).getTriangleToTarget(), paint);
            }
        }
    }


private void drawMap()
{
    sectors.restartChecking();
    //sectors.checkUnit(user);
    for(int i = 0; i < gameMap.getLines(); i++) {
        for (int j = 0; j < gameMap.getColumns(); j++) {
            Iterator<Unit> iterator = gameMap.getMap()[i][j].iterator();
            float speed = user.getSpeed();
            while (iterator.hasNext())
            {
                Unit point = iterator.next();


                if(point.getIsDeleted() == false)
                {
                    sectors.checkUnit(point);//update: what with deleted items
                    if(point instanceof User)
                        continue;
                    for (Bulk bulk : bulkesMap) {
                        if (point != bulk && !bulk.getIsDeleted() && bulk.isEated(point)) {
                            if (bulk.getRadius() > point.getRadius()) {
                                bulk.addMass(point.getFeed());
                                point.setIsDeleted(true);
                                if (bulk instanceof Enemy && ((Enemy) bulk).isTarget(point))
                                    bulk.setIsMoved(false);

                            } else {
                                if (bulk instanceof User)
                                    drawEndGame();//update
                            }
                            break;
                        }
                    }
                }
                if(point.getIsDeleted() == false) {
                    if (user.getIsMoved())//previous loop can change isDeleted
                        point.move(-stick.getdX() * speed, -stick.getdY() * speed);
                    paint.setColor(point.color);

                    if (point.getX() >= gameMap.getX0() + gameMap.getM() * Settings.ScreenWidthDefault)
                        point.setX(point.getX() - gameMap.getM() * Settings.ScreenWidthDefault);
                    else if (point.getX() <= gameMap.getX0())
                        point.setX(gameMap.getM() * Settings.ScreenWidthDefault + point.getX());
                    if (point.getY() >= gameMap.getY0() + gameMap.getK() * Settings.ScreenHeightDefault)
                        point.setY(point.getY() - gameMap.getK() * Settings.ScreenHeightDefault);
                    else if (point.getY() <= gameMap.getY0())
                        point.setY(gameMap.getK() * Settings.ScreenHeightDefault + point.getY());
                    // gameMap.checkPointSector(i,j,point,iterator);
                    if(!(point instanceof Bulk))
                        if (point.getX() <= Settings.ScreenWidthDefault + 80 && point.getX() >= -80 && point.getY() <= Settings.ScreenHeightDefault + 80 && point.getY() >= -80)
                            canvas.drawCircle(point.getX(), point.getY(), point.getRadius(), paint);

                }
            }
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
        temp_date.setTime(currentTime - startTime);
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