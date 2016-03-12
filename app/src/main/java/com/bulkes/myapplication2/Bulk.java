package com.bulkes.myapplication2;

/**
 * Created by progr on 10.03.2016.
 */
public class Bulk extends GamePoint
{
    protected  int xDirection;
    protected  int yDirection;
    protected  int speed;
    protected  Boolean isMoved;
    public Bulk(float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        isMoved = false;
        speed = 10;
    }
    //-------------------------------------------
    public int getXDirection()
    {return xDirection;}
    public void setXDirection(int direct)
    {xDirection = direct;}
    //-------------------------------------------
    public int getYDirection()
    {return yDirection;}
    public void setYDirection(int direct)
    {yDirection = direct;}
    //-------------------------------------------
    public int getSpeed()
    {return speed;}
    public void setSpeed(int _speed)
    {speed = _speed;}
    //-------------------------------------------
    public Boolean getIsMoved()
    {return isMoved;}
    public void setIsMoved(Boolean flag)
    {isMoved = flag;}
}
