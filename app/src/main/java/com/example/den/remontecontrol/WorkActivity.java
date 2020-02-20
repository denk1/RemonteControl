package com.example.den.remontecontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class WorkActivity extends AppCompatActivity {
    public static String IP = null;
    public static int PORT = 0;
    public static String IP_VIDEO = null;
    public static int PORT_VIDEO = 0;
    private final String TAG = "WorkActivity";
    Intent intentMapsActivity = null;
    private SharedPreferences sharedPreferences;
    TextView textViewConnectivly = null;
    TextView txtViewControlConn = null;
    MonitoringStateTask monitoringStateTask = new MonitoringStateTask();
    private static AbstractFactory<Connection> abstractFactory = new AbstractFactoryConnection();
    private static Connection connectionControl = null;
    private static volatile CommandControl commandControl = null;
    private JoystickCommunication joystickCommunication = null;

    private String TYPE_CONNECTION = null;
    public static String IP_CONTROL = null;
    public static int PORT_CONTROL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        initViews();
        sharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        IP = getStrParam(SettinsActivity.IP_ADDRESS_TEXT);
        if(IP != "NULL")
            PORT = getPORT(SettinsActivity.PORT_TEXT);

        // connection of the getting video
        IP_VIDEO = getStrParam(SettinsActivity.IP_ADDRESS_VIDEO_TEXT);
        if(IP_VIDEO != "NULL")
            PORT_VIDEO = getPORT(SettinsActivity.PORT_VIDEO_TEXT);

        // connection of the control
        TYPE_CONNECTION = getStrParam(SettinsActivity.TYPE_OF_CONNECTION);
        IP_CONTROL = getStrParam(SettinsActivity.IP_ADDRESS_CONTROL);
        if(IP_CONTROL != "NULL")
            PORT_CONTROL = getPORT(SettinsActivity.PORT_CONTROL);
        initConnection(TYPE_CONNECTION);
        if(!isConnectedWiFi()) {
            textViewConnectivly.setBackgroundColor(getColor(R.color.colorAlarm));
            textViewConnectivly.setText("соединение с БТС отсутствует");
        }
        joystickCommunication = JoystickCommunication.getInstance(this);
        monitoringStateTask.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        //getMenuInflater().inflate(R.menu.menu, menu);
        getMenuInflater().inflate(R.menu.menu_work, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.title1) {
        //    itemRelease(message);
        //    startGlobalMapActivity();
            startGoogleMapsActivity();
        }
        if(id==R.id.title2) {
            startMapActivity();
        }
        if(id==R.id.title3) {

            startControlActivity();
        }
        if(id==R.id.title4) {
            //String message = "Item4 Cliked";
            //   Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            //    itemRelease(message);
            startInfoActivity();
        }
        if(id==R.id.title5) {
        //    String message = "Item5 Cliked";
        //    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            startErrorActivity();
        }

//        menu item clip handling
        return super.onOptionsItemSelected(item);
    }

    private void startErrorActivity() {
        Intent intent = new Intent(this, ErrorActivity.class);
        startActivity(intent);
    }

    private void startControlActivity() {
        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
    }

    private void startInfoActivity() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void startMapActivity() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private void startGlobalMapActivity() {
        Intent intent = new Intent(this, GlobalMapActivity.class);
        startActivity(intent);
    }

    private void startGoogleMapsActivity() {
        if(intentMapsActivity == null) {
            intentMapsActivity = new Intent(this, CarMapActivity.class);
        }
        startActivity(intentMapsActivity);
    }

    private boolean isConnectedWiFi() {
        ConnectivityManager connectivityManager =  (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getActiveNetworkInfo();
        if(wifi != null) {
            if(wifi.getType()==ConnectivityManager.TYPE_WIFI) {
                Log.i(TAG, "connecting to wifi-network");
                return true;
            }
        } else {
            Log.i(TAG, "not connected to the network");
            //Toast.makeText(this, "нет соединения с сетью", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean isConnectedControlServer() {
        return connectionControl.isConnected();
    }

    // ///////////////////////////////
    // getting IP address
    // ///////////////////////////////

    private String getStrParam(String ip_addr) {
        String IP = "NULL";
        String ip = sharedPreferences.getString(ip_addr, "NULL");
        if(ip != null && ip != "" && ip != "NULL")
            IP = ip;
        return IP;
    }

    // ///////////////////////////////////
    // getting port
    // ///////////////////////////////////

    private int getPORT(String portValue) {
        int PORT = 0;
        String port = sharedPreferences.getString(portValue, "NULL");
        if(port != null && port != "")
            PORT = Integer.valueOf(port);
        return PORT;
    }

    // ////////////////////////////////////
    // the initialization of the connection
    // ////////////////////////////////////

    private boolean initConnection(String type_conn) {

        connectionControl = abstractFactory.create(type_conn);

        if(connectionControl != null) {
            commandControl = new CommandControl(connectionControl);
            return true;
        }
        else {
            return false;
        }
    }

    public static CommandControl getCommandControl() {
        return commandControl;
    }

    private void initViews() {
        textViewConnectivly = findViewById(R.id.textViewConnection);
        txtViewControlConn = findViewById(R.id.txtViewControl);
    }

    class MonitoringStateTask extends AsyncTask<Void, Boolean,  Void> {
        private Thread thread = null;
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {

                while (!isCancelled()) {
                    Boolean[] booleanStates = {false, false};
                    if(isConnectedWiFi()) {
                        booleanStates[0] = true;

                    }

                    if(isConnectedControlServer()) {
                        booleanStates[1] = true;
                    }

                    publishProgress(booleanStates);

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }


                }
            }
        };

        @Override
        protected Void doInBackground(Void... v) {
            this.thread = new Thread(runnable);
            this.thread.start();
            return null;
        }

        @Override
        protected void  onProgressUpdate(Boolean... states) {
            if(!states[0]) {
                textViewConnectivly.setBackgroundColor(getColor(R.color.colorAlarm));
                textViewConnectivly.setText("соединение с БТС отсутствует");
            } else {
                textViewConnectivly.setBackgroundColor(getColor(R.color.colorOk));
                textViewConnectivly.setText("соединение с БТС установлено");
            }

            if(!states[1]) {
                txtViewControlConn.setBackgroundColor(getColor(R.color.colorAlarm));
                txtViewControlConn.setText("соединение с сервером управлегния отсутствует");
            } else {
                txtViewControlConn.setBackgroundColor(getColor(R.color.colorOk));
                txtViewControlConn.setText("соединение с сервером управления установлено");
            }
        }
    }
}



