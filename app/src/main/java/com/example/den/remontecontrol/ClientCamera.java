package com.example.den.remontecontrol;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;

    import java.io.InputStream;
    import java.io.OutputStream;
    import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.ByteBuffer.wrap;
import static java.util.Arrays.copyOfRange;

public class ClientCamera {

    private static ClientCamera clientCamera;
    private Thread sendFrameThread = null;
    private Socket mSocket = null;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final String TAG = "ClientCamera";
    private final int MAX_READ_BYTES = 102400;
    private boolean mRun = true;
    private boolean isConnect = false;
    private int total_read_byte_image = 0;
    private int current_read_byte = 0;
    private byte[] bytes_read_image = null;
    private int sizeImage = 0;
    private byte[] buff;
    private List<Byte> listArrayImage = null;
    private boolean isSendingImage = true;
    private boolean isRecieveImage = false;
    private boolean isContinueRecieveImage = false;
    private OnMessageReceived mMessageListener = null;
    private QueryFrameHandler mQueryFrameHandler = null;

    public ClientCamera(OnMessageReceived listener) {
        mMessageListener = listener;
        sendFrameThread = new Thread(sendFrameRunnable);
        listArrayImage =  new ArrayList();
        buff = new byte[MAX_READ_BYTES];

        try {

            mSocket = new Socket(WorkActivity.IP_VIDEO, WorkActivity.PORT_VIDEO);
            mSocket.setKeepAlive(true);
            mSocket.setSoTimeout(90000);
            inputStream = mSocket.getInputStream();
            outputStream = mSocket.getOutputStream();
            mQueryFrameHandler = new QueryFrameHandler(outputStream);
        } catch (Exception e) {
            Log.e(TAG, "The error to initialize TCP-socket");
        }
        sendFrameThread.start();
    }



    Runnable sendFrameRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                byte[] initialMessage = {0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                outputStream.write(initialMessage);
                outputStream.flush();
                while (mRun) {
                    if(current_read_byte == 0) {
                        current_read_byte = inputStream.read(buff, 0, MAX_READ_BYTES);
                    }

                    if(current_read_byte > 0) {
                        if(buff[0]==0x04 || isContinueRecieveImage) {
                            int offset = 0;
                            if(buff[0] == 0x04 && total_read_byte_image == 0) {
                                offset = 5;
                                sizeImage = ByteBuffer.wrap(buff, 1, 5).getInt();
                                current_read_byte -= 5;
                            }

                            if(sizeImage > (total_read_byte_image + current_read_byte)) {
                                bytes_read_image =  Arrays.copyOfRange(buff, offset, current_read_byte + offset);
                                isContinueRecieveImage = true;
                                total_read_byte_image += current_read_byte;
                                current_read_byte = 0;
                            } else {
                                bytes_read_image = Arrays.copyOfRange(buff, offset, sizeImage - total_read_byte_image);
                                //current_read_byte = total_read_byte_image + current_read_byte - sizeImage;
                                //byte[] buff_remain = Arrays.copyOfRange(buff, current_read_byte, buff.length);
                                isContinueRecieveImage = false;
                                isRecieveImage = true;
                                current_read_byte = total_read_byte_image + current_read_byte - sizeImage;
                                byte[] buff_remain = Arrays.copyOfRange(buff, sizeImage - total_read_byte_image + offset, sizeImage - total_read_byte_image + current_read_byte + offset);
                                System.arraycopy(buff_remain, 0, buff, 0, buff_remain.length);
                            }
                            listArrayImage.addAll( Arrays.asList(ArrayUtils.toObject(Arrays.copyOfRange(bytes_read_image, 0, bytes_read_image.length ))));

                             if(isRecieveImage) {
                                 recieveImageBytes();
                                 isRecieveImage = false;
                                 total_read_byte_image = 0;
                             }
                        }
                        else if (buff[0]==(byte)0x03 && buff[1]==(byte)0x7f && buff[2]==(byte)0x80) {
                            isConnect = true;
                            byte [] answerByte = {buff[0], buff[1], buff[2]};
                            //inputStream = mSocket.getInputStream();
                            //outputStream = mSocket.getOutputStream();
                            current_read_byte -= 3;
                            outputStream.write(answerByte);
                            outputStream.flush();
                        }
                    }
                    else {
                        mRun = false;
                    }
                    if(isSendingImage) {
                        byte[] msgQueryImage = {0x03, 0x00, 0x00, 0x00};
                        //outputStream.write(msgQueryImage);
                        isSendingImage = false;
                    }
                    //ByteBuffer byteBuffer = ByteBuffer.allocate(4 * w * h);
                    //byte[] byteArray = byteBuffer.array();
                    //Arrays.fill(byteArray, 0, byteArray.length, (byte) 0x00);
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    //double pause = 1000.0 / 30.0;
                    try {
                        //FrameManager.getInstance().handleParam(bitmap, FrameManager.CURRENT_FRAME_VALUE);
                        Thread.sleep(33);
                    } catch (Exception e) {

                    }
                }
            }catch (Exception e) {
                Log.e(TAG, "TCP opening  Error", e);

            }finally {
                try {
                    mSocket.close();
                } catch (Exception e) {
                    Log.e(TAG, "TCP-socket closing error");
                }

            }
            buff = copyOfRange(buff, current_read_byte, buff.length);
        }
    };

    public void sendQueryImage() {
        try {
            mQueryFrameHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            Log.e(TAG, "the error of sending message new frame");
        }
    }

    public interface OnMessageReceived {
        public void messageReceived(Byte[] message);
    }

    private void recieveImageBytes() {
        Byte [] sending_arr = listArrayImage.toArray(new Byte[listArrayImage.size()]);
        mMessageListener.messageReceived(sending_arr);
        listArrayImage.clear();
        total_read_byte_image = 0;
    }

    private static class QueryFrameHandler extends Handler {
        final String TAG = "QueryFrameHandler";
        private OutputStream mOutputStream;
        public QueryFrameHandler(OutputStream outputStream) {
            super(Looper.getMainLooper());
            mOutputStream = outputStream;
        }

        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            try {
                new QueryTask(mOutputStream).execute("");
            }catch (Exception e) {
                Log.e(TAG, "the error of sending query of the new frame");
            }
        }
    }

    public static class QueryTask extends AsyncTask<String, Void, Void> {
        final String TAG = "QueryTask";
        private OutputStream mOutputStream;
        public QueryTask(OutputStream outputStream) {
            mOutputStream = outputStream;
        }

        @Override
        protected Void doInBackground(String... params) {
            byte[] msgQueryImage = {0x03, 0x00, 0x00, 0x00};
            try {
                mOutputStream.write(msgQueryImage);
                mOutputStream.flush();
            }catch (Exception e) {
                Log.e(TAG, "the error of sending query of the new frame");
            }
            return null;
        }
    }
}
