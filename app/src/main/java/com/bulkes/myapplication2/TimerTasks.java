package com.bulkes.myapplication2;

import java.sql.Time;
import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by 1 on 15.03.16.
 */
public class TimerTasks extends TimerTask
{

    @Override
    public void run()
    {
        Calendar calendar = Calendar.getInstance();
        GameView.currentTime = calendar.getTimeInMillis();
    }
}
