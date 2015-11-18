package engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.ebucher.affectivecomputingapplication.R;

import java.util.ArrayList;

/**
 * Created by Eli on 11/17/2015.
 */
public class Analyzer {

    private int[][] mapValues;
    private int width, height;
    private ArrayList<Map> maps;
    private Map smile;
    private String emotion;

    public Analyzer(int[][] mapValues){
        this.mapValues = mapValues;
        this.width = mapValues.length;
        this.height = mapValues[0].length;
        this.maps = new ArrayList<>();

        mapDetails();
        analyzeSmile();
    }

    private void mapDetails(){
        // Gather info about maps
        for(int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (mapValues[x][y] != 0) {
                    boolean mapExists = false;
                    for (Map map : maps)
                        if (map.getMapValue() == mapValues[x][y]) {
                            map.checkNewPoint(x, y);
                            mapExists = true;
                            break;
                        }
                    if(!mapExists)
                        maps.add(new Map(x, y, mapValues[x][y]));
                }

        // TODO map should be labeled by color in labeler not here
        smile = maps.get(0);
        for(Map map : maps)
            if(smile.getSize() < map.getSize())
                smile = map;
        smile.generateMap();
    }

    private void analyzeSmile(){
        // TODO take point at a handful of random locations
        ArrayList<Point> points = new ArrayList<>();
        points.add(smile.getMax(0));
        points.add(smile.getMax(.5));
        points.add(smile.getMiddle(.5));
        points.add(smile.getMin(.5));
        points.add(smile.getMax(1));

        for(Point p : points)
            for(int i = 0; i < 9; i++)
                mapValues[p.x+i/3-1][p.y+i%3-1] = 3;

        // TODO automate the scoring given a random location
        int smileInt = 0;
        for(int i = 1; i < 4; i++) {
            if (points.get(0).y - points.get(i).y < 0)
                smileInt++;
            else
                smileInt--;
            if (points.get(4).y - points.get(i).y < 0)
                smileInt++;
            else
                smileInt--;
        }

        // TODO widen neutral range with more points checked
        if(smileInt > 0)
            emotion = "happy";
        if(smileInt < 0)
            emotion = "sad";
        if(smileInt == 0)
            emotion = "neutral";
    }

    // Creates a bitmap that represents mappedPixels array
    public Bitmap convertToImage(Bitmap image) {
        Bitmap bitmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if(mapValues[x][y] == 0)
                    bitmapImage.setPixel(x, y, image.getPixel(x, y));
                else
                    bitmapImage.setPixel(x, y, mapValues[x][y] * -10000);

        return bitmapImage;
    }

    public Bitmap getEmotion(Context context){
        // TODO add neutral face
        if(emotion.equals("happy"))
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.happy);
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.sad);
    }
}
