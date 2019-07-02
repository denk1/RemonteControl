package com.example.den.remontecontrol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class WorkActivity extends AppCompatActivity {
    public static String IP = null;
    public static int PORT = 0;
    private final String TAG = "WorkActivity";
    Intent intentMapsActivity = null;
    private SharedPreferences sharedPreferences;
    TextView textViewConnectivly = null;
    MonitoringStateTask monitoringStateTask = new MonitoringStateTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        initViews();
        sharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_TEXT, "NULL");
        if(ip != null && ip != "" && ip != "NULL")
            IP = ip;
        String port = sharedPreferences.getString(SettinsActivity.PORT_TEXT, "NULL");
        if(port != null && port != "" && ip != "NULL")
            PORT = Integer.valueOf(port);
        if(!isConnectedWiFi()) {
            textViewConnectivly.setBackgroundColor(getColor(R.color.colorAlarm));
            textViewConnectivly.setText("соединение с БТС отсутствует");
        }
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

    private void initViews() {
        textViewConnectivly = findViewById(R.id.textViewConnection);
    }

    class MonitoringStateTask extends AsyncTask<Void, Boolean,  Void> {
        private Thread thread = null;
        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!isCancelled()) {
                    if(isConnectedWiFi()) {
                        Boolean[] booleanStates = {true};
                        publishProgress(booleanStates);
                    } else {
                        Boolean[] booleanStates = {false};
                        publishProgress(booleanStates);
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
        }
    }
}



