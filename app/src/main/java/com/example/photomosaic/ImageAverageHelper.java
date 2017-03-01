package com.example.photomosaic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.graphics.Bitmap.createBitmap;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class ImageAverageHelper {

    private static final int MAX_POOL_SIZE = 10;
    Handler handler;
    ImageData imageData;
    String[][] tilesColorBuckets;
    int numRows, numColumns;

    public ImageAverageHelper(Handler handler) {
        this.handler = handler;
    }

    public void extractBitmapInfo() {
        imageData = ImageData.getInstance();
        int tileWidth = ImageData.TILE_WIDTH, tileHeight = ImageData.TILE_HEIGHT;
        int width = imageData.getWidth(), height = imageData.getHeight();
        numColumns = imageData.getNumColumns();
        numRows = imageData.getNumRows();
        tilesColorBuckets = new String[numRows][numColumns];
        Bitmap bitmap = imageData.getBitmap();
        Log.i("Bitmap in Avg" , " = " + bitmap.toString());
        int tilePixelCount = ImageData.TILE_WIDTH * ImageData.TILE_HEIGHT;    //pixels in one tile
        int redBucket, greenBucket, blueBucket;

        for (int x = 0; x < imageData.getNumRows(); x++) {
            for (int y = 0; y < imageData.getNumColumns(); y++) {

                Bitmap subsetBitmap = createBitmap(bitmap,
                        y * tileWidth, x * tileHeight,
                        tileWidth, tileHeight);

                int[] pixels = new int[tileWidth * tileHeight];
                subsetBitmap.getPixels(pixels, 0, tileWidth, 0, 0, tileWidth, tileHeight);
                redBucket = 0;
                greenBucket = 0;
                blueBucket = 0;
                // process each tile now
                for (int j = 0; j < tileWidth; j++) {
                    for (int k = 0; k < tileHeight; k++) {
                        int color = pixels[j * tileWidth + k]; //eachRowPixels*rowNum + columnNum
                        redBucket += Color.red(color);
                        greenBucket += Color.green(color);
                        blueBucket += Color.blue(color);
                    }
                }
//            Log.i(TAG, " index = " +i + " red = " + redBucket/tilePixelCount);
                String red = String.format("%02X", redBucket / tilePixelCount);
                String green = String.format("%02X", greenBucket / tilePixelCount);
                String blue = String.format("%02X", blueBucket / tilePixelCount);
                tilesColorBuckets[x][y] = red + green + blue;
            }
        }

        for (int i = 0; i < numRows; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < numColumns; j++) {
                stringBuilder.append(tilesColorBuckets[i][j] + "  ");
            }
            Log.i("Bucket Colors", +i + " " + stringBuilder);
        }
        fetchImage();
    }

    private void fetchImage() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE,
                MAX_POOL_SIZE, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numColumns; y++) {
                ImageDownloader imageDownloader =
                        new ImageDownloader(x,y,tilesColorBuckets[x][y], handler);
                threadPoolExecutor.execute(imageDownloader);
            }
        }
    }
}
