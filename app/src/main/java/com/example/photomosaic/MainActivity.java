package com.example.photomosaic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.graphics.Bitmap.createBitmap;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MAX_POOL_SIZE = 10;
    Canvas canvas;
    Bitmap result;
    ImageView iv;
    ImageAverageHelper imageAverageHelper;
    private int count = 0;
    int width , height;
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button pickButton = (Button) findViewById(R.id.pickButton);
        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImageFromGallery();
            }
        });
        iv = (ImageView) findViewById(R.id.iv1);

    }

    private void initImage(final Bitmap bitmap) {
        iv.post(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = iv.getDrawable();
                width = drawable.getIntrinsicWidth();
                height = drawable.getIntrinsicHeight();
                result = createBitmap(width, height, Bitmap.Config.ARGB_8888);
                canvas = new Canvas(result);
                Log.i(TAG, " imageview width = " + width + " height = " + height);
                Log.i("Bitmap in Main" , " = " + bitmap.toString());
                ImageData imageData = ImageData.getInstance();
                imageData.setBitmap(bitmap);
                imageData.setHeight(height);
                imageData.setWidth(width);
                imageAverageHelper = new ImageAverageHelper(myHandler);
                imageAverageHelper.extractBitmapInfo();
            }
        });
    }



    private void drawImage(Bitmap bitmap, int x, int y) {
        Log.i("Drawing ", " Image x =" + x + " y = " + y + " bitmap = " + bitmap.toString());
        canvas.drawBitmap(bitmap, y * ImageData.TILE_WIDTH, x * ImageData.TILE_HEIGHT, null);
        count++;
        ImageData imageData = ImageData.getInstance();
        if (count == imageData.getNumRows() * imageData.getNumColumns()) {
            count = 0;
            Log.i(TAG, " completed");
            iv.post(new Runnable() {
                @Override
                public void run() {
                    iv.setImageBitmap(result);
                }
            });
        }
    }

    private void loadImageFromGallery() {
        count=0;
        imagePicker = new ImagePicker(MainActivity.this);
        imagePicker.loadImageFromGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        imagePicker.onImagePicked(requestCode,resultCode,data);
    }

    public void onImageProcessedUpdateView(Bitmap bitmap){
        iv.setImageBitmap(bitmap);
        initImage(bitmap);
    }


    public Handler myHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG," Received message " + msg);
            super.handleMessage(msg);
            switch (msg.what){
                case ImageDownloader.DOWNLOAD_COMPLETE:
                    //get the bitmap and x,y
                    DownloadedBitmap downloadedBitmap = (DownloadedBitmap) msg.obj;
                    drawImage(downloadedBitmap.getBitmap(), downloadedBitmap.getX(), downloadedBitmap.getY());
                    break;
                case ImageDownloader.DOWNLOAD_FAILED:
                    return;
                default:
                    return;
            }
        }
    };
}
