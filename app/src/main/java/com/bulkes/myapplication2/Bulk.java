package com.bulkes.myapplication2;

import android.graphics.Path;
import android.util.Log;

/**
 * Created by progr on 10.03.2016.
 */
public class Bulk extends Unit
{
    protected  boolean      isMoved;
    protected  float        mass;
    protected Indicator indicator;
    public Bulk(float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        if( this instanceof User )
            Log.v("User constr ", String.valueOf(radius));

        mass = (float)Math.PI * _radius * _radius;
        isMoved = false;
        indicator = new Indicator();
    }
    public Bulk( float _x, float _y, float _radius)
    {
        this(_x, _y, _radius, Settings.BulkDefaultColor);
    }

    public boolean getIsMoved()
    {
        return isMoved;
    }

    public void setIsMoved(boolean flag)
    {
        isMoved = flag;
    }

    public void addMass(float feed)
    {
        setMass(feed + mass);
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass)
    {
        this.mass = mass;
        radius = (float) Math.sqrt((double) mass / Math.PI) * Settings.UserScale;
        if( this instanceof User) {
            Log.v("Mass: ", String.valueOf(mass));
            Log.v("Radius: ", String.valueOf(radius));
        }
    }

    @Override
    public float getFeed()
    {
        return mass;
    }

    public Path getIndicator(float x_end, float y_end)
    {
        float coefficient = Math.max(radius / Settings.UserBaseSize, 2f);
        indicator.getParameters(x,y,getAnimationRadius() + Settings.IndicatorTopOffset * coefficient, x_end, y_end);
        return indicator.getTriangle(x, y, getAnimationRadius() + (Settings.IndicatorBaseOffset * coefficient), coefficient);
    }
}