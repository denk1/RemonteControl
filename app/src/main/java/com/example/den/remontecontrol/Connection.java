package com.example.den.remontecontrol;

public interface Connection {
    void initConnection();
    boolean sendCommand(String strCmd);
}
