package com.bulkes.myapplication2;

/**
 * Created by progr on 14.03.2016.
 */
public class JoyStick {
    private float X;
    private float Y;
    private float X0;
    private float Y0;
    private float radiusOut;
    private float radiusIn;
    private float dX, dY;
    public JoyStick()
    {
        radiusOut = 120;
        radiusIn = 60;
    }
    public JoyStick(float _radiusOut, float _radiusIn)
    {
        radiusOut = _radiusOut;
        radiusIn = _radiusIn;
    }

    public void getParameters(float x0, float y0, float x1, float y1)
    {
        float k;
        if(Math.sqrt(Math.pow((x1 - x0), 2) + Math.pow((y1 - y0), 2)) > radiusOut)
        {
            if((x1 - x0) != 0)
            {
                k = (y1-y0)/(x1 - x0);
                dX = (float)Math.sqrt(Math.pow(radiusOut,2)/(Math.pow(k,2)+1f));
                dY = (float)Math.sqrt((Math.pow(radiusOut,2) * Math.pow(k,2))/(1f + Math.pow(k,2)));
            }
            else
            {
                dX = 0;
                dY = radiusOut;
            }
            dX = (x1 < x0) ? (- dX) : (dX);
            dY = (y1 < y0) ? (- dY) : (dY);
        }
        else
        {
            dX = x1-x0;
            dY = y1-y0;
        }
        X = x0 + dX;
        Y = y0 + dY;
        X0 = x0;
        Y0 = y0;
    }

    public float getX()
    {return X;}
    public float getY()
    {return Y;}
    public float getX0()
    {return X0;}
    public float getY0()
    {return Y0;}
    public float getdX()
    {return dX;}
    public float getdY()
    {return dY;}
    public float getRadiusIn()
    {return radiusIn;}
    public float getRadiusOut()
    {return radiusOut;}
}
