package com.bulkes.myapplication2;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by progr on 25.03.2016.
 */
public final class CriticalData {
    public static User user;
    public static GameMap gameMap;
    public static ArrayList<Bulk> bulkesMap;
    public static float scaling;
    public static long lastTime;
    public static long previousTime;
    public static boolean isRun;
    public static ArrayList<Integer> usersMass;
    public static void createNewField()
    {
        Settings.UserScale = 1f;
        Log.v("Timer diargram", "Clear");
        usersMass = new ArrayList();
        gameMap = new GameMap();
        user = new User(Settings.ScreenWidthDefault / 2, Settings.ScreenHeightDefault / 2, Settings.UserStartSize, Settings.UserDefaultColor);
        bulkesMap = new ArrayList<Bulk>(Settings.CountBulkes + 1);//1 - for user
        bulkesMap.add(user);
        Random random = new Random();
        for(int i = 0; i < Settings.CountBulkes; ++i) {
            Enemy enemy = new Enemy(random.nextInt(1000), random.nextInt(1000), random.nextInt(50) + 50);
            bulkesMap.add(enemy);
            gameMap.addUnit(enemy);
        }
        gameMap.addUnit(user);
        gameMap.fillFood(bulkesMap);
        lastTime = 0;
        previousTime = 0;
        isRun = true;
    }
    public static void createTrainingField()
    {
        Settings.CountBulkes    = 0;
        Settings.UserStartSize  = 180f;
        Settings.MinFoodSize = 20;
        Settings.MaxFoodSize = 40;
        createNewField();
    }

    public static void createSurvivalField()
    {
        Settings.CountBulkes = 5;
        Settings.UserStartSize = 100f;
        Settings.MinFoodSize = 20;
        Settings.MaxFoodSize = 40;
        createNewField();
    }

    public static void createRunField()
    {
        Settings.CountBulkes    = 5;
        Settings.UserStartSize = 40f;
        Settings.MinFoodSize = 75;
        Settings.MaxFoodSize = 80;
        createNewField();
    }
}