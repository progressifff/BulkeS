package com.bulkes.myapplication2;
/**
 * Created by progr on 10.03.2016.
 */
public class Bulk extends Unit
{
    protected float speed;
    protected Boolean isMoved;
    protected float offsetCenter;
    public Bulk(float _x, float _y, float _radius, int _color)
    {
        super(_x,_y, _radius, _color);
        isMoved = false;
        speed = 0.1f;
    }
    public Bulk( float _x, float _y, float _radius)
    {
        super(_x, _y, _radius);
    }
    //-------------------------------------------
    public float getSpeed()
    {return speed;}
    public void setSpeed(float _speed)
    {speed = _speed;}
    //-------------------------------------------
    public Boolean getIsMoved()
    {return isMoved;}
    public void setIsMoved(Boolean flag)
    {isMoved = flag;}
}
