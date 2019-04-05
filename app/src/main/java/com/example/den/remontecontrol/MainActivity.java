package com.example.den.remontecontrol;

import android.content.Intent;
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

import com.neovisionaries.ws.client.HostnameUnverifiedException;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView txResult;
    Spinner spVehicle;
    Menu menu;
    Button btConnect;
    WebSocket ws = null;
    public static  TextView textViewVelocity;
    private static ConnectionControl connectionControl = new ConnectionControl();
    private static CommandControl commandControl = new CommandControl(connectionControl);

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
            Toast.makeText(this, "Настройки", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.title2) {
            Toast.makeText(this, "Выход", Toast.LENGTH_SHORT).show();

        }
        if(id == R.id.title3) {
            Toast.makeText(this, "Управление", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.title4) {
            Toast.makeText(this, "Информация", Toast.LENGTH_SHORT).show();
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
        if(ind == 1) {
            Intent intent = new Intent(MainActivity.this, WorkActivity.class);
            startActivity(intent);
        } else {
            txResult.setText("Отсутствует сетевое соединение с БТС");
        }
    }
}
