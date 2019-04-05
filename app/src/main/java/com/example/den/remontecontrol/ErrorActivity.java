package com.example.den.remontecontrol;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        int a = 3;
        a += 3;
        //try {
        //    Thread.sleep(1000);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
//        TableLayout tableLayout = findViewById(R.id.tableLayout);
//        tableLayout.setColumnStretchable(0, true);
//        tableLayout.setColumnStretchable(1, true);
//        TableRow tr1 = new TableRow(this);
//        TextView cell5 = new TextView(this);
//        TextView cell6 = new TextView(this);
//        cell5.setGravity(Gravity.LEFT);
//        cell6.setGravity(Gravity.RIGHT);
//        cell5.setPadding(10,10,10,10);
//        cell6.setPadding(10,10,10,10);
//        cell5.setText("cell 5");
//        cell6.setText("cell 6");
//        tr1.addView(cell5);
//        tr1.addView(cell6);
//        tableLayout.addView(tr1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.menu_diag, menu);
        return true;
    }

    public static class AnimationTabListener {
    }
}
