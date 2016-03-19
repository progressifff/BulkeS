package com.bulkes.myapplication2;

import android.graphics.Color;
import android.os.CountDownTimer;
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
        X0 = (m%2 == 0) ? (-0.5f*Settings.ScreenWidthDefault*(m - 1)/Settings.ScreenWidthDefault) : (-m/2*Settings.ScreenWidthDefault/Settings.ScreenWidthDefault);
        Y0 = (k%2 == 0) ? (-0.5f*Settings.ScreenHeightDefault*(k - 1)/Settings.ScreenHeightDefault) : (-k/2*Settings.ScreenHeightDefault/Settings.ScreenHeightDefault);
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
        Log.v("Color ", String.valueOf(Settings.getCountColors()));
        return Settings.ColorList[random.nextInt(Settings.getCountColors())];
        //return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }
    private float getRandomX(int startSectorX, int diffSectorX)
    {
        return startSectorX + random.nextInt(diffSectorX);
    }
    private float getRandomY(int startSectorY, int diffSectorY)
    {
        return startSectorY + random.nextInt(diffSectorY);
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
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                for(int t = 0; t < foodInGroup; t++)
                {
                    unit = new Food(
                            getRandomX((int)startSectorX, (int)diffSectorX),
                            getRandomY((int)startSectorY, (int) diffSectorY),
                            getRandomRadius(),
                            getColor(random.nextInt(10)),
                            5);
                    boolean flagCorrect;
                    do
                    {
                        flagCorrect = true;
                        for (Unit temp : map[i][j])
                        {
                            if (temp.isOverlapped(unit))
                            {
                                unit.setX(getRandomX((int) startSectorX, (int) diffSectorX));
                                unit.setY(getRandomY((int) startSectorY, (int) diffSectorY));
                                unit.setRadius(getRandomRadius());
                                flagCorrect = false;
                            }
                        }
                    }while(flagCorrect == false);
                    map[i][j].add(unit);
                    
                for(int e = 0; e < map[i][j].size()-1; e++)
                {
                    for(int t = e + 1; t<map[i][j].size(); t++)
                    {
                        if (map[i][j].get(e).isOverlapped(map[i][j].get(t)))
                            map[i][j].remove(map[i][j].get(t));
                    }
                }
                
    }
    /*
        public void checkPointSector(int i, int j, Unit point, Iterator<Unit> iterator)
        {
            boolean isTurn = false;
            float sectorY = Y0 + i*diffSectorY;
            float sectorX = X0 + j*diffSectorX;
            int columns = getColumns();
            int lines = getLines();
            if(point.getX() >= sectorX + diffSectorX)
            {
                Log.v("A","1111");
                iterator.remove();
                point.setX(point.getX() - m * Settings.ScreenWidthDefault);
                map[i][0].add(point);
                isTurn = true;
            }
            else if(point.getX() < X0)
            {
                Log.v("A","2222");
                iterator.remove();
                point.setX(m * Settings.ScreenWidthDefault + point.getX());
                map[i][columns-1].add(point);
                isTurn = true;
            }
            if(point.getY() >= sectorY + diffSectorY)
            {
                Log.v("A", "3333");
                iterator.remove();
                point.setY(point.getY() - k * Settings.ScreenHeightDefault);
                map[0][j].add(point);
                isTurn = true;
            }
            else if(point.getY() <= Y0)
            {
                Log.v("A","4444");
                iterator.remove();
                point.setY(k * Settings.ScreenHeightDefault + point.getY());
                map[lines-1][j].add(point);
                isTurn = true;
            }


            if(!isTurn) {
                if (point.getX() < sectorX && point.getY() < sectorY) {
                    Log.v("A","1111");
                    iterator.remove();
                    map[i - 1][j - 1].add(point);
                } else if (point.getX() < sectorX && point.getY() > sectorY + diffSectorY) {
                    Log.v("A","2222");
                    iterator.remove();
                    map[i + 1][j - 1].add(point);
                } else if (point.getX() > sectorX + diffSectorX && point.getY() < sectorY) {
                    Log.v("A","3333");
                    iterator.remove();
                    map[i - 1][j + 1].add(point);
                } else if (point.getX() > sectorX + diffSectorX && point.getY() > sectorY + diffSectorY) {
                    Log.v("A","4444");
                    iterator.remove();
                    map[i + 1][j + 1].add(point);
                } else if (point.getX() < sectorX) {
                    Log.v("A","5555");
                    ListIterator<Unit> iterat = getMap()[i][j-1].listIterator();
                    iterat.add(point);
                    //iterator.remove();
                }
            }


        }
     */
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
}
