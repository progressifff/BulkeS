package com.bulkes.myapplication2;

import android.util.Log;

/**
 * Created by 1 on 20.03.16.
 */
public class SectorHolder
{
    private Sector [][]sector_map;
    private float dw;//offset x for translate center to left top angle; x - width
    private float dh;//offset y for translate center to left top angle; y - height
    private int countLine;
    private int countColumn;

    SectorHolder()
    {
        countLine = Settings.MapSizeY * Settings.CountSectorY;
        countColumn = Settings.MapSizeX * Settings.CountSectorX;
        sector_map = new Sector[countLine][countColumn];
        for (int line = 0; line < countLine; ++line )
            for (int column = 0; column < countColumn ; ++column)
                sector_map[line][column] = new Sector();
        Log.v("Sector Holder. L/C", String.valueOf(countLine) + " " + String.valueOf(countColumn));
    }

    public void setOffsets(float _dw, float _dh)
    {
        dw = _dw;
        dh = _dh;
        Log.v("Sector Holder. dw dh", String.valueOf(dw) + " " + String.valueOf(dh));
    }

    public Sector getSector(Unit unit)//update crash was here
    {
        float tempX;
        float tempY;
        tempX = unit.getX() + dw;
        tempY = unit.getY() + dh;
        int line;
        int column;
        line    = (int)tempY / (Settings.ScreenHeightDefault / Settings.CountSectorY);
        column  = (int)tempX / (Settings.ScreenWidthDefault / Settings.CountSectorX);
        // Log.v("Sector XY ", String.valueOf(tempY) + " " + String.valueOf(tempX));
        // Log.v("Sector ", String.valueOf(line) + " " + String.valueOf(column));
        if( line >= countLine || column >= countColumn  ) {
            Log.e("Sector error ", unit.toString());
            Log.e("Sector error L/C", String.valueOf(line) + " " + String.valueOf(column));
        }
        return sector_map[line][column];
    }
    public void restartChecking()
    {
        for (int line = 0; line < countLine; ++line )
            for (int column = 0; column < countColumn ; ++column)
                sector_map[line][column].restart();
    }
    public void checkUnit(Unit unit)
    {
        Sector currentSector;
        currentSector = getSector(unit);
        if( unit instanceof Bulk )
            currentSector.checkBulk((Bulk)unit);
        else
            currentSector.addFeed(unit.getFeed());
    }
    public int getPriorityForUnit(Unit unit)
    {
        Sector currentSector;
        currentSector = getSector(unit);
        return currentSector.getPriority();
    }

    private int basePriorityForSector(int line, int column, int coefficient)
    {
        if( line >=0 && line < countLine)
            if(column >= 0 && column < countColumn)
                return sector_map[line][column].getBasePriority() / coefficient;
        return 0;
    }

    public void solveSectorToMove(Bulk bulk)
    {
        for (int line = 0; line < countLine; ++line )
            for (int column = 0; column < countColumn ; ++column) {
                sector_map[line][column].findPriority(bulk);
                //  Log.v("Priority ", String.valueOf(line) + " " + String.valueOf(column) + " = " + String.valueOf(sector_map[line][column].getBasePriority()));
            }
        for (int line = 0; line < countLine; ++line )
            for (int column = 0; column < countColumn ; ++column) {
                sector_map[line][column].updatePriority(
                        basePriorityForSector(line + 1, column, 2)
                                + basePriorityForSector(line - 1, column, 2)
                                + basePriorityForSector(line, column + 1, 2)
                                + basePriorityForSector(line, column - 1, 2)
                                + basePriorityForSector(line + 1, column + 1, 3)
                                + basePriorityForSector(line - 1, column - 1, 3)
                                + basePriorityForSector(line - 1, column + 1, 3)
                                + basePriorityForSector(line + 1, column - 1, 3)
                );
            }
    }
}