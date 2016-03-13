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
        map = new ArrayList<>();
        random = new Random();
        for(int i =0; i<15;i++)
            addRandomUnit();
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
        Unit point = new Unit(random.nextInt(600), random.nextInt(1000), (float)(20 + (random.nextInt(50-20 + 1))), Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        map.add(point);
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
