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
    protected boolean isDeleted;
    private   float targetRadius;
    protected Bulk    target;

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
        isDeleted = flag;
    }
    public boolean getIsDeleted()
    {
        return isDeleted;
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
        isDeleted = false;
        targetRadius = 0f;
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
        isDeleted = false;
        targetRadius = 0f;
    }
    public Unit(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
        isDeleted = false;
        targetRadius = 0f;
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
    float getAnimationRadius()
    {
        if(targetRadius<radius)
        {
            targetRadius += Settings.StepRadius*radius;
            return targetRadius;
        }
        else
            return radius;
    }
    void move(float dx, float dy)
    {
        x += dx;
        y += dy;
    }
    public void setIsDeleted(boolean isDeleted, Bulk whatBulk)
    {
        target = whatBulk;
        this.isDeleted = isDeleted;
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
    public boolean insideBulk()
    {
        //  float k;
        float newX;
        float newY;
        float dx;
        float dy;
        float stepY;
        float stepX;
        dx = target.getX() - x;
        dy = target.getY() - y;
        stepX = 10f;
        stepY = 10f;
        if(Math.sqrt(Math.pow(dx, 2.0) +  Math.pow(dy, 2.0))<=(target.getRadius()-radius))
            return true;
        else {
            //    k = dy/dx;
            if(Math.abs(dx)>=Math.abs(dy))
            {
                newX = x + ((dx > 0)? stepX : (-stepX));
                //newY = k*(newX - x) + y;
                newY = solveY(newX);
            }
            else
            {
                newY = y + ((dy > 0)? stepY : (-stepY));
                //  newX = (newY - y)/k + x;
                newX = solveX(newY);
            }
            setPosition(newX,newY);
            return false;
        }
    }
    private float solveY(float _x)
    {
        float k;
        k = (target.getY() - getY()) / (target.getX() - getX());
        return k * _x - k * getX() + getY();
    }
    private float solveX(float _y)
    {
        float k;
        if(Math.abs(target.getX() - getX()) < 0.001f )
            return getX();
        else {
            k = (target.getY() - getY()) / (target.getX() - getX());
            return (_y - getY()) / k + getX();
        }
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
    @Override
    public String toString()
    {
        return String.valueOf(x) + " "  + String.valueOf(y) + " " + String.valueOf(radius) + " is_del " + String.valueOf(isDeleted);
    }

}
