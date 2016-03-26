package com.bulkes.myapplication2;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;

import java.util.LinkedList;
import java.util.Random;

public class GameMap
{
    private LinkedList<Unit>[][]map;
    private Random random;
    private float X0;
    private float Y0;
    private float diffSectorX;
    private float diffSectorY;
    private int k;
    private int m;
    private Unit unit;
    private int delFoodCount;

    public GameMap(int _k, int _m)
    {
        random = new Random();
        k = _k;
        m = _m;
        map = new LinkedList[k*Settings.CountSectorY][m*Settings.CountSectorX];
        X0 = 0;
        Y0 = 0;
        delFoodCount = 0;
        unit = new Unit();
        setMapAxis();
        generateSmartMap();
   //     checkFoodTimer();
    }

    private void checkFoodTimer()
    {
        final CountDownTimer addFoodTimer = new CountDownTimer(5000,1000)
        {
            int tempCount = 0;
            boolean onStart = true;
            @Override
            public void onTick(long millisUntilFinished)
            {
                if(onStart)
                {
                    tempCount = delFoodCount;
                    onStart = false;
                }
            }

            @Override
            public void onFinish()
            {
                tempCount = delFoodCount - tempCount;
                if(tempCount != 0)
                    addUnitRandomly(tempCount);
                onStart = true;
                start();
            }
        }.start();
    }

    public void addUnitRandomly(int countFoodToDraw)
    {
        Unit unit;
        for(int i = 0;i<countFoodToDraw;i++)
        {

        }
    }


    private void setMapAxis()
    {
        X0 = (m%2 == 0) ? (-0.5f*Settings.ScreenWidthDefault*(m - 1)) : (-m/2*Settings.ScreenWidthDefault);
        Y0 = (k%2 == 0) ? (-0.5f*Settings.ScreenHeightDefault*(k - 1)) : (-k/2*Settings.ScreenHeightDefault);
        Log.v("X0 Y0", String.valueOf(X0) + " " + String.valueOf(Y0));
    }

    public float getX0()
    {return X0;}
    public float getY0()
    {return Y0;}
    public int getK()
    {return k;}
    public int getM()
    {return m;}


    private int getColor()
    {
        return Settings.ColorList[random.nextInt(Settings.getCountColors())];
        //return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
    private float getRandomX(int startSectorX, int diffSectorX, float radius)
    {
        return  startSectorX + radius + random.nextInt(diffSectorX - (int)radius*2);//2 for right side
    }
    private float getRandomY(int startSectorY, int diffSectorY, float radius)
    {
        return startSectorY + radius + random.nextInt(diffSectorY - (int)radius*2);//2 for bottom side
    }
    private float getRandomRadius()
    {
        return (float)random.nextInt(Settings.MaxFoodSize - Settings.MinFoodSize) + Settings.MinFoodSize;
    }

    public void generateSmartMap()
    {
        Unit unit;
        //LinkedList <Unit> sector_map;
        float startSectorX;
        float startSectorY;
        diffSectorX = Settings.ScreenWidthDefault  / Settings.CountSectorX;
        diffSectorY = Settings.ScreenHeightDefault / Settings.CountSectorY;
        startSectorY = Y0;
        for(int i = 0; i < k*Settings.CountSectorY;i++)
        {
            startSectorX = X0;
            for(int j = 0;j < m*Settings.CountSectorX; j++)
            {
                map[i][j] = new LinkedList<>();
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector + 1 - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                for(int t = 0; t < foodInGroup; t++)
                {
                    float radius = getRandomRadius();
                    unit = new Food(
                            getRandomX((int)startSectorX, (int)diffSectorX, radius),
                            getRandomY((int)startSectorY, (int) diffSectorY, radius),
                            radius,
                            getColor(),
                            Settings.FoodFeedForRadius * radius);
                    boolean flagCorrect;
                    do
                    {
                        flagCorrect = true;
                        for (Unit temp : map[i][j])
                        {
                            if (temp.isOverlapped(unit))
                            {
                                radius = getRandomRadius();
                                unit.setX(getRandomX((int) startSectorX, (int) diffSectorX, radius));
                                unit.setY(getRandomY((int) startSectorY, (int) diffSectorY, radius));
                                unit.setRadius(radius);
                                flagCorrect = false;
                            }
                        }
                    }while(flagCorrect == false);
                    map[i][j].add(unit);
                }
                for(int e = 0; e < map[i][j].size()-1; e++)
                {
                    for(int t = e + 1; t<map[i][j].size(); t++)
                    {
                        if (map[i][j].get(e).isOverlapped(map[i][j].get(t)))
                            map[i][j].remove(map[i][j].get(t));
                    }
                }
                startSectorX += diffSectorX;
            }
            startSectorY += diffSectorY;
        }
    }

    public int getLines()
    {
        return k* Settings.CountSectorY;
    }

    public int getColumns()
    {
        return m* Settings.CountSectorX;
    }

    public LinkedList<Unit>[][] getMap()
    {
        return map;
    }
    public void addUnit(Unit unit)
    {
        map[1][1].add(unit);//update index
    }
}
