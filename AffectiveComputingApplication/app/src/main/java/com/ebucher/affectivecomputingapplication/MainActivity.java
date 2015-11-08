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
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import engine.ImageDecoder;
import engine.Labeler;

public class MainActivity extends Activity {

    private String mImageFullPathAndName = "";
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private ImageView image1, image2, image3;
    private ImageDecoder imageDecoder;
    private Labeler labeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);

    }

    public void DoTakePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, TAKE_PICTURE);

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

        Bitmap image = null;

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

        decodeImage(image);
    }

    protected void decodeImage(Bitmap image){
        imageDecoder = new ImageDecoder(image);
        image2.setImageBitmap(imageDecoder.convertToImage());
        labelImage(imageDecoder.getMapValues(), imageDecoder.getCountInMap());
    }

    protected void labelImage(int[][] mapValues, ArrayList<Integer> countInMap){
        labeler = new Labeler(mapValues, countInMap);
        image3.setImageBitmap(labeler.convertToImage());
    }
}
