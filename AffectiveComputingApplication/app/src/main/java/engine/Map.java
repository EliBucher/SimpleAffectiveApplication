package engine;

import android.graphics.Point;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Eli on 11/6/2015.
 */
public class Map {
    private int left, right, top, bottom;
    private int mapValue;
    private int size;
    private ArrayList<Point> points;
    private int[][] map;

    public Map(int x, int y, int mapValue){
        this.left = x;
        this.right = x;
        this.top = y;
        this.bottom = y;
        this.mapValue = mapValue;
        this.size = 0;
        this.points = new ArrayList<>();
    }

    public void checkNewPoint(int x, int y){
        size++;
        points.add(new Point(x, y));
        if(left > x)
            left = x;
        else if(right < x)
            right = x;
        if(top > y)
            top = y;
        else if(bottom < y)
            bottom = y;
    }

    public boolean isAcceptable(double min, double max){
        double ratio = (right-left+1)/(bottom-top+1);
        if(ratio < min)
            return false;
        if(ratio > max)
            return false;
        return true;
    }

    public int getMapValue(){
        return mapValue;
    }

    public int getSize(){
        return size;
    }

    public boolean ratio(){
        return (((right-left)*(bottom-top))/(size+1) > 2);
    }

    public void generateMap(){
        map = new int[right-left+1][bottom-top+1];
        for(Point p : points)
            map[p.x-left][p.y-top] = 1;
    }

    public Point getMax(double percent){
        int x = (int)((map.length-1)*percent);
        for(int y = 0; y < map[x].length; y++)
            if(map[x][y] == 1)
                return new Point(x+left, y+top);
        return null;
    }

    // TODO change this to get the average height of the pixels at the percent
    public Point getMiddle(double percent){
        return new Point((int)((map.length-1)*percent)+left, getMin(percent).y - getMax(percent).y +top);
    }

    public Point getMin(double percent){
        int x = (int)((map.length-1)*percent);
        for(int y = map[x].length-1; y >= 0; y--)
            if(map[x][y] == 1)
                return new Point(x+left, y+top);
        return null;
    }
}
