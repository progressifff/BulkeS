package com.bulkes.myapplication2;

import android.graphics.Path;

import java.util.Iterator;

/**
 * Created by 1 on 16.03.16.
 */
public class Enemy extends Bulk
{
    int     stepToTarget;
    public Enemy( float _x, float _y, float _radius)
    {
        this(_x, _y, _radius, Settings.EnemyDefaultColor);
    }
    public Enemy( float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        isMoved = true;
        setSpeed(Settings.EnemyStepValue, Settings.EnemyStepValue);
        stepToTarget = 0;

    }
    public void setTarget(Unit unit)
    {
        target = unit;
    }
    private boolean isVisibleUnit(Unit unit)
    {
        if(Math.abs(x - unit.getX()) > Settings.EnemyFindOffset + radius)
            return false;
        if(Math.abs(y - unit.getY()) > Settings.EnemyFindOffset + radius)
            return false;
        return true;
    }
    private void findNewTarget(GameMap gameMap, SectorHolder sectors)
    {
        stepToTarget = 0;
        target = null;

        sectors.solveSectorToMove(this);

        float attraction;
        float maxAttraction = Integer.MIN_VALUE;
        int currentPriority;
        Iterator<Unit> iterator = gameMap.getMap().iterator();
        while (iterator.hasNext()) {
            Unit point = iterator.next();
            if (point != this && !point.isDeleted)
                if ( isVisibleUnit(point) && radius > point.radius) {
                    currentPriority = sectors.getPriorityForUnit(point);
                    float distance = Math.abs(x - point.getX()) + Math.abs(y - point.getY());//not real distance use only for choice
                    float feedByDistance = point.getFeed() / distance;
                    //Log.v("Feed by Distance ", String.valueOf(feedByDistance));
                    attraction = feedByDistance - currentPriority * Settings.PriorityValue;
                    if(attraction > maxAttraction)
                    {
                        maxAttraction = attraction;
                        target = point;
                    }
                }
        }
    }

    public void updateState(GameMap gameMap, SectorHolder sectors)
    {
        if(stepToTarget == Settings.EnemyMaxStepToTarget || target == null) //update null
        {
            findNewTarget(gameMap, sectors);
        }
        if(target == null) //no unit near bulk
            target = gameMap.getAnyUnit();
        //update: if field is empty
        if( moveToTarget())
        {
            findNewTarget(gameMap, sectors);
            stepToTarget = 0;
        }
        else
            stepToTarget++;
//        target.setColor(Color.BLACK);
        //Log.v("Enemy X Y", String.valueOf(getX()) + " " + String.valueOf(getY()));
    }
    public Path getIndicatorToTarget() {
        return getIndicator(target.getX(), target.getY());
    }
    public boolean isTarget(Unit unit)
    {
        return target == unit;
    }
}