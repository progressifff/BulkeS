package com.bulkes.myapplication2;

/**
 * Created by 1 on 14.03.16.
 */
public class Food extends Unit
{
    private  int feed;

    public Food(int feed) {
        this.feed = feed;
    }

    public Food(float _x, float _y, float _radius, int _color, int feed) {
        super(_x, _y, _radius, _color);
        this.feed = feed;
    }

    public Food(float _x, float _y, float _radius, int feed) {
        super(_x, _y, _radius);
        this.feed = feed;
    }
}
