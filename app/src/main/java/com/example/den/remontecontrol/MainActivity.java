package com.example.den.remontecontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.neovisionaries.ws.client.HostnameUnverifiedException;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    TextView txResult;
    Spinner spVehicle;
    Menu menu;
    Button btConnect;
    WebSocket ws = null;
    public static  TextView textViewVelocity;
    private static ConnectionControl connectionControl = new ConnectionControl();
    private static CommandControl commandControl = new CommandControl(connectionControl);
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txResult = findViewById(R.id.textView3);
        spVehicle = findViewById(R.id.spinner);
        btConnect = findViewById(R.id.button);
        String[] items = new String[]{"Машина №1", "Машина №2", "Машина №3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spVehicle.setAdapter(adapter);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(spVehicle.getSelectedItemPosition());
            }
        });
        textViewVelocity = findViewById(R.id.velocity_value);
        sharedPreferences = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//    menu item click handle
        if(id == R.id.title1) {
            startSettingsActivity();
        }
        if(id == R.id.title2) {
            Toast.makeText(this, "О программе", Toast.LENGTH_SHORT).show();

        }
        if(id == R.id.title3) {
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickButtonConnect(View view) {
        Toast.makeText(this, "Кнопка подключения", Toast.LENGTH_SHORT).show();
    }

    public static CommandControl getCommandControl() {
        return commandControl;
    }


    private void validate(int ind) {
        if(ind == 0 || ind == 1 || ind == 2) {
            Intent intent = new Intent(MainActivity.this, WorkActivity.class);
            startActivity(intent);
        } else {
            txResult.setText("Отсутствует сетевое соединение с БТС");
        }
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettinsActivity.class);
        startActivity(intent);

    }
}