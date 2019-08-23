package com.example.den.remontecontrol;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

public class CameraFrame {

    private static CameraFrame cameraFrame = null;
    Matrix matrix = new Matrix();
    ImageView imageView = null;

    public CameraFrame(ImageView imageView) {

        this.imageView = imageView;
        imageView.setImageResource(R.drawable.car);

    }

    public void outputFrame(Bitmap bitmap) {

        matrix.setRotate(90.0f);
        imageView.setImageMatrix(matrix);

        this.imageView.setImageBitmap(bitmap);
    }
}
