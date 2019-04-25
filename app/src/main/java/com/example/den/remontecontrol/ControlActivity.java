package com.example.den.remontecontrol;


import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import android.annotation.SuppressLint;

import static java.lang.Math.abs;


public class ControlActivity extends AppCompatActivity {

    TabHost tabHost;
    SeekBar seekBarTurnFront;
    SeekBar seekBarTurnRear;
    SeekBar seekBarTurnVelocity;
    SeekBar seekBarCurrAngle;
    Context cxt;
    TextView textViewSteerCorner, textViewSteerRearCorner;
    TextView textBoxCurrSteer;

    // joystick

    private TextView mTextViewAngleLeft;
    private TextView mTextViewStrengthLeft;

    private TextView mTextViewAngleRight;
    private TextView mTextViewStrengthRight;
    private TextView mTextViewCoordinateRight;

    // drive

    private SeekBar seekBarTurn = null;
    private SeekBar seekBarAccel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        tabHost = findViewById(R.id.tabHost);
        seekBarTurnFront = findViewById(R.id.seekBar);
        seekBarTurnRear = findViewById(R.id.seekBar1);
        seekBarTurnVelocity = findViewById(R.id.seekBar2);
        seekBarCurrAngle = findViewById(R.id.seekBarCurrAngle);
        textBoxCurrSteer = findViewById(R.id.textBoxCurrSteer);
        seekBarCurrAngle.setOnTouchListener(new SeekBar.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        seekBarTurnFront.setMax(100);
        seekBarTurnFront.setProgress(50);
        seekBarTurnRear.setMax(100);
        seekBarTurnRear.setProgress(50);
        seekBarTurnVelocity.setMax(100);
        seekBarTurnVelocity.setProgress(50);
        textViewSteerCorner = (TextView)findViewById(R.id.textViewSteerCorner);
        textViewSteerRearCorner = (TextView)findViewById(R.id.textViewSteerRearCorner);
        cxt = getBaseContext();


        //proc front steer

        seekBarTurnFront.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSteerCorner.setText(String.valueOf(50 - progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //proc rear steer

        seekBarTurnRear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSteerRearCorner.setText(String.valueOf(50 - progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tabHost.setup();

        //Tab1
        TabHost.TabSpec spec = tabHost.newTabSpec(getResources().getString(R.string.tab_one_title));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.tab_one_title));
        tabHost.addTab(spec);

        //Tab2
        spec = tabHost.newTabSpec(getResources().getString(R.string.tab_two_title));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.tab_two_title));
        tabHost.addTab(spec);

        //Tab3
        spec = tabHost.newTabSpec(getResources().getString(R.string.tab_three_title));
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.tab_three_title));
        tabHost.addTab(spec);

        //Tab4
        spec = tabHost.newTabSpec("Управление");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Управление");
        tabHost.addTab(spec);

        //event
        tabHost.setOnTabChangedListener(new AnimationTabListener(this, tabHost));

        // joystick

        mTextViewAngleLeft = (TextView) findViewById(R.id.textView_angle_left);
        mTextViewStrengthLeft = (TextView) findViewById(R.id.textView_strength_left);

        JoystickView joystickLeft = (JoystickView) findViewById(R.id.joystickView_left);
        final CommandControl commandControl = MainActivity.getCommandControl();
        joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngleLeft.setText(angle + "°");
                mTextViewStrengthLeft.setText(strength + "%");
                int turnValueAngle = 50 - strength;
                if(strength <= 7) {
                    commandControl.turnLeftUp();
                    commandControl.turnRightUp();
                }
                else if((angle >= 270 || angle < 90))
                {
                    commandControl.turnLeftUp();
                    commandControl.turnRightDown(turnValueAngle);
                }
                else
                {
                    commandControl.turnRightUp();
                    commandControl.turnLeftDown(turnValueAngle);
                }

            }
        });


        mTextViewAngleRight = (TextView) findViewById(R.id.textView_angle_right);
        mTextViewStrengthRight = (TextView) findViewById(R.id.textView_strength_right);
        mTextViewCoordinateRight = findViewById(R.id.textView_coordinate_right);


        final JoystickView joystickRight = (JoystickView) findViewById(R.id.joystickView_right);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onMove(int angle, int strength) {
                mTextViewAngleRight.setText(angle + "°");
                mTextViewStrengthRight.setText(strength + "%");
                mTextViewCoordinateRight.setText(
                        String.format("x%03d:y%03d",
                                joystickRight.getNormalizedX(),
                                joystickRight.getNormalizedY())
                );
                if(strength <= 7)
                {
                    commandControl.racingUp();
                    commandControl.stoppingUp();
                }
                else if(angle < 180)
                {
                    commandControl.stoppingUp();
                    commandControl.racingDown(strength);
                }
                else
                {
                    commandControl.racingUp();
                    commandControl.stoppingDown(strength);
                }
            }
        });

        TextView txtViewVelocityValue = (TextView)findViewById(R.id.velocity_value);
        InfoParameters infoParameters = InfoParameters.createInfoParameters();
        infoParameters.setViewOfParam("velocity", txtViewVelocityValue);
        infoParameters.setViewOfParam("seekBarCurrAngle", seekBarCurrAngle);
        infoParameters.setViewOfParam("textBoxCurrSteer", textBoxCurrSteer);

        // drive pannel

        seekBarTurn = findViewById(R.id.seekBarTurn);
        seekBarAccel = findViewById(R.id.seekBarAccel);

        seekBarTurn.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int angle = 50 - progress;
                if(angle < 0) {
                    commandControl.turnLeftUp();
                    commandControl.turnRightDown(angle);
                }
                else if(angle > 0) {
                    commandControl.turnRightUp();
                    commandControl.turnLeftDown(angle);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                commandControl.turnRightUp();
                commandControl.turnLeftUp();
                seekBar.setProgress(50);
            }
        });

        seekBarAccel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int throttle_degree = 50 - progress;
                if(throttle_degree < 0) {
                    commandControl.stoppingUp();
                    commandControl.racingDown(abs(throttle_degree) * 2);
                }
                else if(throttle_degree > 0) {
                    commandControl.racingUp();
                    commandControl.stoppingDown(abs(throttle_degree) * 2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                commandControl.stoppingUp();
                commandControl.racingUp();
                seekBar.setProgress(50);
            }
        });

    }
}


