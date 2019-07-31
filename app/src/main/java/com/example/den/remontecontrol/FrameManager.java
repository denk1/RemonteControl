package com.example.den.remontecontrol;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import java.util.HashMap;

public class FrameManager {
    private Handler handler = null;
    private static FrameManager frameManager;
    public static final int CURRENT_FRAME_VALUE = 1;
    private HashMap<String, CameraFrame> screensMap = new HashMap<String, CameraFrame>();

    public static FrameManager getInstance() {
        if(frameManager==null) {
            frameManager = new FrameManager();
        }
        return frameManager;
    }

    public void handleParam(Object param, int typeParam) {
        Message messageParam = handler.obtainMessage(typeParam, param);
        messageParam.sendToTarget();
    }

    private FrameManager() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Object objParameter = inputMessage.obj;
                switch (inputMessage.what) {
                    case CURRENT_FRAME_VALUE:
                        drawFrame("camera1", objParameter);
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };
    }

    public void setCameraFrame(String cameraName, CameraFrame cameraFrame) {
        screensMap.put(cameraName, cameraFrame);
    }

    private void drawFrame(String cameraName, Object object) {
        CameraFrame cameraFrame = screensMap.get(cameraName);
        Bitmap bitmapFrame = (Bitmap) object;
        cameraFrame.outputFrame(bitmapFrame);
    }
}