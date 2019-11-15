package com.example.den.remontecontrol;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionTCP implements Connection {
    public final String TAG = "ConnectionTCP";
    private Socket mSocket = null;
    private InputStream inputStream;
    private OutputStream outputStream;
    private JSONObject jsonObject;
    private int steeringAngle = 0;
    private int throttleProc = 0;
    private int signMoving = 1;
    private boolean isSendingMoving = false;
    private boolean isSendingSteering =false;
    private boolean isLastSteering = false;
    private boolean isLastMoving = false;
    private Thread sendCommandThread = null;
    private boolean mRun = true;

    public ConnectionTCP() {
        sendCommandThread = new Thread(sendCommandRunnable);
        sendCommandThread.start();
    }

    @Override
    public void initConnection() {
        try {
            //WorkActivity.IP_CONTROL = "192.168.88.199";
            //WorkActivity.PORT_CONTROL = 8001;
            mSocket = new Socket(WorkActivity.IP_CONTROL, WorkActivity.PORT_CONTROL);
            mSocket.setKeepAlive(true);
            mSocket.setSoTimeout(90000);
            inputStream = mSocket.getInputStream();
            outputStream = mSocket.getOutputStream();
            //byte[] bytes = {1};
            //outputStream.write(bytes);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean sendCommand(String strCmd) {
        try {
            jsonObject = new JSONObject(strCmd);
            String command = jsonObject.getString("action");
            setCommand(command);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    private void setCommand(String strCmd) {
        if(strCmd.equals("turn_left_down")) {
            setSteeringAngle();
        }
        else if(strCmd.equals("turn_left_up")) {
            steeringAngle = 0;
            isSendingSteering = false;
            isLastSteering = true;
        }
        else if(strCmd.equals("turn_right_down")) {
            setSteeringAngle();
        }
        else if(strCmd.equals("turn_right_up")) {
            steeringAngle = 0;
            isSendingSteering = false;
            isLastSteering = true;
        }
        else if(strCmd.equals("racing_down")) {
            signMoving = 1;
            setMovingProc();
        }
        else if(strCmd.equals("racing_up")) {
            throttleProc = 0;
            isSendingMoving = false;
            isLastMoving = true;
        }
        else if(strCmd.equals("stopping_down")) {
            signMoving = -1;
            setMovingProc();
        }
        else if(strCmd.equals("stopping_up")) {
            throttleProc = 0;
            isSendingMoving = false;
            isLastMoving = true;
        }
        else {
            Log.e(TAG, "the command's recognised ");
        }
    }

    private void setSteeringAngle() {
        try {
            JSONObject jsonParams = jsonObject.getJSONObject("params");
            steeringAngle = jsonParams.getInt("steering_angle");
            isSendingSteering = true;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setMovingProc() {
        try {
            JSONObject jsonParams = jsonObject.getJSONObject("params");
            throttleProc = jsonParams.getInt("throttle_proc") * signMoving;
            isSendingMoving = true;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private Runnable sendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            initConnection();
            while (mRun) {
                try {
                    Thread.sleep(10);
                    if(isSendingSteering || isSendingMoving) {
                        try {
                            int [] params = {steeringAngle, throttleProc};
                            outputStream.write(toByteArray(params));
                        }
                        catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }

                    }

                    if (isLastMoving || isLastSteering) {
                        try {
                            int [] params = {steeringAngle, throttleProc};
                            if(isLastSteering) {
                                params[0] = 0;
                                isLastSteering = false;
                            }
                            if(isLastMoving) {
                                params[1] = 0;
                                isLastMoving = false;
                            }
                            outputStream.write(toByteArray(params));

                        }
                        catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                }
                catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        }
    };

    private byte[] toByteArray(int[] value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putDouble((double) value[0]);
        byteBuffer.putDouble((double) value[1]);
        return byteBuffer.array();
    }

    @Override
    public boolean isConnected() {
        if(mSocket != null) {
            return mSocket.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void setActivated() {
       if(sendCommandThread.isInterrupted()) {
           sendCommandThread.start();
       }
    }

    @Override
    public void setDisactivated() {
        if(sendCommandThread.isAlive()) {
            sendCommandThread.interrupt();
        }
    }
}
