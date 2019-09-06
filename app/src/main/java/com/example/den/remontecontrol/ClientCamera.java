package com.example.den.remontecontrol;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;


import java.io.ByteArrayInputStream;
import org.apache.commons.lang3.ArrayUtils;
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
    private int total_read_byte = 0;
    private int current_read_byte = 0;
    private byte[] bytes_read_image = null;
    private int sizeImage = 0;
    private byte[] buff;
    private List<Byte> listArrayImage = null;
    private boolean isSendingImage = true;
    private boolean isRecieveImage = false;
    private boolean isContinueRecieveImage = false;
    private OnMessageReceived mMessageListener = null;

    public ClientCamera(OnMessageReceived listener) {
        mMessageListener = listener;
        sendFrameThread = new Thread(sendFrameRunnable);
        listArrayImage =  new ArrayList();
        buff = new byte[MAX_READ_BYTES];
        sendFrameThread.start();
    }



    Runnable sendFrameRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mSocket = new Socket(WorkActivity.IP_VIDEO, WorkActivity.PORT_VIDEO);
                mSocket.setKeepAlive(true);
                mSocket.setSoTimeout(20000);
                inputStream = mSocket.getInputStream();
                outputStream = mSocket.getOutputStream();
                byte[] initialMessage = {0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
                outputStream.write(initialMessage);
                while (mRun) {
                    if(current_read_byte == 0) {
                        current_read_byte = inputStream.read(buff, 0, MAX_READ_BYTES);
                    }
                    total_read_byte += current_read_byte;
                    final int w = 700;
                    final int h = 700;

                    if(current_read_byte > 0) {
                        if(buff[0]==0x04 || isContinueRecieveImage) {
                            int offset = 0;
                            if(buff[0] == 0x04) {
                                offset = 5;
                                sizeImage = ByteBuffer.wrap(buff, 1, 5).getInt();
                                total_read_byte -= 5;
                                current_read_byte -= 5;
                            }

                            if(sizeImage > total_read_byte ) {
                                bytes_read_image = ByteBuffer.wrap(buff, offset, current_read_byte).array();
                                isContinueRecieveImage = true;
                                current_read_byte = 0;
                            } else {
                                bytes_read_image = ByteBuffer.wrap(buff, offset, current_read_byte - (total_read_byte - sizeImage)).array();
                                current_read_byte = total_read_byte - sizeImage;
                                isContinueRecieveImage = false;
                                isRecieveImage = true;
                                Arrays.fill(buff, (byte)0);
                                byte[] buff_remain = Arrays.copyOfRange(buff, current_read_byte , buff.length);
                                System.arraycopy(buff_remain, 0, buff, 0, buff_remain.length);
                            }

                            listArrayImage.addAll( Arrays.asList(ArrayUtils.toObject(bytes_read_image)));
                             if(isRecieveImage) {
                                 recieveImageBytes();
                                 isRecieveImage = false;
                                 total_read_byte = 0;
                             }

                        }
                        else if (buff[0]==(byte)0x03 && buff[1]==(byte)0x7f && buff[2]==(byte)0x80) {
                            isConnect = true;
                            byte [] answerByte = {buff[0], buff[1], buff[2]};
                            inputStream = mSocket.getInputStream();
                            outputStream = mSocket.getOutputStream();
                            current_read_byte -= 3;
                            total_read_byte -= 3;
                            outputStream.write(answerByte);
                        }
                    }
                    else {
                        mRun = false;
                    }
                    if(isSendingImage) {
                        byte[] msgQueryImage = {0x03, 0x00, 0x00, 0x00};
                        outputStream.write(msgQueryImage);
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
        isSendingImage = true;
    }

    public interface OnMessageReceived {
        public void messageReceived(Byte[] message);
    }

    private void recieveImageBytes() {
        mMessageListener.messageReceived(listArrayImage.toArray(new Byte[listArrayImage.size()]));
        total_read_byte = 0;
    }

}
