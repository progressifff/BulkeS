package com.bulkes.myapplication2;

import android.graphics.Path;

/**
 * Created by 1 on 10.03.16.
 */
public class User extends Bulk
{
    private Indicator indicator;
    public User( float _x, float _y, float _radius)
    {
        super(_x, _y, _radius);
        indicator = new Indicator();
        setSpeed(0.1f);
    }

    public User( float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        indicator = new Indicator();
        setSpeed(0.1f);
    }

    public Indicator getIndicatorPosition(float x_end, float y_end)
    {
        indicator.getParameters(x,y,radius + 15, x_end, y_end);
        return indicator;
    }
    public Path getTriangle()
    {
        return indicator.getTriangle(x, y, radius + 5 );
    }
}
