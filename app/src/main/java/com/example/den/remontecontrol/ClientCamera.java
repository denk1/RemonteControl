package com.example.den.remontecontrol;

import android.graphics.Bitmap;
import android.media.Image;

import java.util.Arrays;

public class ClientCamera {

    private static ClientCamera clientCamera;
    private Thread sendFrameThread = null;

    public static ClientCamera getInstance() {
        if(clientCamera==null) {
            clientCamera = new ClientCamera();
        }
        return clientCamera;
    }

    Runnable sendFrameRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                int[] arr_image = new int[490000];
                Arrays.fill(arr_image, 0);
                Bitmap bitmap = Bitmap.createBitmap(arr_image, 700, 700, Bitmap.Config.ALPHA_8);
                double pause = 1000.0 / 30.0;
                try {
                    FrameManager.getInstance().handleParam(bitmap, FrameManager.CURRENT_FRAME_VALUE);
                    Thread.sleep((long) pause);
                } catch (Exception e) {

                }
            }
        }
    };

    private ClientCamera() {
        sendFrameThread = new Thread(sendFrameRunnable);
        sendFrameThread.start();
    }

}
