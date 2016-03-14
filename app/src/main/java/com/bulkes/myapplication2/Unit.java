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
}
