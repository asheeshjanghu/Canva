package com.example.photomosaic;

import android.graphics.Bitmap;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class DownloadedBitmap {
    Bitmap bitmap;
    int x;
    int y;

    public DownloadedBitmap(Bitmap bitmap, int x, int y) {
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
