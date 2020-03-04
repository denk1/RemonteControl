package com.example.den.remontecontrol;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class JoystickCommunication {

    public static volatile JoystickCommunication instance = null;
    private WorkActivity mJoystickActivity = null;
    private UsbDevice mUsbDevice = null;
    private UsbManager mUsbManager = null;
    private UsbDeviceConnection mUsbDeviceConnection = null;
    private UsbEndpoint mUsbEndpoint1 = null;
    private UsbEndpoint mUsbEndpoint2 = null;
    private Handler mainHandler = null;
    private final String TAG = "JoystickCommutation";
    private final String USB_ACTION_PERMITION = "permition";
    private boolean mDisableUsbThread = false;
    private CommandAdapter mCommandAdapter = null;
    private int [] convert = new int[256];

    public static JoystickCommunication getInstance(WorkActivity joystickActivity) {
        if(instance == null)
            synchronized (JoystickCommunication.class) {
                if(instance == null)
                    instance = new JoystickCommunication(joystickActivity);
            }
        return instance;
    }

    private JoystickCommunication(WorkActivity joystickActivity) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(USB_ACTION_PERMITION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        joystickActivity.registerReceiver(broadcastReceiver, intentFilter);
        mJoystickActivity = joystickActivity;
        mainHandler = new Handler(Looper.getMainLooper());
        byte[] byteA = new byte[8];
        Arrays.fill(byteA, (byte) 0);
        byte testValue = 127;
        byteA[0] = testValue; byteA[1] = testValue; byteA[2] = testValue; byteA[3] = testValue;
        byteA[4] = testValue; byteA[5] = testValue; byteA[6] = testValue; byteA[7] = testValue;
        JoystickProvider.getInstance().setByteA(byteA);
        initConvert();
        mCommandAdapter = new CommandAdapter();
        startUsbConnection(mJoystickActivity.getIntent());
        //startUsbConnection(null);
    }



    private void initConvert() {
        for(int i = 0; i < 128; i++) {

            convert[i] = -i;
            convert[i + 127 + 1] = 128 - i;

        }
    }


    private BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction() != null) {
                if (intent.getAction().equals(USB_ACTION_PERMITION)) {
                    checkPermitionUSbAndStart(intent);
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    startUsbConnection(intent);

                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    stopUsbConnection();
                }
            }
        }
    };



    private void startUsbConnection(Intent intent) {
        mUsbManager = (UsbManager) mJoystickActivity.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if(deviceList != null) {
            log("size of deviceList: " + deviceList.size());
        }
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            mUsbDevice = deviceIterator.next();
            //your code
        }
        //Log.d(TAG, "Found USB device" + mUsbDevice.toString());
        if(mUsbDevice != null) {
            log("the device has found");
            boolean granted = false;
            if(intent != null) {
                Bundle bundle = intent.getExtras();
                if(bundle != null)
                    granted = bundle.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
            }
            //Toast.makeText(mJoystickActivity, "working here", Toast.LENGTH_SHORT  ).show();
            //if(!granted) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mJoystickActivity, 0, new Intent(USB_ACTION_PERMITION), 0);
                mUsbManager.requestPermission(mUsbDevice, pendingIntent);
            //} else {
            //    checkPermitionUSbAndStart(intent);
            //}
            mDisableUsbThread = false;
        } else {
            log("the device hasn't found");
        }
    }

    private void stopUsbConnection() {
        mDisableUsbThread = true;
        if(mUsbDeviceConnection != null)
            mUsbDeviceConnection.close();
        setDeactivateInd();
    }

    private void initConnection() {
        UsbInterface usbInterface = mUsbDevice.getInterface(0);
        UsbDeviceConnection usbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
        mUsbDeviceConnection = usbDeviceConnection;
        if (usbDeviceConnection != null && usbInterface != null && usbDeviceConnection.claimInterface(usbInterface, true)) {
            log("the connection is OK");
            log("endpoint count: " + usbInterface.getEndpointCount());
            mUsbEndpoint1 = usbInterface.getEndpoint(0);
            mUsbEndpoint2 = usbInterface.getEndpoint(1);
            int directionEndpoint1 = mUsbEndpoint1.getDirection();
            int directionEndpoint2 = mUsbEndpoint2.getDirection();
            int maxPacketSize1 = mUsbEndpoint1.getMaxPacketSize();
            int maxPacketSize2 = mUsbEndpoint2.getMaxPacketSize();
            int typeEndPoint1 = mUsbEndpoint1.getType();
            int typeEndPoint2 = mUsbEndpoint2.getType();
            log("endpoint 1 direction value: " + directionEndpoint1);
            log("packet size 1: " + maxPacketSize1);
            log("type of endpoint 1: " + typeEndPoint1);
            log("endpoint 2 direction value: " + directionEndpoint2);
            log("packet size 2 : " + maxPacketSize2);
            log("type of endpoint 2: " + typeEndPoint2);
            StartTask startTask = new StartTask();
            new Thread(startTask).start();
            setActivatedInd();
        }
    }

    private void log(final String str_log) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(mJoystickActivity, str_log, Toast.LENGTH_SHORT).show();
                Log.d(TAG, str_log);
            }
        });
    }

    public void startThreadUsb() {
        ExampleThread exampleThread = new ExampleThread(10);
        exampleThread.start();
    }

    class StartTask implements Runnable {
        @Override
        public void run() {
            boolean hasData;
            byte[] byteA = new byte[mUsbEndpoint1.getMaxPacketSize()];
            log("Starting Runnable");
            Arrays.fill(byteA, (byte) 0);


            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mJoystickActivity, "Start task from Joystick", Toast.LENGTH_SHORT ).show();
                }
            });

            while (!mDisableUsbThread) {
                UsbRequest request = new UsbRequest();

                if (!request.initialize(mUsbDeviceConnection, mUsbEndpoint1)) {
                    log("request not initialized");
                }

                //ByteBuffer byteBuffer = ByteBuffer.wrap(byteA);
                //request.queue(byteBuffer, 8);
                //mUsbDeviceConnection.requestWait();
                hasData = mUsbDeviceConnection.bulkTransfer(mUsbEndpoint1, byteA, mUsbEndpoint1.getMaxPacketSize(), 2000) >= 0;

                //final String testStr = byteBuffer.array().toString();
                //resultBuilder.append(testStr);

                if(hasData) {
                    JoystickProvider.getInstance().getJoystick1().x = (int) byteA[0];
                    JoystickProvider.getInstance().getJoystick1().y = (int) byteA[1];
                    JoystickProvider.getInstance().getJoystick2().x = (int) byteA[2];
                    JoystickProvider.getInstance().getJoystick2().y = (int) byteA[3];
                    JoystickProvider.getInstance().setByteA(byteA);
                    sendCommand(convert[(int) byteA[0] + 128], convert[(int) byteA[1] + 128], convert[(int) byteA[2] + 128], convert[(int) byteA[3] + 128]);
                }
                request.close();
            }
        }
    }

    private void sendCommand(int x1, int y1, int x2, int y2) {
       mCommandAdapter.sendingCommand(x1, y1, x2, y2);
    }

    private void setActivatedInd() {
        Resources.Theme theme = mJoystickActivity.getTheme();
        Drawable drawable = mJoystickActivity.getResources().getDrawable(R.drawable.back, theme);
        mJoystickActivity.getJoystickInd().setBackground(drawable);
        mJoystickActivity.getJoystickInd().setText("Джойстик подключен");
    }

    private void setDeactivateInd() {
        Resources.Theme theme = mJoystickActivity.getTheme();
        Drawable drawable = mJoystickActivity.getResources().getDrawable(R.drawable.back_alarm, theme);
        mJoystickActivity.getJoystickInd().setBackground(drawable);
        mJoystickActivity.getJoystickInd().setText("Джойстик отключен");
    }

    class UsbTask extends AsyncTask<String, String, String> {
        UsbTask() {
            super();
        }

        @Override
        protected String doInBackground(String... strings) {

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            byte[] byteA = new byte[mUsbEndpoint1.getMaxPacketSize()];
            log("Starting AsyncTask");
            Arrays.fill(byteA, (byte) 0);
            byteA[0] = 88; byteA[1] = 88; byteA[2] = 88; byteA[3] = 88;
            byteA[4] = 88; byteA[5] = 88; byteA[6] = 88; byteA[7] = 88;
            JoystickProvider.getInstance().setByteA(byteA);
        }
    }

    private void checkPermitionUSbAndStart(Intent intent) {
        boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
        Log.i(TAG, "granted permission");
        if (granted) {
            Log.i(TAG, "grant has been getting");
            initConnection();
        } else {
            Log.i(TAG, "not permission usb-granted");
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

    class ExampleThread extends Thread {
        int seconds;

        ExampleThread(int seconds) {
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
