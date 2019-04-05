package com.example.den.remontecontrol;

import android.graphics.PointF;
import android.widget.TextView;

public class Vehicle {
    private float velocity;
    private PointF currentLocation;
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



}
