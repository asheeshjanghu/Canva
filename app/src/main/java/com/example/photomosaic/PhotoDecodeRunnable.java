package com.example.photomosaic;

import android.os.Process;

/**
 * Created by Asheesh on 16-Feb-17.
 */

public class PhotoDecodeRunnable implements Runnable {
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }
}
