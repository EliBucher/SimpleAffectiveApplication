package engine;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Eli on 10/9/2015.
 */
public class ImageDecoder {
    //0 alpha
    //1 red
    //2 green
    //3 blue
    //4 pixel
    private int[][][] pixels;
    private int[][] mappedPixels;
    private Bitmap bitmapImage;
    private int width, height;
    private ArrayList<Integer> countInMap;

    public ImageDecoder(Bitmap image) {
        countInMap = new ArrayList<>();
        convertImage(image);
        remapSmallMaps(mapPixels(0));
    }

    // Convert from single int ARGB to A, R, G, B
    private void convertImage(Bitmap image) {
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width][height][5];
        mappedPixels = new int[width][height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                int pixel = image.getPixel(x, y);
                pixels[x][y][0] = ((pixel >> 24) & 0xff);
                pixels[x][y][1] = ((pixel >> 16) & 0xff);
                pixels[x][y][2] = ((pixel >> 8) & 0xff);
                pixels[x][y][3] = (pixel & 0xff);
                pixels[x][y][4] = ((pixels[x][y][0] & 0x0ff) << 24) | ((pixels[x][y][1] & 0x0ff) << 16) | ((pixels[x][y][2] & 0x0ff) << 8) | (pixels[x][y][3] & 0x0ff);
            }

    }

    // Recursively maps pixels
    private int mapPixels(int mapValue) {
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (mappedPixels[x][y] == 0) {
                    mapValue++;
                    countInMap.add(1);
                    mapPixels(x, y, null, new int[5], mapValue);
                }

        return mapValue;
    }

    // Maps pixels recursively
    private void mapPixels(int x, int y, int rgb[], int avgRGB[], int mapValue) {
        // Check that x and y are within bounds
        if (x >= 0 && x < width && y >= 0 && y < height) {
            // If r = -1 then this is the starter pixel
            if (rgb == null) {
                mappedPixels[x][y] = mapValue;
                countInMap.set(mapValue-1, countInMap.get(mapValue-1)+1);
                avgRGB = pixels[x][y];

                // Check surrounding pixels
                for (int i = 0; i < 9; i++)
                    if (i != 4)
                        mapPixels(x + i / 3 - 1, y + i % 3 - 1, pixels[x][y], avgRGB, mappedPixels[x][y]);
            }
            // If r != -1 then check if this pixel can be added to the current map
            else if (mappedPixels[x][y] == 0) {
                // Compare pixel
                if (comparePixel(rgb, pixels[x][y], avgRGB)) {
                    mappedPixels[x][y] = mapValue;
                    int count = countInMap.get(mapValue-1)+1;
                    countInMap.set(mapValue-1, count);
                    for(int i = 1; i < 4; i++)
                        avgRGB[i] = (avgRGB[i]*(count-1))/count + pixels[x][y][i]/count;

                    // Check surrounding pixels
                    for (int i = 0; i < 9; i++)
                        if (i != 4)
                            mapPixels(x + i / 3 - 1, y + i % 3 - 1, pixels[x][y], avgRGB, mappedPixels[x][y]);
                }
            }
        }
    }

    // Returns true if RGB is similar enough to rbg and avgRGB
    private boolean comparePixel(int[] rgb, int[] RGB, int[] avgRGB) {
        // TODO check pixel vs the average pixel in the current map

        // Difference between this pixel and last
        int maxDifference = 12;
        double difference = 0;
        for(int i = 1; i < 4; i++)
            difference += (rgb[i] - RGB[i]) * (rgb[i] - RGB[i]);
        difference = Math.sqrt(difference);
        if(difference > maxDifference)
            return false;
        return true;

        // Difference between this pixel and average in map
//        maxDifference = 100;
//        difference = 0;
//        for(int i = 1; i < 4; i++)
//            difference += (avgRGB[i] - RGB[i]) * (avgRGB[i] - RGB[i]);
//        difference = Math.sqrt(difference);
//        return (difference < maxDifference);

    }

    // Remaps anything smaller than smallestAllowedMap
    private void remapSmallMaps(int mapValue){
        int smallestAllowedMap = 50;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if(countInMap.get(mappedPixels[x][y]-1) < smallestAllowedMap){
                    mappedPixels[x][y] = 0;
                    pixels[x][y][1] = 0;
                    pixels[x][y][2] = 0;
                    pixels[x][y][3] = 0;
                }

        mapPixels(mapValue);
    }

    // Creates a bitmap that represents mappedPixels array
    public Bitmap convertToImage() {
        bitmapImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                bitmapImage.setPixel(x, y, mappedPixels[x][y] * -10000);

        return bitmapImage;
    }

    public int[][] getMapValues(){
        return mappedPixels;
    }

    public ArrayList<Integer> getCountInMap(){
        return countInMap;
    }}
