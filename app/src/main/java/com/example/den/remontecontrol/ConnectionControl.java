package com.example.den.remontecontrol;

import android.graphics.PointF;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;
import android.os.Handler;

import org.json.JSONObject;

public class ConnectionControl {
    private WebSocket ws = null;
    public Handler updateHandler;

    public ConnectionControl() {

        final InfoManager infoManager = InfoManager.createInfoManager();
        Log.i("TAG", "constructor of ConnectionControl");
        // Create a WebSocket factory and set 5000 milliseconds as a timeout
        // value for socket connection.
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        try {
            ws = factory.createSocket("ws://192.168.12.49:6789/");

            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    // Send message to main thread Handler.
                    JSONObject jsonMessage = new JSONObject(message);
                    String messageSpeed = jsonMessage.getString("speed");
                    JSONObject jsonLocation = jsonMessage.getJSONObject("location");
                    String xLocation = jsonLocation.getString("x");
                    String zLocation = jsonLocation.getString("z");
                    PointF pointFLocation = new PointF();
                    pointFLocation.set(Float.valueOf(xLocation), Float.valueOf(zLocation));
                    Log.d("TAG", "{\"speed\": \"" + messageSpeed + "\", \"location\": {" + "\"x\":\"" + xLocation + "\", \"z\":\"" + zLocation + "\"}}");
                    infoManager.handleParam(Float.valueOf(messageSpeed), InfoManager.CURRENT_VELOCITY_VALUE);

                }
            });

            ws.connectAsynchronously();
            boolean bOpen = ws.isOpen();
            Log.d("TAG",  "bOpen=" + String.valueOf(bOpen));
            bOpen = !bOpen;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", "IOException");
        }
    }

    public boolean sendCommand(String strCmd) {
        if (ws.isOpen()) {
            ws.sendText(strCmd);
            return true;
        }
        else
        {
            return false;
        }
    }
}
