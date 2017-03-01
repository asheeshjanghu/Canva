package com.example.photomosaic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class ImageDownloader implements Runnable {
    private static final String TAG = ImageDownloader.class.getSimpleName();
    int xIndex;
    int yIndex;
    String color;
    Handler handler;
    public static final int DOWNLOAD_COMPLETE = 1;
    public static final int DOWNLOAD_FAILED = -1;

    public ImageDownloader(int xIndex, int yIndex, String color, Handler handler) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.color = color;
        this.handler = handler;
    }

    @Override
    public void run() {
        Bitmap bitmap = getImage();
        Log.i(TAG," x =" + xIndex + " y = " + yIndex);
        DownloadedBitmap downloadedBitmap = new DownloadedBitmap(bitmap, xIndex, yIndex);
        handler.sendMessage(handler.obtainMessage(DOWNLOAD_COMPLETE,downloadedBitmap));
    }

    private Bitmap getImage() {
        String BASE_URL = "http://10.0.2.2:8765/color/32/32/";
        String url = BASE_URL + color;
        try {

            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
//            Log.i(TAG, " response = " + bitmap);
            return bitmap;
//            iv.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
