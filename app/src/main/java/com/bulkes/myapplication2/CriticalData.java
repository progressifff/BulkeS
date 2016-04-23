package com.bulkes.myapplication2;

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

    public static ArrayList<Integer> usersMass;

    public static void createNewField()
    {
        usersMass = new ArrayList();
        gameMap = new GameMap();
        user = new User(Settings.ScreenWidthDefault / 2, Settings.ScreenHeightDefault / 2, Settings.UserStartSize, Settings.UserDefaultColor);
        bulkesMap = new ArrayList<Bulk>(Settings.CountBulkes + 1);
        bulkesMap.add(user);
        Random random = new Random();
        for(int i = 0; i < Settings.CountBulkes; i++) {
            Enemy enemy = new Enemy(random.nextInt(1000), random.nextInt(1000), random.nextInt(50) + 50);
            bulkesMap.add(enemy);
            gameMap.addUnit(enemy);
        }
        gameMap.addUnit(user);
        lastTime = 0;
    }
}