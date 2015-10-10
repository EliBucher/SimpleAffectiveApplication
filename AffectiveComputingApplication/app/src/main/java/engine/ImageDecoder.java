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
    private int[][] mappedPixels, decodedImage;
    private Bitmap bitmapImage;
    private int width, height;
    private ArrayList<Integer> count, pixel;

    public ImageDecoder(Bitmap image){
        convertImage(image);
        getCounts();
        sortCounts();
        for(int i = 0; i < 10; i++)
            System.out.println(count.get(i)+" : "+convertToRGB(pixel.get(i)));
        createDecodedImage();
    }

    private void convertImage(Bitmap image) {
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[width][height][5];
        decodedImage = new int[width][height];
        mappedPixels = new int[width][height];

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++){
                int pixel = image.getPixel(x, y);
                int round = 20;
                pixels[x][y][0] = ((pixel >> 24) & 0xff);
                pixels[x][y][1] = ((pixel >> 16) & 0xff)/round*round;
                pixels[x][y][2] = ((pixel >> 8) & 0xff)/round*round;
                pixels[x][y][3] = (pixel & 0xff)/round*round;
                pixels[x][y][4] = ((pixels[x][y][0]&0x0ff)<<24)|((pixels[x][y][1]&0x0ff)<<16)|((pixels[x][y][2]&0x0ff)<<8)|(pixels[x][y][3]&0x0ff);
            }

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                mapPixels(x, y, -1, -1, -1, (int)(Math.random()*20000000));
    }

    private void getCounts(){
        count = new ArrayList<>();
        pixel = new ArrayList<>();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++){
                int p = pixels[x][y][4];
                int index = pixel.indexOf(p);
                if(index != -1)
                    count.set(index, count.get(index)+1);
                else{
                    count.add(1);
                    pixel.add(p);
                }
            }
    }

    private void sortCounts(){
        ArrayList<Integer> sortedCount = new ArrayList<Integer>();
        ArrayList<Integer> sortedPixel = new ArrayList<Integer>();
        int max, value, index;
        while(count.size() != 0){
            max = 0;
            value = 0;
            index = -1;
            for(int i = 0; i < count.size(); i++)
                if(max < count.get(i)){
                    max = count.get(i);
                    value = pixel.get(i);
                    index = i;
                }
            sortedCount.add(max);
            sortedPixel.add(value);
            count.remove(index);
            pixel.remove(index);
        }
        count = sortedCount;
        pixel = sortedPixel;
    }

    private String convertToRGB(int singleIntARGB){
        return "SINGLE:"+singleIntARGB+
                " ALPHA:"+((singleIntARGB >> 24) & 0xff)+
                " RED:"+((singleIntARGB >> 16) & 0xff)+
                " GREEN:"+((singleIntARGB >> 8) & 0xff)+
                " BLUE:"+(singleIntARGB & 0xff);
    }

    private void createDecodedImage(){
        while(pixel.size() > 30){
            pixel.remove(pixel.size()-1);
            count.remove(count.size()-1);
        }

        for(int x = 0; x < pixels.length; x++)
            for(int y = 0; y < pixels[0].length; y++){
                if(!pixel.contains(pixels[x][y][4]))
                    decodedImage[x][y] = -16724940;
                else
                    decodedImage[x][y] = pixels[x][y][4];
            }
    }

    public Bitmap getBitmapImage(){
        bitmapImage = Bitmap.createBitmap(decodedImage.length, decodedImage[0].length, Bitmap.Config.ARGB_8888);

        for(int x = 0; x < decodedImage.length; x++)
            for(int y = 0; y < decodedImage[0].length; y++)
                bitmapImage.setPixel(x, y, decodedImage[x][y]);

        return bitmapImage;
    }

    public Bitmap getBitmapImage2(){
        bitmapImage = Bitmap.createBitmap(decodedImage.length, decodedImage[0].length, Bitmap.Config.ARGB_8888);

        for(int x = 0; x < decodedImage.length; x++)
            for(int y = 0; y < decodedImage[0].length; y++)
                bitmapImage.setPixel(x, y, mappedPixels[x][y]*-1);

        return bitmapImage;
    }

    private void mapPixels(int x, int y, int r, int g, int b, int mapValue){
        if(x >= 0 && x < mappedPixels.length && y >= 0 && y < mappedPixels[0].length) {
            if (r == -1) {
                if (mappedPixels[x][y] == 0) {
                    mappedPixels[x][y] = mapValue;
                    mapPixels(x - 1, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x - 1, y, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x - 1, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                }
            } else if (mappedPixels[x][y] == 0) {
                if (Math.sqrt((r - pixels[x][y][1]) * (r - pixels[x][y][1]) + (g - pixels[x][y][1]) * (g - pixels[x][y][1]) + (b - pixels[x][y][1]) * (b - pixels[x][y][1])) < 10) {
                    mappedPixels[x][y] = mapValue;
                    mapPixels(x - 1, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y - 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x - 1, y, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x - 1, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                    mapPixels(x + 1, y + 1, pixels[x][y][1], pixels[x][y][2], pixels[x][y][3], mappedPixels[x][y]);
                }
            }
        }
    }
}
