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
   // private Indicator pointOut;
    public Unit()
    {
        x = 0f;
        y = 0f;
        radius = 0f;
        color = Color.RED;
        isDeleted = false;
        targetRadius = 0;
      //  pointOut = new Indicator();
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius;
        color = _color;
        isDeleted = false;
        targetRadius = 0;
      //  pointOut = new Indicator();
    }
    public Unit(float _x, float _y, float _radius) {
        x = _x;
        y = _y;
        radius = _radius;
        color = Color.RED;
        isDeleted = false;
        targetRadius = 0;
     //   pointOut = new Indicator();
    }
    float getX()
    {return x;}
    float getY()
    {return y;}
    float getRadius()
    {return radius;}
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
    public void setX(float x)
    {this.x = x;}
    public void setY(float y)
    {this.y = y;}
    public float getFeed()//this method must override in all class
    {return 0f;}
    public void setRadius(float radius)
    {this.radius = radius;}
    public void setColor(int color)
    {this.color = color;}
    public void setIsDeleted(boolean isDeleted, Bulk whatBulk)
    {
        target = whatBulk;
        this.isDeleted = isDeleted;
    }
    public boolean getIsDeleted()
    {
        return isDeleted;
    }
    public int getColor() {
        return color;
    }
    void move(float dx, float dy)
    {
        x += dx;
        y += dy;
    }

    public boolean insideBulk(float deltaX, float deltaY)
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
        stepX = 0.8f*Math.abs(deltaX*target.getSpeed());
        stepY = 0.8f*Math.abs(deltaY*target.getSpeed());
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

    protected float solveY(float _x)
    {
        float k;
        k = (target.getY() - getY()) / (target.getX() - getX());
        return k * _x - k * getX() + getY();
    }
    protected float solveX(float _y)
    {
        float k;
        if(Math.abs(target.getX() - getX()) < 0.001f )
            return getX();
        else {
            k = (target.getY() - getY()) / (target.getX() - getX());
            return (_y - getY()) / k + getX();
        }
    }


    public boolean overlap(Unit unit)
    {
        Indicator pointOut = new Indicator();//point on radius external circle
        pointOut.getParameters(unit.getX(), unit.getY(), unit.getRadius(),x, y);//(x;y) - center current unit
        return (Math.pow((double)pointOut.getX() - x, 2.0) +  Math.pow((double)pointOut.getY() - y, 2.0)) < Math.pow((float)radius, 2.0);
    }
    public boolean isEated(Unit unit)
    {
        return (Math.pow((double)unit.getX() - x, 2.0) +  Math.pow((double)unit.getY() - y, 2.0)) < Math.pow((float)radius, 2.0);
    }
    public void setPosition(float _x, float _y)
    {
        x = _x;
        y = _y;
    }

}
