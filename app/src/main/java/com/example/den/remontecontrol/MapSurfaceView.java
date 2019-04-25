package com.example.den.remontecontrol;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MapSurfaceView extends SurfaceView implements Runnable {
    Path path;

    Thread thread = null;
    SurfaceHolder surfaceHolder;
    boolean running = true;
    SurfaceHolder holder;
    Bitmap bitmap;
    Bitmap bitmapCar;
    float x = 512.0f, y = 512.0f, dx = 0, dy = 0;
    float x0 = 0, y0 = 0;
    final float scaleX = 0.75f, scaleY = 0.75f;
    private Matrix matrix = new Matrix();
    private Matrix matrixLane = new Matrix();
    private Matrix matrixCar = new Matrix();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PointF pointFCarLocation = new PointF();
    private float currentYaw = 0.0f;

    Vehicle vehicle = null;

    private void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback2() {
            @Override
            public void surfaceRedrawNeeded(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                thread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                running = false;
                while (retry) {
                    try {
                        thread.join();
                        retry = false;
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lane_full);
        bitmapCar = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.RED);
        setZOrderOnTop(true);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSLUCENT);
        thread = new Thread(this);
        matrix.setScale(scaleX, scaleY);
        InfoParameters infoParameters = InfoParameters.createInfoParameters();
        infoParameters.setViewOfParam("map", this);
    }

    public MapSurfaceView(Context context) {
        super(context);
        init();
    }

    public MapSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            x0 = event.getX(); y0 = event.getY();

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            dx = (event.getX() - x0) / scaleX; dy = (event.getY() - y0) / scaleY;
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            x += dx; y += dy;
            dx = 0; dy = 0;
        }

        return true;
    }

    @Override
    public void run() {
        while (running) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if(canvas != null) {
                    canvas.drawColor(Color.WHITE);
                    //Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    canvas.setMatrix(matrix);
                    matrixLane.setTranslate(-bitmap.getWidth()/2.0f, -bitmap.getHeight()/2.0f);
                    matrixLane.postRotate(90.0f);
                    matrixLane.postTranslate(x + dx + 6050.0f, y + dy + 6050.0f);
                    canvas.drawBitmap(bitmap, matrixLane, null);
                    matrixCar.setTranslate(-bitmapCar.getWidth()/2, -bitmapCar.getHeight()/2);
                    matrixCar.postRotate(-currentYaw + 180);
                    matrixCar.postTranslate((pointFCarLocation.x * 11.8f + x + dx) * 3.6f / scaleX, (pointFCarLocation.y * 11.8f + y + dy) * 3.6f / scaleY);
                    matrixCar.postScale(scaleX/3.6f, scaleY/3.6f);

                    canvas.drawBitmap(bitmapCar, matrixCar, null);
                }
            }
            finally {
                if(canvas!=null)
                    surfaceHolder.unlockCanvasAndPost(canvas);
            }

        }
    }

    public void setCarLocation() {
        pointFCarLocation = vehicle.getCurrentLocation();
    }

    public void setCarYaw() {
        currentYaw = vehicle.getCurrentYaw();
    }

    public void setVehicle(Vehicle car) {
        vehicle = car;
    }



}