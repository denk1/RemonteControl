package com.example.den.remontecontrol;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

public class InfoManager {
    private Handler handler = null;
    private static InfoManager infoManager = null;
    private Vehicle vehicle;

    public static final int CURRENT_VELOCITY_VALUE = 1;
    public static final int CURRENT_LOCATION_VALUE = 2;

    public static InfoManager createInfoManager() {
        if(infoManager==null) {
            infoManager = new InfoManager();
        }
        return infoManager;
    }

    public void handleParam(Object param, int typeParam) {
        Message messageParam = handler.obtainMessage(typeParam, param);
        messageParam.sendToTarget();
    }

    private InfoManager() {
        vehicle = Vehicle.getInstance();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Object objParameter = inputMessage.obj;

                switch (inputMessage.what) {
                    case CURRENT_VELOCITY_VALUE:
                        vehicle.setVelocity((float)objParameter);
                        break;

                    case CURRENT_LOCATION_VALUE:
                        break;

                    default:
                        /*
                         * Pass along other messages from the UI
                         */
                        super.handleMessage(inputMessage);

                }
            }
        };
    }

}
