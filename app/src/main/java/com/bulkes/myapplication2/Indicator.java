package com.bulkes.myapplication2;

import android.graphics.Path;

/**
 * Created by 1 on 10.03.16.
 */
public class Indicator
{
    private float x;
    private float y;
    private float alpha;//radians

    public void getParameters(float x0, float y0, float R, float x1, float y1)
    {
        float k;
        if(Math.abs(x1 - x0) < 0.001f)
        {
            x = x0;
            if (y1 < y0)
            {
                y = -R + y0;
                alpha = (float) -Math.PI / 2.0f;//  -Pi/2
            }
            else
            {
                y = R + y0;
                alpha = (float) Math.PI / 2.0f;//  Pi/2
            }
        }
        else
        {
            k = (y1 - y0) / (x1 - x0);
            if (x1 - x0 < 0)
                x = (float) Math.sqrt(1.0f / (1f + k * k)) * (-R) + x0;
            else
                x = (float) Math.sqrt(1.0f / (1f + k * k)) * R + x0;

            y = k * x - k * x0 + y0;
            if (y1 - y0 < 0)
                alpha = -(float) Math.acos((x - x0) / R);
            else
                alpha = (float) Math.acos((x - x0) / R);
        }
        //  Log.v("Indicator ", String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(alpha) + " " + String.valueOf(x1) + " " + String.valueOf(y1));
    }

    public float getAlpha() {
        return alpha;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    public Path getTriangle(float x0, float y0, float R, float coefficient)
    {
        Path path = new Path();
        path.moveTo(x , y);//Top of triangle
        float x2;
        float y2;
        float alpha2;
        float alphaDiff;
        alphaDiff = Settings.IndicatorBaseAlpha / coefficient;
        alpha2 = alpha + alphaDiff;
        x2 = (float)Math.cos(alpha2) * R + x0;
        y2 = (float)Math.sin(alpha2) * R + y0;
        path.lineTo(x2, y2);//Left Bottom

        float x3;
        float y3;
        float alpha3;
        alpha3 = alpha - alphaDiff;
        x3 = (float)Math.cos(alpha3) * R + x0;
        y3 = (float)Math.sin(alpha3) * R + y0;
        path.lineTo(x3, y3);//Right Bottom
        return path;
    }
}