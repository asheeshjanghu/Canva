package com.example.photomosaic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class ImagePicker {

    private static final String TAG = ImagePicker.class.getSimpleName();
    public static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 1;

    MainActivity activity;

    public ImagePicker(MainActivity activity) {
        this.activity = activity;
    }

    void loadImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, PICK_GALLERY_IMAGE_REQUEST_CODE);
        } else {
            Log.i(TAG, "No activity to pick image");
        }
    }

    protected void onImagePicked(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GALLERY_IMAGE_REQUEST_CODE &&
                resultCode == activity.RESULT_OK && data != null) {
            Log.i(TAG, "picked image " + data.getData());
            InputStream inputStream = null;
            try {
                inputStream = activity.getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            if (inputStream == null) {
                Log.i(TAG, " is is null");
                return;
            }
            BitmapFactory.Options bo = new BitmapFactory.Options();
            bo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, bo);
            int outWidth = bo.outWidth;
            int outHeight = bo.outHeight;
            int scaleFactor = 1;
            while (outWidth / 2 > ImageData.MAX_WIDTH && outHeight / 2 > ImageData.MAX_HEIGHT) {
                outWidth /= 2;
                outHeight /= 2;
                scaleFactor *= 2;
            }

            Log.i("Scale factor ", " = " + scaleFactor);

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scaleFactor;
            FileInputStream fis = null;
            try {
                fis = (FileInputStream) activity.getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, o2);
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            activity.onImageProcessedUpdateView(bitmap);
//            iv.setImageBitmap(bitmap);
//            initImage(bitmap);
        } else {
            Log.i(TAG, "failed to pick image");
        }
    }

}
