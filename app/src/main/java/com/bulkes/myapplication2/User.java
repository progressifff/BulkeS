package com.bulkes.myapplication2;

import android.graphics.Path;

/**
 * Created by 1 on 10.03.16.
 */
public class User extends Bulk
{
    public User( float _x, float _y, float _radius)
    {
        this(_x, _y, _radius, Settings.UserDefaultColor);
    }

    public User( float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        indicator = new Indicator();
        setSpeed(Settings.UserDefaultSpeed);
    }
}
