package com.example.den.remontecontrol;

import android.location.OnNmeaMessageListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Client {
    public static final String TAG = Client.class.getSimpleName();
    //public static final String SERVER_IP = "192.168.43.249";
    //public static final int SERVER_PORT = 8080;
    public static final String SERVER_IP = "10.91.1.33";
    public static final int SERVER_PORT = 50000;
    private OnMessageReceived mMessageListener = null;
    private CarMapActivity.ConnectTask mAsyncTask;
    // message to send to the server

    private boolean mRun = false;

    private Handler handler;

    Thread sendPositionThread;


    private Double [] doubles = new Double[10];

    public Client(OnMessageReceived listener, CarMapActivity.ConnectTask asyncTask) {
        Arrays.fill(doubles, 0.0);
        mMessageListener = listener;
        sendPositionThread = new Thread(sendPositionInfoRunnable);
        mAsyncTask = asyncTask;
    }

    Runnable sendPositionInfoRunnable = new Runnable() {
        @Override
        public void run() {
            while (mRun) {
                try {


                    Thread.sleep(2000);
                }
                catch (Exception e) {

                }
                mMessageListener.messageReceived(doubles);
            }
        }
    };

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.d("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);
            sendPositionThread.start();

            try {

                //receives the message which the server sends back
                //mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InputStream inputStream = socket.getInputStream();
                byte [] buff = new byte[80];

                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    if(mAsyncTask.isCancelled()) {
                        mRun = false;
                        sendPositionThread.interrupt();
                    }
                    //mServerMessage = mBufferIn.readLine();
                    int i = inputStream.read(buff, 0, 80);
                    Log.i(TAG, String.valueOf(i));


                    if(i > 0) {
                        byte[] valueBuff = Arrays.copyOfRange(buff, 0, 8);
                        byte[] valueBuff1 = Arrays.copyOfRange(buff, 8, 16);
                        byte[] valueBuff2 = Arrays.copyOfRange(buff, 16, 24);
                        byte[] valueBuff3 = Arrays.copyOfRange(buff, 24, 32);
                        byte[] valueBuff4 = Arrays.copyOfRange(buff, 32, 40);
                        byte[] valueBuff5 = Arrays.copyOfRange(buff, 40, 48);
                        byte[] valueBuff6 = Arrays.copyOfRange(buff, 48, 56);
                        byte[] valueBuff7 = Arrays.copyOfRange(buff, 56, 64);
                        byte[] valueBuff8 = Arrays.copyOfRange(buff, 64, 72);
                        byte[] valueBuff9 = Arrays.copyOfRange(buff, 72, 80);

                        doubles[0] = toDouble(valueBuff);
                        doubles[1] = toDouble(valueBuff1);
                        doubles[2] = toDouble(valueBuff2);
                        doubles[3] = toDouble(valueBuff3);
                        doubles[4] = toDouble(valueBuff4);
                        doubles[5] = toDouble(valueBuff5);
                        doubles[6] = toDouble(valueBuff6);
                        doubles[7] = toDouble(valueBuff7);
                        doubles[8] = toDouble(valueBuff8);
                        doubles[9] = toDouble(valueBuff9);
                        //mAsyncTask.setIndicatorConnectionOk();
                    }
                }
            } catch (Exception e) {
                Log.e("TCP", "S: Error", e);
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                //mAsyncTask.setIndicatorConnectionFailed();
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            //mAsyncTask.setIndicatorConnectionFailed();
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(Double[] message);
    }

    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }

}

