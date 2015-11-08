package engine;

/**
 * Created by Eli on 11/6/2015.
 */
public class Map {
    private int left, right, top, bottom;
    private int mapValue;

    public Map(int x, int y, int mapValue){
        this.left = x;
        this.right = x;
        this.top = y;
        this.bottom = y;
        this.mapValue = mapValue;
    }

    public void checkNewPoint(int x, int y){
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
}
