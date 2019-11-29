package com.example.den.remontecontrol;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class InfoManager {
    private Handler handler = null;
    private static InfoManager infoManager = null;
    private Vehicle vehicle;
    private final String TAG = "InfoManager";

    public static final int CURRENT_VELOCITY_VALUE = 1;
    public static final int CURRENT_LOCATION_VALUE = 2;
    public static final int CURRENT_YAW_VALUE = 3;
    public static final int CURRENT_STEERING_ANGLE = 4;

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
                        try {
                            vehicle.setVelocity((float) objParameter);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                        vehicle.setVelocitySpeedometer((float)objParameter);
                        break;

                    case CURRENT_LOCATION_VALUE:
                        vehicle.setCurrentLocation((PointF)objParameter);
                        break;

                    case CURRENT_YAW_VALUE:
                        vehicle.setCurrentYaw((Float)objParameter);

                    case CURRENT_STEERING_ANGLE:
                        vehicle.setCurrentSteeringAngle((Float)objParameter);

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
