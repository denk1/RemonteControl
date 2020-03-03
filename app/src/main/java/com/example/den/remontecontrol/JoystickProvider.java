package com.example.den.remontecontrol;

import android.graphics.PointF;

public class JoystickProvider {
    private static volatile JoystickProvider instance;
    private PointF mPointF1;
    private PointF mPointF2;
    private byte[] mByteA = new byte[8];
    private JoystickProvider() {
        mPointF1 = new PointF();
        mPointF2 = new PointF();
    }

    public static JoystickProvider getInstance() {
        if(instance == null)
            synchronized (JoystickProvider.class) {
            if( instance == null)
                instance = new JoystickProvider();

            }
        return instance;
    }

    public PointF getJoystick1() {
        return mPointF1;
    }

    public PointF getJoystick2() {
        return mPointF2;
    }

    public byte[] getByteA() {
        return mByteA;
    }

    public void setByteA(byte[] byteA) {
        mByteA = byteA;
    }
}
