package com.bulkes.myapplication2;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

class mapSize
{
    public int k;
    public int m;
    public mapSize(int _k, int _m)
    {k = _k; m = _m;}
}

public class GameMap
{
    private LinkedList<Unit> map;
    private Random random;
    private float X0;
    private float Y0;
    private float diffSectorX;
    private float diffSectorY;
    private mapSize size;
    private Unit unit;
    private ArrayList <Unit> sector_map;
    public GameMap(int sizeI, int sizeJ)
    {
        random = new Random();
        map = new LinkedList<>();
        size = new mapSize(sizeI,sizeJ);
        X0 = 0;
        Y0 = 0;
        unit = new Unit();
        sector_map = new ArrayList<>(100);
        setMapAxis();
        generateSmartMap();
    }

    private void setMapAxis()
    {
        X0 = (size.m%2 == 0) ? (-0.5f*Settings.ScreenWidthDefault*(size.m - 1)) : (-size.m/2*Settings.ScreenWidthDefault);
        Y0 = (size.k%2 == 0) ? (-0.5f*Settings.ScreenHeightDefault*(size.k - 1)) : (-size.k/2*Settings.ScreenHeightDefault);
    }

    public void changeMapOfs(float dx, float dy)
    {
        X0 += dx;
        Y0 += dy;
    }
    public float getX0()
    {return X0;}
    public float getY0()
    {return Y0;}
    public void setX0(float _X0)
    {X0 = _X0;}
    public void setY0(float _Y0)
    {Y0 = _Y0;}
    public mapSize getMapSize()
    {return size;}

    public GameMap(int count)
    {
        map = new LinkedList<>();
        random = new Random();
        for(int i =0; i<count;i++)
            addRandomUnit();
    }
    public void addRandomUnit()
    {
        Unit point = new Unit(random.nextInt(1920), random.nextInt(1080), (float)(20 + (random.nextInt(50-20 + 1))), Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        map.add(point);
    }

    private int getColor(int count)
    {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
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
        float startSectorX;
        float startSectorY;
        diffSectorX = Settings.ScreenWidthDefault  / Settings.CountSectorX;
        diffSectorY = Settings.ScreenHeightDefault / Settings.CountSectorY;
        startSectorY = Y0;
        Log.v("SSSSSSS", String.valueOf(Y0));
        Log.v("SSSSSSS", String.valueOf((size.k - (int)(size.k/2))));
        while (startSectorY < (size.k - (int)(size.k/2))*Settings.ScreenHeightDefault)
        {
            startSectorX = X0;

            while (startSectorX < (size.m - (int)(size.m/2))*Settings.ScreenWidthDefault)
            {
                int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
                sector_map.clear();
                for(int i = 0; i < foodInGroup; ++i)
                {
                    unit = new Food(
                            getRandomX((int)startSectorX, (int)diffSectorX),
                            getRandomY((int) startSectorY, (int) diffSectorY),
                            getRandomRadius(),
                            getColor(random.nextInt(10)),//update
                            5);//update
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
                    for (int i = 0; i < sector_map.size(); i++)
                    {
                        if(temp.isOverlapped(sector_map.get(i)))
                            sector_map.remove(i);
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

    private void fillSector(float startSectorX, float startSectorY)
    {
        int foodInGroup = random.nextInt(Settings.MaxFoodInSector - Settings.MinFoodInSector) + Settings.MinFoodInSector;
        sector_map.clear();
        for(int i = 0; i < foodInGroup; ++i)
        {
            unit = new Food(
                    getRandomX((int)startSectorX, (int)diffSectorX),
                    getRandomY((int) startSectorY, (int) diffSectorY),
                    getRandomRadius(),
                    getColor(random.nextInt(10)),//update
                    5);//update
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
            for (int i = 0; i < sector_map.size(); i++)
            {
                if(temp.isOverlapped(sector_map.get(i)))
                    sector_map.remove(i);
            }
        }
        for (Unit temp: sector_map)//loop for adding
        {
            map.add(temp);
        }
    }

    public void moveRelativeUnit(float dX, float dY)
    {
        unit.x += dX;
        unit.y += dY;
    }
    public void setRelativeUnit(float X, float Y)
    {
        unit.setX(X);
        unit.setY(Y);
    }
    public Unit getRelatveUnit()
    {return unit;}

    public void removeUnit(int i)
    {
        map.remove(i);
    }

    public void updateMap()
    {
        Boolean isFound;
        for(float i = -diffSectorY; i < Settings.ScreenHeightDefault + diffSectorY; i += diffSectorY)
        {
            for(float j = -diffSectorX; j < Settings.ScreenWidthDefault + diffSectorX; i += diffSectorX)
            {
                isFound = false;
                for(Unit u : map)
                {
                    if(isFound)
                        break;
                    if(u.getX() > j&&u.getX()<j+diffSectorX)
                        isFound = true;
                }
                if(!isFound)
                {
                    fillSector(i,j);
                }
            }
        }

    }

    public int getUnitsCount()
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
            return map.get(map.size()-1);
        }
    }
}
