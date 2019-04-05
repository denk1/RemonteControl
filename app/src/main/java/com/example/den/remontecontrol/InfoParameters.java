package com.example.den.remontecontrol;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class InfoParameters {

    private static InfoParameters instance = null;
    private Map infoMap = null;
    private Map infoViewMap;
    private InfoParameters() {
        infoMap = new HashMap<String, String>();
        infoViewMap = new HashMap<String, Object>();
    }

    public static InfoParameters createInfoParameters() {
        if(instance == null) {
            instance = new InfoParameters();
        }
        return instance;
    }

    public void setParam(String key, String value) {
        infoMap.put(key, value);
    }

    public void setViewOfParam(String key, Object view) {
        infoViewMap.put(key, view);
    }

    public String getParamView(String key) {
        return (String)infoMap.get(key);
    }

    public Object getViewOfParam(String key) {
        return (Object) infoViewMap.get(key);
    }

}
