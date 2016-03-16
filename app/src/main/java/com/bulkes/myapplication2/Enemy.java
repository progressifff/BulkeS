package com.bulkes.myapplication2;

/**
 * Created by 1 on 16.03.16.
 */
public class Enemy extends Bulk
{
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
}
