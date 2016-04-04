package com.bulkes.myapplication2;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 1 on 20.03.16.
 */

public class Sector
{
    private int basePriority;
    private int finishPriority;
    private float     sumFeed;
    private ArrayList<Bulk> bulkes;
    final  static int MAX_PRIORITY = 10000;
    final static int    EMPTY           =   5;
    final static int    BIG_BULK        =   10;
    Sector()
    {
        bulkes = new ArrayList<Bulk>(Settings.CountBulkes + 1);//1 - for user
        sumFeed = 0f;
        basePriority = 0;
    }
    public void restart()
    {
        bulkes.clear();
        sumFeed = 0f;
        basePriority = 0;
    }
    public void addFeed(float feed)
    {
        sumFeed += feed;
    }
    public void checkBulk(Bulk bulk)
    {
        bulkes.add(bulk);
    }
    public void findPriority(Bulk current_bulk)
    {
        for (Bulk bulk: bulkes )
        {
            if ( bulk != current_bulk)
            {
                if(bulk.getRadius() < current_bulk.getRadius() - Settings.BulkOffsetRadius)
                    sumFeed += bulk.getFeed();
                else
                    basePriority += BIG_BULK;
            }
        }
        basePriority += Math.min(Settings.MaxTotalFeed / sumFeed, EMPTY);
        //Log.v("Sum Feed ", String.valueOf(sumFeed) + " Priority " + String.valueOf(basePriority));
    }
    public int getPriority()
    {
        return finishPriority;
    }
    public int getBasePriority()
    {
        return basePriority;
    }
    public void updatePriority(int value)
    {
        finishPriority = basePriority + value;
    }
}
