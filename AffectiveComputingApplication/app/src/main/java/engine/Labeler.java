package engine;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Eli on 11/6/2015.
 */
public class Labeler {
    // TODO create a labeler method that labels the eyes and mouth with specific colors, removes everything else
    // TODO create more/ better ways of removing non-action units
    private int[][] mapValues;
    private ArrayList<Integer> countInMap, badValues;
    private int width, height;

    public Labeler(int[][] mapValues, ArrayList<Integer> countInMap){
        this.mapValues = mapValues;
        this.countInMap = countInMap;
        this.width = mapValues.length;
        this.height = mapValues[0].length;
        this.badValues = new ArrayList<>();

        removeEdges();
        removeStrange();
        removeBasedOnSize();
    }

    // Marks edges as bad values
    private void removeEdges(){
        for(int x = 0; x < width; x++)
            if (!badValues.contains(mapValues[x][0]))
                badValues.add(mapValues[x][0]);
            else if (!badValues.contains(mapValues[x][height-1]))
                badValues.add(mapValues[x][height-1]);

        for(int y = 0; y < height; y++)
            if (!badValues.contains(mapValues[0][y]))
                badValues.add(mapValues[0][y]);
            else if (!badValues.contains(mapValues[width-1][y]))
                badValues.add(mapValues[width-1][y]);
    }

    // Removes the shapes that are unlikely to be action units
    private void removeStrange(){
        ArrayList<Map> maps = new ArrayList<>();

        // Gather info about maps
        for(int x = 0; x < width; x++) {
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
        }

        // Marks strange maps as bad values
        for(Map map : maps)
            if(!map.isAcceptable(.2, 5.0))
                badValues.add(map.getMapValue());

        for(Map map : maps)
            if(map.ratio())
                badValues.add(map.getMapValue());

        //TODO check for size violation here
    }

    // Removes if the map is too big or too small to be an action unit
    // Also removes bad values
    private void removeBasedOnSize(){
        int maxSize = width*height/10;
        int minSize = 5;
        for(int x = 0; x < width; x++)
            for(int y = 0; y < height; y++)
                if(badValues.contains(mapValues[x][y]) || countInMap.get(mapValues[x][y]-1) <= minSize || countInMap.get(mapValues[x][y]-1) >= maxSize)
                    mapValues[x][y] = 0;
    }

    public int[][] getMapValues(){
        return mapValues;
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
}
