package com.example.den.remontecontrol;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TabHost;

public class InfoActivity extends AppCompatActivity {

    TabHost tabHost;
    TabHost subTabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        tabHost = findViewById(R.id.tabHost);
        subTabHost = findViewById(R.id.subTabHost);
        tabHost.setup();
        subTabHost.setup();

        //Tab1
        TabHost.TabSpec spec = tabHost.newTabSpec(getResources().getString(R.string.tab_info_one_title));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.tab_info_one_title));
        tabHost.addTab(spec);

        //Tab2
        spec = tabHost.newTabSpec(getResources().getString(R.string.tab_info_two_title));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.tab_info_two_title));
        tabHost.addTab(spec);

        //Tab3
        spec = tabHost.newTabSpec(getResources().getString(R.string.tab_info_three_title));
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.tab_info_three_title));
        tabHost.addTab(spec);

        //Tab4
        spec = tabHost.newTabSpec(getResources().getString(R.string.tab_info_four_title));
        spec.setContent(R.id.tab4);
        spec.setIndicator(getResources().getString(R.string.tab_info_four_title));
        tabHost.addTab(spec);

        //event
        tabHost.setOnTabChangedListener(new AnimationTabListener(this, tabHost));

        //SubTab1
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_steer_title));
        spec.setContent(R.id.subTab1);
        spec.setIndicator(getResources().getString(R.string.subtab_info_steer_title));
        subTabHost.addTab(spec);

        //SubTab2
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_velocity_title));
        spec.setContent(R.id.subTab2);
        spec.setIndicator(getResources().getString(R.string.subtab_info_velocity_title));
        subTabHost.addTab(spec);

        //SubTab3
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_ac_dc_title));
        spec.setContent(R.id.subTab3);
        spec.setIndicator(getResources().getString(R.string.subtab_info_ac_dc_title));
        subTabHost.addTab(spec);

        //SubTab4
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_engine_title));
        spec.setContent(R.id.subTab4);
        spec.setIndicator(getResources().getString(R.string.subtab_info_engine_title));
        subTabHost.addTab(spec);

        //SubTab5
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_pressure_tempture_title));
        spec.setContent(R.id.subTab5);
        spec.setIndicator(getResources().getString(R.string.subtab_info_pressure_tempture_title));
        subTabHost.addTab(spec);

        //SubTab6
        spec = subTabHost.newTabSpec(getResources().getString(R.string.subtab_info_navigation_title));
        spec.setContent(R.id.subTab6);
        spec.setIndicator(getResources().getString(R.string.subtab_info_navigation_title));
        subTabHost.addTab(spec);

        //event
        subTabHost.setOnTabChangedListener(new AnimationTabListener(this, subTabHost));

        // initialization progress bar of the steering control

        Resources res = getResources();
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.progress_bar);
        ProgressBar mProgressFront = findViewById(R.id.progressBarFront);
        ProgressBar mProgressRear = findViewById(R.id.progressBarRear);
        ProgressBar mProgressBattery = findViewById(R.id.progressBarBattery);

        // tempture and pression

        ProgressBar mProgressTemptureEngine = findViewById(R.id.progressBarTemptureEngine);
        ProgressBar mProgressTemptureDC_DC = findViewById(R.id.progressBarTempDC_DC);

        mProgressFront.setProgress(50);   // Main Progress
        mProgressRear.setProgress(50);
        mProgressBattery.setProgress(0);

        mProgressTemptureEngine.setProgress(25);
        mProgressTemptureDC_DC.setProgress(40);
//        mProgress.setSecondaryProgress(50); // Secondary Progress
        mProgressFront.setMax(100); // Maximum Progress
        mProgressFront.setProgressDrawable(drawable);

        mProgressRear.setMax(100);
        mProgressRear.setProgressDrawable(drawable);

        mProgressBattery.setMax(100);
        //mProgressBattery.setProgressDrawable(drawable);

        mProgressTemptureEngine.setProgressDrawable(drawable);
        mProgressTemptureEngine.setMax(100);
        mProgressTemptureDC_DC.setProgressDrawable(drawable);
        mProgressTemptureDC_DC.setMax(100);


    }
}
