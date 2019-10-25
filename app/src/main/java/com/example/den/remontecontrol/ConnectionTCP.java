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
    private boolean isSending = false;
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
            isSending = false;
        }
        else if(strCmd.equals("turn_right_down")) {
            setSteeringAngle();
        }
        else if(strCmd.equals("turn_right_up")) {
            isSending = false;
        }
        else {
            Log.e(TAG, "the command's recognised ");
        }
    }

    private void setSteeringAngle() {
        try {
            JSONObject jsonParams = jsonObject.getJSONObject("params");
            steeringAngle = jsonParams.getInt("steering_angle");
            isSending = true;
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
                    if(isSending) {
                        try {
                            outputStream.write(toByteArray(steeringAngle));
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

    byte[] toByteArray(int value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
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
