package com.example.den.remontecontrol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
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
                final int w = 700;
                final int h = 700;
                ByteBuffer byteBuffer = ByteBuffer.allocate(4 * w * h);
                byte [] byteArray = byteBuffer.array();
                Arrays.fill(byteArray, 0, byteArray.length, (byte) 0x00);
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length );
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
