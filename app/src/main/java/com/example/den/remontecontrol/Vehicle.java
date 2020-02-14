package com.example.den.remontecontrol;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.anastr.speedviewlib.DeluxeSpeedView;

public class Vehicle {
    private float velocity;
    private PointF currentLocation;
    private Float currentYaw;
    private Float currentSteerAngle;
    private static Vehicle instance = null;
    private InfoParameters infoParameters;

    private Vehicle() {
        infoParameters = InfoParameters.createInfoParameters();
    }
    public static Vehicle getInstance() {
        if(instance == null) {
            instance = new Vehicle();
        }
        return instance;
    }

    public void setVelocity(float velocityScalar) {
        velocity = velocityScalar;
        TextView textViewVelocity = (TextView)infoParameters.getViewOfParam("velocity");
        if(textViewVelocity!=null)
            textViewVelocity.setText(String.valueOf(velocityScalar));
    }

    public void setCurrentLocation(PointF pointFLocation) {
        currentLocation = pointFLocation;
        MapSurfaceView mapSurfaceView = (MapSurfaceView) infoParameters.getViewOfParam("map");
        if(mapSurfaceView!=null) {
            mapSurfaceView.setVehicle(this);
            mapSurfaceView.setCarLocation();
        }
    }

    public void setCurrentYaw(Float yaw) {
        currentYaw = yaw;
        MapSurfaceView mapSurfaceView = (MapSurfaceView) infoParameters.getViewOfParam("map");
        if (mapSurfaceView != null) {
            mapSurfaceView.setVehicle(this);
            mapSurfaceView.setCarYaw();
        }
    }

    public void setVelocitySpeedometer(float velocitySpeedometer) {
        DeluxeSpeedView deluxeSpeedView1 = (DeluxeSpeedView)infoParameters.getViewOfParam("speedometer1");
        DeluxeSpeedView deluxeSpeedView2 = (DeluxeSpeedView)infoParameters.getViewOfParam("speedometer2");

        if(deluxeSpeedView1 != null)
            deluxeSpeedView1.speedTo(velocitySpeedometer, 500);
        if(deluxeSpeedView2 != null)
            deluxeSpeedView2.speedTo(velocitySpeedometer, 500);
    }

    public void setCurrentSteeringAngle(float steeringAngle) {
        currentSteerAngle = steeringAngle;
        SeekBar seekBarCurrAngleSteering = (SeekBar) infoParameters.getViewOfParam("seekBarCurrAngle");
        TextView textBoxCurrSteer = (TextView) infoParameters.getViewOfParam("textBoxCurrSteer");
        if(seekBarCurrAngleSteering != null && textBoxCurrSteer != null) {
            seekBarCurrAngleSteering.setProgress(50 - (int)steeringAngle, false);
            textBoxCurrSteer.setText(String.valueOf((int)steeringAngle));
        }
    }

    public float getVelocity() {
        return velocity;
    }

    public PointF getCurrentLocation() {
        return currentLocation;
    }

    public float getCurrentYaw() {
        return currentYaw;
    }

    public static class FrameManager {
        private Handler handler = null;
        private static FrameManager frameManager = null;
        public final static int CURRENT_FRAME_CAMERA1 = 1;
        public static FrameManager getInstance() {
            if(frameManager==null) {
                frameManager = new FrameManager();
            }
            return frameManager;
        }

        private FrameManager() {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message inputMessage) {
                    Object objParameter = inputMessage.obj;
                    switch (inputMessage.what) {
                        case CURRENT_FRAME_CAMERA1:

                        default:
                            super.handleMessage(inputMessage);
                    }
                }
            };
        }
    }
}
