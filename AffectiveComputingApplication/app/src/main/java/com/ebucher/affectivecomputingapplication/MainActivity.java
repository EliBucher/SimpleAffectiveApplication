package com.ebucher.affectivecomputingapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import engine.Analyzer;
import engine.ImageDecoder;
import engine.Labeler;

public class MainActivity extends Activity {

    private String mImageFullPathAndName = "";
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private ImageView image1, image2, image3, image4, emotion;
    private Button button;
    private Bitmap image;
    private ImageDecoder imageDecoder;
    private Labeler labeler;
    private Analyzer analyzer;
    private int stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        emotion = (ImageView) findViewById(R.id.emotion);

        button = (Button) findViewById(R.id.button);

        stage = 0;

    }

    public void DoTakePhoto(View view) {
        // TODO rewrite this as a case
        if(stage == 0) {
            button.setText("Decode Image");
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, TAKE_PICTURE);
        }
        if(stage == 1) {
            button.setText("Label Image");
            decodeImage();
        }
        if(stage == 2) {
            button.setText("Analyze Image");
            labelImage(imageDecoder.getMapValues(), imageDecoder.getCountInMap());
        }
        if(stage == 3) {
            button.setText("Get Emotion");
            analyzeImage(labeler.getMapValues());
        }
        if(stage == 4)
            getEmotion();
        // TODO make stage loop for new picture
        stage++;


//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        /**
//         Here destination is the File object in which your captured images will be stored
//         **/
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.DIRECTORY_PICTURES, "temp.jpg")));
///**
// Here REQUEST_IMAGE is the unique integer value you can pass it any integer
// **/
//        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        image = null;

        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
//
//                System.out.println("GETTING IMAGE FROM STORAGE");
//
//                Uri imageUri = data.getData();
//                try {
//                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                System.out.println("START");

                image = (Bitmap) data.getExtras().get("data");
                image1.setImageBitmap(image);
//
//                Uri selectedImage = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImage,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                mImageFullPathAndName = cursor.getString(columnIndex);
//                cursor.close();
            }
        }
    }

    protected void decodeImage(){
        imageDecoder = new ImageDecoder(image);
        image2.setImageBitmap(imageDecoder.convertToImage());
    }

    protected void labelImage(int[][] mapValues, ArrayList<Integer> countInMap){
        labeler = new Labeler(mapValues, countInMap);
        image3.setImageBitmap(labeler.convertToImage(image));
    }

    protected void analyzeImage(int[][] mapValues){
        analyzer = new Analyzer(mapValues);
        image4.setImageBitmap(analyzer.convertToImage(image));
    }

    protected void getEmotion(){
        emotion.setImageBitmap(analyzer.getEmotion(this.getApplicationContext()));
    }
}
