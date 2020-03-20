package com.example.den.remontecontrol;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;


import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static java.util.Arrays.copyOf;

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
    private Thread getTelemetryThread = null;
    private boolean mRun = true;
    private InfoManager infoManager = null;
    private boolean isEndingFrameTM;
    private int remain_bytes = 0;
    private byte[] buff = new byte[16];
    private int prev_readed_bytes = 0;
    private int current_read_bytes = 0;
    private ArrayList<Byte> bytes_buff = new ArrayList<>();
    private HandlerThread handlerThread = null;
    private ConnectionHandler connectionHandler = null;

    public ConnectionTCP() {
        getTelemetryThread = new Thread(getTelemetryRunnable);
        infoManager = InfoManager.createInfoManager();
        handlerThread = new HandlerThread("SenderCommand", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        connectionHandler = new ConnectionHandler(looper);
        initConnMsg();
        getTelemetryThread.start();
    }

    @Override
    public void initConnection() {
        try {
            mSocket = new Socket(WorkActivity.IP_CONTROL, WorkActivity.PORT_CONTROL);
            mSocket.setKeepAlive(true);
            mSocket.setSoTimeout(90000);
            inputStream = mSocket.getInputStream();
            outputStream = mSocket.getOutputStream();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean sendCommand(String strCmd) {
        try {
            synchronized (this) {
                jsonObject = new JSONObject(strCmd);
                String command = jsonObject.getString("action");
                setCommand(command);
            }
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
            sendMsg();
        }
        else if(strCmd.equals("turn_right_down")) {
            setSteeringAngle();
        }
        else if(strCmd.equals("turn_right_up")) {
            steeringAngle = 0;
            isSendingSteering = false;
            isLastSteering = true;
            sendMsg();
        }
        else if(strCmd.equals("racing_down")) {
            signMoving = 1;
            setMovingProc();

        }
        else if(strCmd.equals("racing_up")) {
            throttleProc = 0;
            isSendingMoving = false;
            isLastMoving = true;
            sendMsg();
        }
        else if(strCmd.equals("stopping_down")) {
            signMoving = -1;
            setMovingProc();
        }
        else if(strCmd.equals("stopping_up")) {
            throttleProc = 0;
            isSendingMoving = false;
            isLastMoving = true;
            sendMsg();
        }
        else {
            Log.e(TAG, "the command's recognised ");
        }
    }

    private void setSteeringAngle() {
        try {
            JSONObject jsonParams = jsonObject.getJSONObject("params");
            steeringAngle = jsonParams.getInt("steering_angle")/2;
            //isSendingSteering = true;
            sendMsg();
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setMovingProc() {
        try {
            JSONObject jsonParams = jsonObject.getJSONObject("params");
            throttleProc = jsonParams.getInt("throttle_proc");
            if (throttleProc < 200)
                throttleProc = throttleProc * signMoving;
            else if(throttleProc == 2300) {
                throttleProc = 1500;
            }
            else {
                int shift = throttleProc - 800;
                throttleProc = 800 + shift * signMoving;
            }
            sendMsg();
            //isSendingMoving = true;
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private Runnable sendCommandRunnable = new Runnable() {
        @Override
        public void run() {
            //initConnection();
            //getTelemetryThread.start();
            while (mRun) {
                //getInfo();
                try {

                    if(isSendingSteering || isSendingMoving) {
                        formmingCommand(steeringAngle, throttleProc);
                    }
                    Thread.sleep(10);
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
                            if(outputStream != null)
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

    private Runnable getTelemetryRunnable = new Runnable() {
        @Override
        public void run() {
            while (mRun) {
                try {
                    Thread.sleep(30);
                    getInfo();
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
       if(!sendCommandThread.isAlive()) {
           mRun = true;
           sendCommandThread.start();
       }
    }

    @Override
    public void setDisactivated() {
        mRun = false;
        if(!sendCommandThread.isAlive()) {
            sendCommandThread.interrupt();
        }
        if(mSocket != null) {
            if (mSocket.isConnected()) {
                try {
                    outputStream.close();
                    inputStream.close();
                    mSocket.close();
                } catch (Exception e) {
                    Log.e(TAG, "the error of the closing connection");
                }
            }
        }
    }

    //getting information from vehicle

    private void getInfo() {

        byte[] bytes = new byte[18];
        int readed_bytes = 0;
        try {
            readed_bytes = inputStream.read(bytes);

            if(readed_bytes!=bytes.length) {
                remain_bytes = bytes.length - readed_bytes;
                if(!isWholePocket(bytes)) {
                    if(current_read_bytes + readed_bytes >= bytes.length) {
                        bytes_buff.addAll(Arrays.asList(ArrayUtils.toObject( Arrays.copyOf(buff, readed_bytes))));
                        Byte[] temp_bytes = bytes_buff.toArray(new Byte[bytes_buff.size()]);
                        parsingData(ArrayUtils.toPrimitive(temp_bytes));
                        bytes_buff.clear();
                    } else {
                        bytes_buff.addAll(Arrays.asList(ArrayUtils.toObject( Arrays.copyOf(buff, readed_bytes))));
                    }
                } else {
                    bytes_buff.addAll(Arrays.asList(ArrayUtils.toObject( Arrays.copyOf(buff, readed_bytes))));
                }
            } else {
                if(isWholePocket(bytes)) {
                    parsingData(bytes);
                    bytes_buff.clear();
                } else {
                     int remain_bytes = readed_bytes - bytes_buff.size();
                     byte[] remain_array = Arrays.copyOf(buff, remain_bytes);
                     bytes_buff.addAll(Arrays.asList(ArrayUtils.toObject(remain_array)));
                     parsingData(bytes);
                     bytes_buff.clear();
                }
            }
        }catch (Exception e) {
            Log.e(TAG, "the error of the reading bytes from the input stream");
        }finally {
            prev_readed_bytes = readed_bytes;
            current_read_bytes += readed_bytes;
        }
    }

    private boolean isWholePocket(byte [] bytes) {
        return bytes[0] == 'T' && bytes[1] == 'M';
    }

    private void parsingData(byte[] bytes) {
        byte[] velocityBytes = Arrays.copyOfRange(bytes, 2, 10);
        byte[] angleBytes = Arrays.copyOfRange(bytes,10, 18);
        double velocity = ConvertData.toDouble(velocityBytes);
        double angle = ConvertData.toDouble(angleBytes);
        infoManager.handleParam((float)velocity, InfoManager.CURRENT_VELOCITY_VALUE);
        infoManager.handleParam((float)angle, InfoManager.CURRENT_STEERING_ANGLE);
        buff = bytes;
    }

    private void formmingCommand(int steer, int throttle) {
        try {
            int [] params = {steer, throttle};
            if(outputStream != null)
                outputStream.write(toByteArray(params));
        }
        catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void sendMsg() {
        Message msg = connectionHandler.obtainMessage();
        msg.arg1 = 2;
        connectionHandler.sendMessage(msg);
    }

    private void sendCommand() {
        formmingCommand(steeringAngle, throttleProc);
    }

    private void initConnMsg() {
        Message msg = connectionHandler.obtainMessage();
        msg.arg1 = 1;
        connectionHandler.sendMessage(msg);
    }

    private final class ConnectionHandler extends Handler {
        public ConnectionHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 == 2)
                sendCommand();
            else if(msg.arg1 == 1) {
                initConnection();
            }

        }
    }
}
