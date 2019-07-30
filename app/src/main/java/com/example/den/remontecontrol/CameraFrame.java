package com.example.den.remontecontrol;

import android.widget.ImageView;

public class CameraFrame {
    private static CameraFrame cameraFrame = null;
    ImageView imageView = null;

    public CameraFrame(ImageView imageView) {
        this.imageView = imageView;
    }

    public void outputFrame() {

    }
}
