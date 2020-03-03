package com.example.den.remontecontrol;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

public class CommandAdapter {
    private CommandControl mCommandControl = null;

    public CommandAdapter() {

    }

    public void sendingCommand(int _x1, int _y1, int _x2, int _y2) {
        double x1 = (double)_x1;
        double y1 = (double)_y1;
        double x2 = (double)_x2;
        double y2 = (double)_y2;
        mCommandControl = WorkActivity.getCommandControl();
        if(mCommandControl != null) {
            double angle1 = Math.atan2( x1, y1);
            double d1 = Math.sin(angle1);
            double abs_normalize1 = (Math.min(abs_vector(x1, y1), 128.0) /128.0) * 100.0;
            double force1 = ((Math.min(abs_vector(x1, y1), 128.0) - 10.0)/118.0) * 100.0;
            double angle2 = Math.atan2(x2, y2);
            double d2 = Math.cos(angle2);
            double abs_normalize2 = (Math.min(abs_vector(x2, y2), 128.0) /128.0) * 100.0;
            double force2 = ((Math.min(abs_vector(x2, y2), 128.0) - 10.0)/118.0) * 100.0;
            if(abs_normalize1 < 10.0)
            {
                mCommandControl.turnLeftUp();
                mCommandControl.turnRightUp();
            } else {

                if (angle1 < 0) {
                    mCommandControl.turnRightDown((int) (d1 * force1));
                } else {
                    mCommandControl.turnLeftDown((int) (d1 * force1));
                }
            }

            if( abs_normalize2 <= 10.0)
            {
                mCommandControl.racingUp();
                mCommandControl.stoppingUp();
            }
            else
            {
                if(Math.abs(Math.toDegrees(angle1)) < 90.0) {
                    mCommandControl.stoppingUp();
                    mCommandControl.racingDown((int) (d2 * force2));
                } else {
                    mCommandControl.racingUp();
                    mCommandControl.stoppingDown((int)(-d2 * force2));
                }
            }
        }
    }

    private double abs_vector(double x, double y) {
        return  Math.sqrt(x*x + y*y );
    }
}
