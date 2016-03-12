package com.bulkes.myapplication2;

import android.graphics.Color;

/**
 * Created by 1 on 09.03.16.
 */
public class GamePoint
{
    protected   float       x;
    protected   float       y;
    protected   float       radius;
    protected   int         color;
    protected   boolean     is_deleted;
    GamePoint()
    {
        x = 0f;
        y = 0f;
        radius = 0f;
        color = Color.RED;
        is_deleted = false;
    }
    GamePoint(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
        is_deleted = false;
    }
    GamePoint(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
        is_deleted = false;
    }
    float X()
    {
        return x;
    }
    float Y()
    {
        return y;
    }
    float Radius()
    {
        return radius;
    }
    void Move(float dx, float dy)
    {
        x += dx;
        y += dy;
    }
}
