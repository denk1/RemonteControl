package com.example.den.remontecontrol;

import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class JoystickActivity extends AppCompatActivity {
    CircleView circleView = null;
    CircleView circleView2 = null;
    TextView textView = null;
    Button button = null;
    Handler handler = null;

    final String TAG = "MainActivity";
    PointF mPointF = null;
    boolean isRun = true;
    float sign = 1;
    JoystickCommunication joystickCommunication = null;
    JoystickProvider mJoystickProvider = null;
    int [] convert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick);
        setTitle(R.string.title_joystick_activity);
        circleView = findViewById(R.id.circleView1);
        circleView2 = findViewById(R.id.circleView2);
        button = findViewById(R.id.button1);
        textView = findViewById(R.id.textView);
        mJoystickProvider = JoystickProvider.getInstance();
        joystickCommunication = JoystickCommunication.getInstance(this);
        mPointF = new PointF(0, 0);
        handler = new Handler();
        convert = circleView.getConvert();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joystickCommunication.startThreadUsb();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
        new MoveTask().execute("a", "b", "c");
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
    }

    public Handler getHandler() {
        return handler;
    }

    public void startThread() {
        for(int i =0; i < 10; i++) {
            Log.d(TAG, "startThread: " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {

    }

    class MoveTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... strings) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(JoystickActivity.this, "Starting AsyncTask from JoysticActivity", Toast.LENGTH_LONG).show();
                }
            });

            while (isRun) {
                try {
                    Thread.sleep(1);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPointF.x += sign;
                            mPointF.y += sign;
                            PointF mPointF1 = JoystickProvider.getInstance().getJoystick1();
                            PointF mPointF2 = JoystickProvider.getInstance().getJoystick2();
                            //stepMotion();
                            circleView.setCurrPointPos(mPointF1);
                            circleView2.setCurrPointPos(mPointF2);
                            circleView.invalidate();
                            circleView2.invalidate();
                            // the adding test
                            byte[] byteA = JoystickProvider.getInstance().getByteA();
                            textView.setText(String.format("%d %d %d %d %d %d %d %d",
                                    convert[(int) byteA[0] + 128],
                                    convert[(int) byteA[1] + 128],
                                    convert[(int) byteA[2] + 128],
                                    convert[(int) byteA[3] + 128],
                                    convert[(int) byteA[4] + 128],
                                    convert[(int) byteA[5] + 128],
                                    convert[(int) byteA[6] + 128],
                                    convert[(int) byteA[7] + 128]));
                        }
                    });

                } catch (InterruptedException e) {
                    Log.i(TAG, "cannot interrupt runnable");
                }
            }
            return null;
        }
    }

    private void stepMotion() {
        if (Math.sqrt(Math.pow(mPointF.x, 2.0) + Math.pow(mPointF.y, 2.0)) + circleView.getRadiusInner() > 300) {
            sign *= -1.0;
        }
    }


    class ExampleRunnable implements Runnable {
        int seconds;

        ExampleRunnable(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for(int i =0; i < seconds; i++) {
                Log.d(TAG, "startThread: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
