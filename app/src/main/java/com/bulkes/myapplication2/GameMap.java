package com.bulkes.myapplication2;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;
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
    private CountDownTimer addFoodTimer;

    public GameMap()
    {
        random = new Random();
        map = new LinkedList<Unit>();
        addFood = new LinkedList<Unit>();
        maxFoodCountOnMap = Settings.CountSectorX*Settings.MapSizeX*Settings.CountSectorY*Settings.MapSizeY*Settings.MaxFoodInSector;
        minFoodCountOnMap = Math.round(maxFoodCountOnMap/4);
        offsetTopLeftX = (Settings.MapSizeX%2 == 0) ? (int)(-0.5f*Settings.ScreenWidthDefault*(Settings.MapSizeX - 1)) : (-Settings.MapSizeX/2*Settings.ScreenWidthDefault);
        offsetTopLeftY = (Settings.MapSizeY%2 == 0) ? (int)(-0.5f*Settings.ScreenHeightDefault*(Settings.MapSizeY - 1)) : (-Settings.MapSizeY/2*Settings.ScreenHeightDefault);
        delFoodCount = 0;
        needAddFood = false;
    }

    public void fillFood(ArrayList<Bulk> bulkesMap)
    {
        Log.v("fill food", String.valueOf(map.size()));
        generateSmartMap(bulkesMap);
        startFoodTimer();
    }

    public void stopFoodTimer() {addFoodTimer.cancel();}

    public void startFoodTimer()
    {
        addFoodTimer = new CountDownTimer(Settings.TimeDelayFirstNewFood * 1000,Settings.TimeCreateNewFood * 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {}
            @Override
            public void onFinish()
            {
                if(CriticalData.isRun) {
                    Log.v("Timer ", "Tick");
                    if (delFoodCount != 0) {
                        addUnitRandomly(delFoodCount);
                        delFoodCount = 0;
                        needAddFood = true;
                    }
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
                        radius,
                        getColor(),
                        Settings.FoodFeedForRadius * radius);
                boolean flagCorrect;
                do {
                    flagCorrect = true;
                    for (Unit temp : addFood) {
                        if (temp.isOverlapped(food)) {
                            radius = getRandomRadius();
                            food.setX(getRandomX(offsetTopLeftX, Settings.MapWidthP, radius));
                            food.setY(getRandomY(offsetTopLeftY, Settings.MapHeightP, radius));
                            food.setRadius(radius);
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

    public void generateSmartMap(ArrayList<Bulk> bulkesMap)
    {
        Unit unit;
        float startSectorX;
        float startSectorY;
        LinkedList<Unit> sectorMap = new LinkedList<Unit>();
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
                            getRandomX((int) startSectorX, (int) diffSectorX,radius),
                            getRandomY((int) startSectorY, (int) diffSectorY,radius),
                            radius,
                            getColor(),
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
                for (Unit food: sectorMap)//loop for adding
                {
                    boolean canAdd = true;
                    for (Bulk bulk: bulkesMap)
                        if(bulk.isOverlapped(food)) {
                            canAdd = false;
                            break;
                        }
                    if(canAdd)
                        map.add(food);
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
    public void addUnit(Unit unit)//update check here
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