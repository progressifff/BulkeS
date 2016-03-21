package com.bulkes.myapplication2;

import android.graphics.Color;

/**
 * Created by 1 on 09.03.16.
 */
public class Unit
{
    protected float x;
    protected float y;
    protected float radius;
    protected int color;
    protected boolean is_deleted;
    private   float targetRadius;

    public Unit()
    {
        x = 0f;
        y = 0f;
        radius = 0f;
        color = Color.RED;
        is_deleted = false;
        targetRadius = 0;
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
        is_deleted = false;
        targetRadius = 0;
    }
    public Unit(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
        is_deleted = false;
        targetRadius = 0;
    }
    float getX()
    {return x;}
    float getY()
    {return y;}
    float getRadius()
    {return radius;}
    float getAnimationRadius()
    {
        if(targetRadius<radius)
        {
            targetRadius += Settings.StepRadius;
            return targetRadius;
        }
        else
            return radius;
    }
    public void setX(float x)
    {this.x = x;}
    public void setY(float y)
    {this.y = y;}
    public float getFeed()//this method must override in all class
    {return 0f;}
    public void setRadius(float radius)
    {this.radius = radius;}
    public void setColor(int color)
    {this.color = color;}
    public boolean getIsDeleted()
    {
        return is_deleted;
    }
    public int getColor() {
        return color;
    }
    void move(float dx, float dy)
    {
        x += dx;
        y += dy;
    }
    public boolean isOverlapped(Unit unit)
    {
        Indicator pointOut= new Indicator();//point on radius external circle
        pointOut.getParameters(unit.getX(), unit.getY(), unit.getRadius(),x, y);//(x;y) - center current unit
        float dx;
        float dy;
        dx = pointOut.getX() - x;
        dy = pointOut.getY() - y;
        return (dx*dx + dy*dy) < (radius * radius);
    }
    public boolean isEated(Unit unit)
    {
        float dx;
        float dy;
        dx = unit.getX() - x;
        dy = unit.getY() - y;
        return (dx*dx + dy*dy) < (radius * radius);
    }
    public void setPosition(float _x, float _y)
    {
        x = _x;
        y = _y;
    }

}
