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

    public void setX(float x)
    {
        this.x = x;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getFeed()//this method must override in all class
    {
        return 0f;
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
    public boolean getIsDeleted()
    {
        return is_deleted;
    }


    public int getColor() {
        return color;
    }


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
        if(       unit.getX() < x - (radius + unit.radius)
                ||unit.getX() > x + (radius + unit.radius)
                ||unit.getY() < y - (radius + unit.radius)
                ||unit.getY() > y + (radius + unit.radius))
            return false;

            Indicator pointOut = new Indicator();//point on radius external circle
            pointOut.getParameters(unit.getX(), unit.getY(), unit.getRadius(), x, y);//(x;y) - center current unit
            float dx;
            float dy;
            dx = pointOut.getX() - x;
            dy = pointOut.getY() - y;
            return (dx * dx + dy * dy) < (radius * radius);
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
