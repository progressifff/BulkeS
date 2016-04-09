package com.bulkes.myapplication2;

import android.graphics.Color;
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
    private void findNewTarget(GameMap gameMap, SectorHolder sectors)
    {
        stepToTarget = 0;
        target = null;

        sectors.findSectorToMove(this);

        int minimumPriority = Integer.MAX_VALUE;
        float minimumDistance = (float) Integer.MAX_VALUE;
        int currentPriority;
        float maxFeedByDistance = -1f;//warning default
        Iterator<Unit> iterator = gameMap.getMap().iterator();
        while (iterator.hasNext()) {
            Unit point = iterator.next();
            if (point != this && !point.isDeleted)
                if (Math.abs(x - point.getX()) < Settings.EnemyFindOffset + radius && Math.abs(y - point.getY()) < Settings.EnemyFindOffset + radius && radius > point.radius) {
                    currentPriority = sectors.getPriorityForUnit(point);
                    if (currentPriority < minimumPriority) {
                        minimumPriority = sectors.getPriorityForUnit(point);
                        float distance = Math.abs(x - point.getX()) + Math.abs(y - point.getY());//not real distance use only for choice
                        float feedByDistance = point.getFeed() / distance;
                        maxFeedByDistance = feedByDistance;
                        target = point;
                        //minimumDistance = distance;
                    } else {
                        if (currentPriority == minimumPriority) {
                            float distance = Math.abs(x - point.getX()) + Math.abs(y - point.getY());//not real distance use only for choice
                            float feedByDistance = point.getFeed() / distance;
                            if (feedByDistance > maxFeedByDistance) {
                                maxFeedByDistance = feedByDistance;
                                target = point;
                            }
                        }
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
        if(target!=null)
            target.setColor(Color.BLACK);
        //Log.v("Enemy X Y", String.valueOf(getX()) + " " + String.valueOf(getY()));
    }
    private float solveY(float _x)
    {
        float k;
        k = (target.getY() - getY()) / (target.getX() - getX());
        return k * _x - k * getX() + getY();
    }
    private float solveX(float _y)
    {
        float k;
        if(Math.abs(target.getX() - getX()) < 0.001f )
            return getX();
        else {
            k = (target.getY() - getY()) / (target.getX() - getX());
            return (_y - getY()) / k + getX();
        }
    }
    public Path getIndicatorToTarget() {
        return getIndicator(target.getX(), target.getY());
    }
    public boolean isTarget(Unit unit)
    {
        return target == unit;
    }
}