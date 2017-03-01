package com.example.photomosaic;

import android.graphics.Bitmap;

import java.util.Collections;
import java.util.List;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class ImageData {

    public static int MAX_WIDTH = 1024;
    public static int MAX_HEIGHT = 1024;
    public static int TILE_WIDTH = 32;
    public static int TILE_HEIGHT = 32;
    int width , height;
    private String[][] tilesColorBuckets;
    int numRows;
    int numColumns;
    Bitmap [][] downloadedImageBitmap;

    public Bitmap bitmap;

    static ImageData mInstance;

    private ImageData() {
    }

    public static ImageData getInstance(){
        if(mInstance == null){
            mInstance = new ImageData();
        }
        return mInstance;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String[][] getTilesColorBuckets() {
        return tilesColorBuckets;
    }

    public void setTilesColorBuckets(String[][] tilesColorBuckets) {
        this.tilesColorBuckets = tilesColorBuckets;
    }

    public int getNumRows() {
        numRows = height / TILE_HEIGHT;
        return numRows;
    }

    public int getNumColumns() {
        numColumns = width / TILE_WIDTH;
        return numColumns;
    }

    public void setUpBitmapMatrix(){
        if(numRows > 0 && numColumns > 0){
            downloadedImageBitmap = new Bitmap[numRows][numColumns];
        }
    }

    public void updateMatrix(Bitmap bitmap, int x, int y){
        if(downloadedImageBitmap.length < numRows * numColumns){
            synchronized (downloadedImageBitmap){
                downloadedImageBitmap[x][y] = bitmap;
                //check if row filled
                boolean isFilled = checkRowFilled(x);
                if(isFilled){
                    //send message to draw the row

                    //check next row
                    if(x == numRows){
                        return;
                    } else {

                    }
                    //else break free
                }
            }
        }
    }

    private boolean checkRowFilled(int x){
        for(Object object : downloadedImageBitmap[x]){
            if(object == null){
                return false;
            }
        }
        return true;
    }

    public Bitmap[][] getDownloadedImageBitmap() {
        return downloadedImageBitmap;
    }

    public void setDownloadedImageBitmap(Bitmap[][] downloadedImageBitmap) {
        this.downloadedImageBitmap = downloadedImageBitmap;
    }
}
