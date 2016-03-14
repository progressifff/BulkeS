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
    private int getColor(int count)
    {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    // 8 sectors; each contains [min;max] Food with [min;max] Size
    public void generateSmartMap()
    {
        map = new ArrayList<>();
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
                for(int i = 0; i < foodInGroup; ++i)
                {
                    unit = new Food(
                            startSectorX + random.nextInt((int)diffSectorX),
                            startSectorY + random.nextInt((int)diffSectorY),
                            (float)random.nextInt(Settings.MaxFoodSize - Settings.MinFoodInSector) + Settings.MinFoodSize,
                            getColor(random.nextInt(10)),//update
                            5);//update
                    map.add(unit);
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
