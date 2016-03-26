package com.bulkes.myapplication2;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by 1 on 14.03.16.
 */
public final class Settings
{
    final static int    ScreenWidthDefault         = 1920;
    final static int    ScreenHeightDefault        = 1080;
    final static float  UserStartSize              = 50f;
    final static float  UserDefaultSpeed           = 0.1f;
    final static int    UserDefaultColor           = Color.RED;
    final static float  UserScale                  = 0.5f;
    final static int    MinFoodInSector            = 0;
    final static int    MaxFoodInSector            = 5;
    final static int    MinFoodSize                = 20;
    final static int    MaxFoodSize                = 40;
    final static int    BaseFoodSize               = (MaxFoodSize - MinFoodSize) / 2;
    final static int    FoodDefaultFeed            = 500;
    final static int    FoodFeedForRadius          = 20;
    final static int    CountSectorX               = 3;
    final static int    CountSectorY               = 3;

    final static int    MaxTotalFeed               = MaxFoodSize * FoodFeedForRadius * MaxFoodInSector * 2 ;

    final static float  BulkOffsetRadius           = 1f;
    final static int    EnemyFindOffset            = 500;
    final static float  EnemyDefaultSpeed          = 0.1f;
    final static float  EnemyStepValue             = 5f;
    final static int    EnemyDefaultColor          = Color.MAGENTA;
    final static float  IndicatorTopOffset         = 15f;
    final static float  IndicatorBaseOffset        = 5f;
    final static float  IndicatorBaseAlpha         = 0.3f;
    final static float  JoyStickRadiusOut          = 120f;
    final static float  JoyStickRadiusIn           = 60f;
    final static int    CountBulkes                = 5;
    final static int    ColorList[] = {
            Color.rgb(0xFF,0xA5,0x00),//#FFA500 orange
            Color.rgb(0x1F,0x57,0xB3),//#1f57b3 blue
            Color.rgb(0xFF,0x73,0x73),//#ff7373 pink
            Color.rgb(0xCC,0xFF,0x00),//#ccff00 light green
            Color.rgb(0x33,0x99,0xFF),//#3399ff light blue
            Color.rgb(0x64,0x95,0xED),//#6495ed light blue
            Color.rgb(0x8A,0x2B,0xE2),//#8a2be2 magenta
            Color.rgb(0x7B,0xC0,0x43),//#7bc043 green
            Color.rgb(0xFB,0xE5,0x66),//#fbe566 light yellow
            Color.rgb(0xFF,0xA7,0x00)//#ffa700 gold
    };
    public static int getCountColors()
    {
        return ColorList.length;
    }
}
