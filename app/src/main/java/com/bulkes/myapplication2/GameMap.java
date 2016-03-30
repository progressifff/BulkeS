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
    private int offsetTopLeftX;
    private int offsetTopLeftY;
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
        offsetTopLeftX = (Settings.MapSizeX%2 == 0) ? (int)(-0.5f*Settings.ScreenWidthDefault*(Settings.MapSizeX - 1)) : (-Settings.MapSizeX/2*Settings.ScreenWidthDefault);
        offsetTopLeftY = (Settings.MapSizeY%2 == 0) ? (int)(-0.5f*Settings.ScreenHeightDefault*(Settings.MapSizeY - 1)) : (-Settings.MapSizeY/2*Settings.ScreenHeightDefault);
        delFoodCount = 0;
        needAddFood = false;
        generateSmartMap();
        startFoodTimer();
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

    public void addUnitRandomly(int countFoodToDraw) {
        Unit food;
        ListIterator<Unit> foodIterator;
        int mapSize = map.size();
        float scaleValue = (float) Math.floor(((Settings.MinAddFoodScaleValue + Math.random() * (Settings.MaxAddFoodScaleValue - Settings.MinAddFoodScaleValue)) * 10)) / 10;
        if (mapSize >= minFoodCountOnMap && mapSize <= maxFoodCountOnMap)
            countFoodToDraw = Math.round((float) countFoodToDraw * scaleValue);
        else if (mapSize + Math.round((float) countFoodToDraw * scaleValue) < minFoodCountOnMap)
            countFoodToDraw = Math.round((maxFoodCountOnMap - minFoodCountOnMap) / 2);
        else if (mapSize > maxFoodCountOnMap)
            countFoodToDraw = 0;
        if (countFoodToDraw != 0) {
            for (int i = 0; i < countFoodToDraw; i++) {
                float radius = getRandomRadius();
                food = new Food(
                        getRandomX(offsetTopLeftX, Settings.MapWidthP, radius),
                        getRandomY(offsetTopLeftY, Settings.MapHeightP, radius),
                        getRandomRadius(),
                        getColor(),
                        Settings.FoodFeedForRadius * radius);
                boolean flagCorrect;
                do {
                    flagCorrect = true;
                    for (Unit temp : addFood) {
                        radius = getRandomRadius();
                        if (temp.isOverlapped(food)) {
                            food.setX(getRandomX(offsetTopLeftX, Settings.MapWidthP, radius));
                            food.setY(getRandomY(offsetTopLeftY, Settings.MapHeightP, radius));
                            food.setRadius(getRandomRadius());
                            flagCorrect = false;
                        }
                    }
                } while (flagCorrect == false);
                addFood.add(food);
            }
            synchronized (map) {
                for (Iterator<Unit> mapIterator = map.iterator(); mapIterator.hasNext(); ) {
                    Unit point = mapIterator.next();
                    if (!addFood.isEmpty()) {
                        synchronized (addFood) {
                            for (foodIterator = addFood.listIterator(); foodIterator.hasNext(); ) {
                                food = foodIterator.next();
                                if (food.isOverlapped(point)) foodIterator.remove();
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkForFoodAdd(ListIterator<Unit> iterator)
    {
        if (!addFood.isEmpty()&&needAddFood)
        {
            for (Unit food : addFood) {
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
        startSectorY = offsetTopLeftY;
        while (startSectorY < (offsetTopLeftY + Settings.MapHeightP))
        {
            startSectorX = offsetTopLeftX;
            while (startSectorX < (offsetTopLeftX + Settings.MapWidthP))
            {
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                sectorMap.clear();
                for(int i = 0; i < foodInGroup; i++)
                {
                    float radius = getRandomRadius();
                    unit = new Food(
                            getRandomX((int)(startSectorX), (int)(diffSectorX),radius),
                            getRandomY((int)(startSectorY), (int)(diffSectorY),radius),
                            radius,
                            getColor(),//update
                            Settings.FoodFeedForRadius * radius);
                    boolean flagCorrect;
                    do
                    {
                        flagCorrect = true;
                        for (Unit temp : sectorMap)
                        {
                            if(temp.isOverlapped(unit))
                            {
                                radius = getRandomRadius();
                                unit.setX(getRandomX((int) startSectorX, (int) diffSectorX, radius));
                                unit.setY(getRandomY((int) startSectorY, (int) diffSectorY, radius));
                                unit.setRadius(radius);
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

    public void incDeletedFood()
    {
        delFoodCount++;
    }

    public LinkedList<Unit> getMap()
    {
        return map;
    }
    public void addUnit(Unit unit)
    {
        map.add(unit);
    }

    public int getOffsetTopLeftX()
    {return offsetTopLeftX;}
    public int getOffsetTopLeftY()
    {return offsetTopLeftY;}
    public Unit getAnyUnit()
    {
        if(!map.isEmpty())
            return map.getFirst();
        return null;
    }
}