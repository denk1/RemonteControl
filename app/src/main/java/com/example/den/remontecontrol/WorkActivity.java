package com.example.den.remontecontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class WorkActivity extends AppCompatActivity {
    Intent intentMapsActivity = null;
    private SharedPreferences sharedPreferences;
    public static String IP = null;
    public static int PORT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        sharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String ip = sharedPreferences.getString(SettinsActivity.IP_ADDRESS_TEXT, "NULL");
        if(ip != null)
            IP = ip;
        String port = sharedPreferences.getString(SettinsActivity.PORT_TEXT, "NULL");
        PORT = Integer.valueOf(port);
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

}
