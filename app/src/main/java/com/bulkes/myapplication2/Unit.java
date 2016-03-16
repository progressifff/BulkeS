package com.bulkes.myapplication2;

import android.graphics.Color;

/**
 * Created by 1 on 09.03.16.
 */
public class Unit
{
    protected float x;
    protected float y;

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public void setRadius(float radius)
    {
        this.radius = radius;
    }

    public void setColor(int color)
    {
        this.color = color;
    }
    public void setIsDeleted(boolean flag)
    {
        is_deleted = flag;
    }
    protected float radius;

    public int getColor() {
        return color;
    }

    protected int color;
    protected boolean is_deleted;
    public Unit()
    {
        x = 0f;
        y = 0f;
        radius = 0f;
        color = Color.RED;
        is_deleted = false;
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
        is_deleted = false;
    }
    public Unit(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
        is_deleted = false;
    }
    float getX()
    {
        return x;
    }
    float getY()
    {
        return y;
    }
    float getRadius()
    {
        return radius;
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
        return (Math.pow((double)pointOut.getX() - x, 2.0) +  Math.pow((double)pointOut.getY() - y, 2.0)) < Math.pow((float)radius, 2.0);
    }
    public boolean isEated(Unit unit)
    {
        return (Math.pow((double)unit.getX() - x, 2.0) +  Math.pow((double)unit.getY() - y, 2.0)) < Math.pow((float)radius, 2.0);
    }

}
