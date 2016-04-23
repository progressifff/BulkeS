package com.bulkes.myapplication2;

import android.graphics.Color;

/**
 * Created by 1 on 14.03.16.
 */
public final class Settings
{
    //------------------ScreenParameters--------------------
    final   static int    ScreenWidthDefault         = 1920;
    final   static int    ScreenHeightDefault        = 1080;
    //--------------------Game------------------------------
       static int    GameFieldColor             = Color.WHITE;
    //--------------------User--------------------------------
    final   static float  UserStartSize              = 100f;
    final   static float  UserDefaultSpeed           = 0.1f;
            static int    UserDefaultColor           = Color.RED;
    final   static float  UserBaseSize               = 100f;//for indicator
    final   static float  UserMaxRadius              = 200f;
    final   static float  UserSpeedCoefficient       = 0.1f;
    static  float  UserScale                         = 1f;
    //------------------Food--------------------------------
    final   static int    TimeCreateNewFood          = 5;
    final   static int    TimeDelayFirstNewFood      = 10;
    final   static int    MinFoodInSector            = 0;
    final   static int    MaxFoodInSector            = 6;
    final   static float  MinAddFoodScaleValue       = 0.6f;
    final   static float  MaxAddFoodScaleValue       = 1.3f;
    final   static int    MinFoodSize                = 20;
    final   static int    MaxFoodSize                = 40;
    final   static int    BaseFoodSize               = (MaxFoodSize - MinFoodSize) / 2;
    final   static int    FoodDefaultFeed            = 500;
    final   static int    FoodFeedForRadius          = 20;
    //------------------Map----------------------------------
    final   static int    MapWidthP                  = Settings.MapSizeX * Settings.ScreenWidthDefault;
    final   static int    MapHeightP                 = Settings.MapSizeY * Settings.ScreenHeightDefault;
    final   static int    CountSectorX               = 3;
    final   static int    CountSectorY               = 3;
    final   static int    MapSizeX                   = 3;
    final   static int    MapSizeY                   = 3;
    final   static float  StepRadius                 = 0.01f;
    final   static float  UnitToTargetCoefficient    = 1.5f;
    final   static float  MinFoodSpeed               = 2f;
    final   static int    MaxTotalFeed               = MaxFoodSize * FoodFeedForRadius * MaxFoodInSector * 2 ;
    final   static int    BulkDefaultColor           = Color.YELLOW;
    final   static float  BulkOffsetRadius           = 5f;//min difference
    //------------------Enemy--------------------------------
    final   static int    EnemyMaxStepToTarget       = 30;
    final   static int    EnemyFindOffset            = 500;
    final   static float  EnemyStepValue             = 5f;
    final   static int    EnemyDefaultColor          = Color.MAGENTA;
    //------------------JoyStick/Indicator--------------------------------
    final   static float  IndicatorTopOffset         = 15f;
    final   static float  IndicatorBaseOffset        = 5f;
    final   static float  IndicatorBaseAlpha         = 0.3f;
    final   static float  JoyStickRadiusOut          = 120f;
    final   static float  JoyStickRadiusIn           = 60f;
    final   static int    CountBulkes                = 10;
    //------------------Dialogs---------------------------------
    final   static int    DialogPauseID              = 0;
    final   static int    DialogEndID                = 1;
    final   static int    DialogGameOverID           = 2;
    //------------------Colors----------------------------------
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

    final static int UsersBulkColors[] = {
            Color.rgb(239,48,56),//red
            Color.rgb(250,72,102),//light red
            Color.rgb(240,108,0),//orange
            Color.rgb(234,117,0),//mandarin
            Color.rgb(255,215,0),//gold
            Color.rgb(255,255,0),//yellow
            Color.rgb(191,255,0),//light green
            Color.rgb(102,255,0),//lime
            Color.rgb(0,255,255),//cyan
            Color.rgb(120,219,226),//material blue
            Color.rgb(28,169,201),//dark blue
            Color.rgb(29,172,214),//blue Krayola
            Color.rgb(147,112,216),//purpur
            Color.rgb(184,143,255)//violet
    };
}
