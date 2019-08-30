package com.example.den.remontecontrol;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CameraMonitoringActivity extends AppCompatActivity {
    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btnPlayPause;
    ClientCamera clientCamera = null;
    ImageView imageView = null;
    static HashMap<String, ImageView> screenItems = new HashMap<String, ImageView>();

    //String videoURL = "http://10.91.1.33:8000/android_tutorial.mp4";
    //String videoURL = "rtsp://10.91.1.33:7654/mystream.sdp";
    //String videoURL = "http://10.91.1.33:8090/detection.webm";
    String videoURL = "http://10.91.1.33:8090/detection.webm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_monitoring);
        FrameManager.getInstance().setCameraFrame("camera1", new CameraFrame((ImageView)findViewById(R.id.imageView)));

        imageView = (ImageView)findViewById(R.id.imageView);
        videoView = (VideoView)findViewById(R.id.videoView);
        btnPlayPause = (ImageButton)findViewById(R.id.btn_play_pause);
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new ProgressDialog(CameraMonitoringActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                try {
                    if(!videoView.isPlaying()) {
                        Uri uri = Uri.parse(videoURL);
                        videoView.setVideoURI(uri);
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                btnPlayPause.setImageResource(R.drawable.ic_pause);
                            }
                        });
                    } else {
                        mDialog.dismiss();
                        videoView.pause();
                        btnPlayPause.setImageResource(R.drawable.ic_play);
                    }
                } catch (Exception e) {

                }
                videoView.requestFocus();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mDialog.dismiss();
                        mp.setLooping(true);
                        videoView.start();
                        btnPlayPause.setImageResource(R.drawable.ic_pause);
                    }
                });
            }
        });

    }

    public static ImageView getScreen(String nameCamera) {
        if(screenItems.size() > 0)
            return screenItems.get(nameCamera);
        else
            return null;
    }

    public class FrameTask  extends AsyncTask<String, byte[], Boolean> {

        @Override
        protected Boolean doInBackground(String... message) {
            clientCamera = new ClientCamera(new ClientCamera.OnMessageReceived() {
                @Override
                public void messageReceived(byte[] message) {
                    publishProgress(message);
                }
            });
            return true;
        }

        @Override
        protected void onProgressUpdate(byte[]... values) {
            Bitmap bmp = BitmapFactory.decodeByteArray(values[0], 0, values.length);

            imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(),
                    imageView.getHeight(), false));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean value) {
            super.onPostExecute(value);
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
            this.cancel(true);
        }
    }
}
