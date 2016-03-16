package com.bulkes.myapplication2;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class GameMap
{
    private ArrayList<Unit> map;
    private Random random;
    public GameMap()
    {
        random = new Random();
        generateSmartMap();
        //this(15);
    }



    public GameMap(int count)
    {
        map = new ArrayList<>();
        random = new Random();
        for(int i =0; i<count;i++)
            addRandomUnit();
    }
    public void addRandomUnit()
    {
        Unit point = new Unit(random.nextInt(1920), random.nextInt(1080), (float)(20 + (random.nextInt(50-20 + 1))), Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        map.add(point);
    }
    //update!!!
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
    public void addUnit(Unit unit)
    {
        map.add(unit);
    }
    // 8 sectors; each contains [min;max] Food with [min;max] Size
    public void generateSmartMap()
    {
        map = new ArrayList<>();
        ArrayList <Unit> sector_map = new ArrayList<>(100);
        Unit unit;
        float startSectorX;
        float startSectorY;
        float diffSectorX = Settings.ScreenWidthDefault / Settings.CountSectorX;
        float diffSectorY = Settings.ScreenHeightDefault / Settings.CountSectorY;
        startSectorY = 0f;
        while (startSectorY < Settings.ScreenHeightDefault)
        {
            startSectorX = 0f;
            while (startSectorX < Settings.ScreenWidthDefault)
            {
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                Log.v("Food ", String.valueOf(foodInGroup));
                sector_map.clear();
                for(int i = 0; i < foodInGroup; ++i)
                {
                    unit = new Food(
                            getRandomX((int)startSectorX, (int)diffSectorX),
                            getRandomY((int) startSectorY, (int) diffSectorY),
                            getRandomRadius(),
                            getColor(),//update
                            Settings.FoodDefaultFeed);//update
                    boolean flagCorrect;
                    do//update infinity loop
                    {
                        flagCorrect = true;
                        for (Unit temp : sector_map)
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
                    sector_map.add(unit);
                }
                for (Unit temp: map)//loop for correction
                {
                    for (Unit temp_sector: sector_map)
                    {
                        if(temp.isOverlapped(temp_sector))
                            temp_sector.setIsDeleted(true);
                    }
                }
                for (Unit temp: sector_map)//loop for adding
                {
                    map.add(temp);
                }
                startSectorX += diffSectorX;
            }
            startSectorY += diffSectorY;
        }
    }
    public int getSize()
    {
        return map.size();
    }

    public Unit getMapUnit(int i)
    {
        if(i<map.size() && i >= 0)
        {
            return map.get(i);
        }
        else
        {
            Log.v("Map","Выход за пределы карты");
            return map.get(map.size()-1);
        }
    }
}
