package com.bulkes.myapplication2;

import android.os.CountDownTimer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class GameMap
{
    private LinkedList<Unit>map;
    private Random random;
    private int X0;
    private int Y0;
    private float diffSectorX;
    private float diffSectorY;
    private int delFoodCount;
    private LinkedList<Unit> addFood;
    private boolean needAddFood;
    private int maxFoodCountOnMap;
    private int minFoodCountOnMap;
    public GameMap()
    {
        random = new Random();
        map = new LinkedList<>();
        addFood = new LinkedList<>();
        maxFoodCountOnMap = Settings.CountSectorX*Settings.MapSizeX*Settings.CountSectorY*Settings.MapSizeY*Settings.MaxFoodInSector;
        minFoodCountOnMap = Math.round(maxFoodCountOnMap/4);
        X0 = 0;
        Y0 = 0;
        delFoodCount = 0;
        needAddFood = false;
        setMapAxis();
        generateSmartMap();
        startFoodTimer();
    }

    private void setMapAxis()
    {
        X0 = (Settings.MapSizeX%2 == 0) ? (int)(-0.5f*Settings.ScreenWidthDefault*(Settings.MapSizeX - 1)) : (-Settings.MapSizeX/2*Settings.ScreenWidthDefault);
        Y0 = (Settings.MapSizeY%2 == 0) ? (int)(-0.5f*Settings.ScreenHeightDefault*(Settings.MapSizeY - 1)) : (-Settings.MapSizeY/2*Settings.ScreenHeightDefault);
    }

    private void startFoodTimer()
    {
        final CountDownTimer addFoodTimer = new CountDownTimer(7000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {}
            @Override
            public void onFinish()
            {
                if(delFoodCount != 0)
                {
                    addUnitRandomly(delFoodCount);
                    delFoodCount = 0;
                    needAddFood = true;
                }
                start();
            }
        }.start();
    }

    public void addUnitRandomly(int countFoodToDraw)
    {
        Unit food;
        float distance;
        Iterator<Unit> foodIterator;
        int mapSize = map.size();
        float scaleValue = (float)Math.floor(((0.6+Math.random()*(1.3-0.6))*10))/10;
        if(mapSize>=minFoodCountOnMap && mapSize<=maxFoodCountOnMap)
            countFoodToDraw = Math.round((float)countFoodToDraw*scaleValue);
        else if(mapSize + Math.round((float)countFoodToDraw*scaleValue) < minFoodCountOnMap)
            countFoodToDraw = Math.round((maxFoodCountOnMap - minFoodCountOnMap)/2);
        else if(mapSize > maxFoodCountOnMap)
            countFoodToDraw = 0;
        if(countFoodToDraw !=0) {
            for (int i = 0; i < countFoodToDraw; i++)
            {
                food = new Food(
                        getRandomX(X0, Settings.MapSizeX * Settings.ScreenWidthDefault),
                        getRandomY(Y0, Settings.MapSizeY * Settings.ScreenHeightDefault),
                        getRandomRadius(),
                        getColor(),
                        500);
                if (i != 0)
                {
                    boolean flagCorrect;
                    do
                    {
                        flagCorrect = true;
                        for (Unit temp : addFood)
                        {
                            distance = food.getRadius()+temp.getRadius()+18;
                            if((Math.abs(food.getX() - temp.getX())<distance)&&(Math.abs(food.getY()-temp.getY())<distance))
                            {
                                food.setX(getRandomX(X0, Settings.MapSizeX * Settings.ScreenWidthDefault));
                                food.setY(getRandomY(Y0, Settings.MapSizeY * Settings.ScreenHeightDefault));
                                food.setRadius(getRandomRadius());
                                flagCorrect = false;
                            }
                        }
                    }while(flagCorrect == false);
                    addFood.add(food);
                }
                else
                    addFood.add(food);
            }
            for(Iterator<Unit> mapIterator = map.iterator(); mapIterator.hasNext();){
                Unit point = mapIterator.next();
                for (foodIterator = addFood.iterator(); foodIterator.hasNext(); ) {
                    food = foodIterator.next();
                    distance = point.getRadius() + food.getRadius() + 18;//18, why 18
                    if ((Math.abs(point.getX() - food.getX()) < distance) && (Math.abs(point.getY() - food.getY()) < distance))
                        foodIterator.remove();
                }
            }
        }
    }

    public void checkForFoodAdd(ListIterator<Unit> iterator)
    {
        if(!addFood.isEmpty()&&needAddFood)
        {
            for(Unit food : addFood)
            {
                iterator.add(food);
            }
            needAddFood = false;
            addFood.clear();
        }
    }

    public void generateSmartMap()
    {
        Unit unit;
        float startSectorX;
        float startSectorY;
        LinkedList<Unit> sectorMap = new LinkedList<>();
        diffSectorX = Settings.ScreenWidthDefault  / Settings.CountSectorX;
        diffSectorY = Settings.ScreenHeightDefault / Settings.CountSectorY;
        startSectorY = Y0;
        while (startSectorY < (Y0 + Settings.MapSizeY*Settings.ScreenHeightDefault))
        {
            startSectorX = X0;
            while (startSectorX < (X0 + Settings.MapSizeX*Settings.ScreenWidthDefault))
            {
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                sectorMap.clear();
                for(int i = 0; i < foodInGroup; i++)
                {
                    unit = new Food(
                            getRandomX((int)(startSectorX + 20), (int)(diffSectorX - 20)),
                            getRandomY((int)(startSectorY + 20), (int)(diffSectorY - 20)),
                            getRandomRadius(),
                            getColor(),//update
                            500);//update
                    boolean flagCorrect;
                    do//update infinity loop
                    {
                        flagCorrect = true;
                        for (Unit temp : sectorMap)
                        {
                            float distance = unit.getRadius()+temp.getRadius()+12;
                            if((Math.abs(unit.getX() - temp.getX())<distance)&&(Math.abs(unit.getY()-temp.getY())<distance))
                            {
                                unit.setX(getRandomX((int) startSectorX, (int) diffSectorX));
                                unit.setY(getRandomY((int) startSectorY, (int) diffSectorY));
                                unit.setRadius(getRandomRadius());
                                flagCorrect = false;
                            }
                        }
                    }while(flagCorrect == false);
                    sectorMap.add(unit);
                }
                for (Unit t: sectorMap)//loop for adding
                {
                    map.add(t);
                }
                startSectorX += diffSectorX;
            }
            startSectorY += diffSectorY;
        }
    }

    private int getColor()
    {
        return Settings.ColorList[random.nextInt(Settings.getCountColors())];
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

    public void delFood(Iterator<Unit> iterator)
    {
        iterator.remove();
        delFoodCount++;
    }

    public LinkedList<Unit> getMap()
    {
        return map;
    }
    public void addUnit(Unit unit)
    {
        map.add(unit);//update index
    }

    public int getX0()
    {return X0;}
    public int getY0()
    {return Y0;}
    public Unit getAnyUnit()
    {
        if(!map.isEmpty())
            return map.getFirst();
        return null;
    }

   /*
    public int getLines()
    {
        return k* Settings.CountSectorY;
    }
    public int getColumns()
    {
        return m* Settings.CountSectorX;
    }
    public void getFoodSector(Unit point)
    {
    }
    */
}