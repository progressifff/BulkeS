package com.bulkes.myapplication2;

import android.graphics.Color;
import android.util.Log;

/**
 This is a base unit for all game element
 */
public class Unit
{
    protected float x;
    protected float y;
    protected float radius;
    private   float animationRadius;
    protected boolean isDeleted;
    protected int color;
    protected Unit    target;

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    protected  float        speedX;
    protected  float        speedY;

    //constructors
    public Unit()
    {
        this(0f, 0f, 0f);
    }
    public Unit(float _x, float _y, float _radius) {
        this(_x, _y, _radius, Color.GRAY);
    }
    public Unit(float _x, float _y, float _radius, int _color)
    {
        x = _x;
        y = _y;
        radius = _radius * Settings.UserScale;
        color = _color;
        isDeleted = false;
        animationRadius = 0f;
    }

    //setters
    public void setX(float x)
    {
        this.x = x;
    }
    public void setY(float y)
    {
        this.y = y;
    }
    public void setRadius(float radius)
    {
        this.radius = radius * Settings.UserScale;
    }
    public void updatePosition(Unit unit)//update location + radius
    {
        radius *= Settings.UserScale;
        x = unit.x + ((x - unit.x) * Settings.UserScale);
        y = unit.y + ((y - unit.y) * Settings.UserScale);
        if(isOnMainScreen())
            animationRadius = radius;
    }
    public void setSpeed(float _speedX, float _speedY)
    {
        speedX = _speedX;
        speedY = _speedY;
    }
    public void setColor(int color)
    {
        this.color = color;
    }
    public void setPosition(float _x, float _y)
    {
        x = _x;
        y = _y;
    }
    public void setIsDeleted(boolean isDeleted, Unit whatBulk)
    {
        target = whatBulk;
        this.isDeleted = isDeleted;
    }
    public void setAnimationRadius(float animationRadius)
    {
        this.animationRadius = animationRadius;
    }

    //getters
    public float getX()
    {
        return x;
    }
    public float getY()
    {
        return y;
    }
    public float getRadius()
    {
        return radius;
    }
    public float getAnimationRadius()
    {
        if(animationRadius < radius)
        {
            animationRadius += Settings.StepRadius*radius;
            if(animationRadius < radius)
                return animationRadius;
            else
                animationRadius = radius;
        }
        else
            if(animationRadius > radius)
            {
                animationRadius -= Settings.StepRadius*radius;
                if(animationRadius >= radius)
                    return animationRadius;
                else
                    animationRadius = radius;
            }
        return radius;
    }
    public float getFeed()//this method must override in all class
    {
        return 0f;
    }
    public boolean getIsDeleted()
    {
        return isDeleted;
    }
    public int getColor() {
        return color;
    }

    //public methods
    public void move(float dx, float dy)
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
    public boolean isEaten(Unit unit)
    {
        float dx;
        float dy;
        dx = unit.getX() - x;
        dy = unit.getY() - y;
        return (dx*dx + dy*dy) < (radius * radius);
    }

    @Override
    public String toString()
    {
        return String.valueOf(x) + " "  + String.valueOf(y) + " " + String.valueOf(radius) + " is_del " + String.valueOf(isDeleted);
    }

    //protected
    protected boolean isOnMainScreen()
    {
        if(x + radius < 0f || x - radius > Settings.ScreenWidthDefault)
            return false;
        if(y + radius < 0f || y - radius > Settings.ScreenHeightDefault)
            return false;
        return true;
    }
    protected boolean catchTarget()
    {
        if(isOnMainScreen()) {
            speedX = Math.max(Settings.MinFoodSpeed, target.getSpeedX() * Settings.UnitToTargetCoefficient);
            speedY = Math.max(Settings.MinFoodSpeed, target.getSpeedY() * Settings.UnitToTargetCoefficient);
            return moveToTarget();
        }
        else
            return true;
    }
    protected boolean moveToTarget()
    {
        float dx;
        float dy;
        float newX;
        float newY;
        dx = target.getX() - x;
        dy = target.getY() - y;
        if ( dx*dx + dy*dy < (target.getRadius()-radius) * (target.getRadius()-radius) )
            return true;
            //catchTarget();
        if (Math.abs(dx) > Math.abs(dy)) {
            if (dx > 0)
                newX = x + speedX;
            else
                newX = x - speedX;
            setPosition(newX, solveY(newX));
        } else {
            if (dy > 0)
                newY = y + speedY;
            else
                newY = y - speedY;
            setPosition(solveX(newY), newY);
        }
        return false;
    }

    //private
    private float solveY(float _x)//x  - increment; y = f(x)
    {
        float k;
        k = (target.getY() - getY()) / (target.getX() - getX());
        return k * _x - k * getX() + getY();
    }
    private float solveX(float _y)//y  - increment; x = f(y)
    {
        float k;
        if(Math.abs(target.getX() - getX()) < 0.001f )
            return getX();
        else {
            k = (target.getY() - getY()) / (target.getX() - getX());
            return (_y - getY()) / k + getX();
        }
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
        if(Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0))<=(target.getRadius()-radius))
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

}
