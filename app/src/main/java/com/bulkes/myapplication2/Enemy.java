package com.bulkes.myapplication2;

import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;

import java.util.Iterator;

/**
 * Created by 1 on 16.03.16.
 */
public class Enemy extends Bulk
{
    Unit    target;//goal for eating

    public Enemy( float _x, float _y, float _radius)
    {
        this(_x, _y, _radius, Settings.EnemyDefaultColor);
    }

    public Enemy( float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        indicator = new Indicator();
        setSpeed(Settings.EnemyDefaultSpeed);
    }
    public void setTarget(Unit unit)
    {
        target = unit;
    }
    public void updateState(GameMap gameMap, SectorHolder sectors)
    {
        if(isMoved && !target.is_deleted) {//other enemy can target eat
            float dx;
            float dy;
            float newX;
            float newY;
            dx = target.getX() - getX();
            dy = target.getY() - getY();
            if (Math.abs(dx) < 0.001f && Math.abs(dy) < 0.001f)
                setIsMoved(false);
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0)
                    newX = getX() + Settings.EnemyStepValue;
                else
                    newX = getX() - Settings.EnemyStepValue;
                setPosition(newX, solveY(newX));
            } else {
                if (dy > 0)
                    newY = getY() + Settings.EnemyStepValue;
                else
                    newY = getY() - Settings.EnemyStepValue;
                setPosition(solveX(newY), newY);
            }
        }
        else {
            isMoved = true;
            sectors.findSectorToMove(this);
            target = null;
            int minimumPriority = Integer.MAX_VALUE;
            float minimumDistance = (float) Integer.MAX_VALUE;
            int currentPriority;
            for (int i = 0; i < gameMap.getLines(); i++) {
                for (int j = 0; j < gameMap.getColumns(); j++) {
                    Iterator<Unit> iterator = gameMap.getMap()[i][j].iterator();
                    while (iterator.hasNext()) {
                        Unit point = iterator.next();
                        if (point != this && !point.is_deleted)
                            if (Math.abs(x - point.getX()) < Settings.EnemyFindOffset && Math.abs(y - point.getY()) < Settings.EnemyFindOffset) {
                                currentPriority = sectors.getPriorityForUnit(point);
                                if (currentPriority < minimumPriority) {
                                    minimumPriority = sectors.getPriorityForUnit(point);
                                    float distance = Math.abs(x - point.getX()) + Math.abs(y - point.getY());//not real distance use only for choice
                                    target = point;
                                    minimumDistance = distance;
                                } else {
                                    if (currentPriority == minimumPriority) {
                                        float distance = Math.abs(x - point.getX()) + Math.abs(y - point.getY());//not real distance use only for choice
                                        if (distance < minimumDistance) {
                                            minimumDistance = distance;
                                            target = point;
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
        if(target == null)
           // target = new Unit(x+100, y + 200, 0);
            isMoved = false;//update find random
        else
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
    public Path getTriangleToTarget() {
        return getTriangle(target.getX(), target.getY());
    }
    public boolean isTarget(Unit unit)
    {
        return target == unit;
    }
}
