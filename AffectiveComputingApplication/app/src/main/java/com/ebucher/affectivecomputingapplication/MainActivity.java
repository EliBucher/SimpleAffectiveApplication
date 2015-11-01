package com.ebucher.affectivecomputingapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import engine.ImageDecoder;


public class MainActivity extends Activity {

    private String mImageFullPathAndName = "";
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private ImageView image1, image2, image3;
    private ImageDecoder imageDecoder;

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
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap image = null;

        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                image = (Bitmap) data.getExtras().get("data");
                image1.setImageBitmap(image);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImageFullPathAndName = cursor.getString(columnIndex);
                cursor.close();
            }
        }

        decodeImage(image);
    }

    protected void decodeImage(Bitmap image){
        System.err.println("here");
        imageDecoder = new ImageDecoder(image);
        image2.setImageBitmap(imageDecoder.getBitmapImage());
        image3.setImageBitmap(imageDecoder.getBitmapImage2());
    }
}
