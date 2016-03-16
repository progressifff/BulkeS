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
    public Unit()
    {
        x = 0f;
        y = 0f;
        radius = 0f;
        color = Color.RED;
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
    }
    public Unit(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
    }

    float getX()
    {
        return x;
    }
    float getY()
    {return y;}
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
        Indicator pointOut = new Indicator();//point on radius external circle
        pointOut.getParameters(unit.getX(), unit.getY(), unit.getRadius(),x, y);//(x;y) - center current unit
        return (Math.pow((double)pointOut.getX() - x, 2.0) +  Math.pow((double)pointOut.getY() - y, 2.0)) < Math.pow((float)radius, 2.0);
    }
    public void setX(float x)
    {this.x = x;}
    public void setY(float y)
    {this.y = y;}
    public void setRadius(float radius)
    {this.radius = radius;}
    public void setColor(int color)
    {this.color = color;}
}
