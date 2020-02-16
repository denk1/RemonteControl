package com.example.den.remontecontrol;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class CircleView extends View {
    int backgroundColor;
    int innerCircleColor;
    int outerCircleColor;
    final int RADIUS_INNER = 100;
    final int RADIUS_OUTTER = 300;
    int [] convert = new int[256];
    PointF mPoint = null;

    void initConvert() {
        for(int i = 0; i < 128; i++) {

            convert[i] = -i;
            convert[i + 127 + 1] = 128 - i;

        }
    }

    public CircleView(Context context, AttributeSet attr) {
        super(context, attr);
        mPoint = new PointF(0,0);
        initConvert();
        TypedArray a = context.getTheme().obtainStyledAttributes(attr, R.styleable.CircleView, 0 , 0);
        try {
            backgroundColor = a.getColor(R.styleable.CircleView_backgroundColor, Color.WHITE);
            innerCircleColor = a.getColor(R.styleable.CircleView_innerCircleColor, Color.parseColor("#CD5C5C"));
            outerCircleColor = a.getColor(R.styleable.CircleView_outerCircleColor, Color.parseColor("#5CCD5C"));

        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = getWidth();
        int y = getHeight();
        int radiusInner = 100;
        int radiusOuter = 300;

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(backgroundColor);
        canvas.drawPaint(paint);
        paint.setColor(outerCircleColor);
        canvas.drawCircle(x/2, y/2, radiusOuter, paint);
        paint.setColor(innerCircleColor);
        canvas.drawCircle((float) x /2 + convert[(int)mPoint.x + 128], (float) y/2 + convert[(int)mPoint.y + 128], radiusInner, paint);
    }

    public void setCurrPointPos(PointF mPoint) {
        this.mPoint = mPoint;
    }

    public void setCurrX(float v) {
        mPoint.x = v;
    }

    public void setCurrY(float v) {
        mPoint.y = v;
    }

    public int getRadiusInner() {
        return RADIUS_INNER;
    }

    public int getRadiusOuter() {
        return RADIUS_OUTTER;
    }

    public int[] getConvert() {
        return convert;
    }
}
