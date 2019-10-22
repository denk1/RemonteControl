package com.example.den.remontecontrol;

public class AbstractFactoryConnection implements AbstractFactory<Connection> {
    @Override
    public Connection create(String connectionType) {

        if("ConnectionControl".equalsIgnoreCase(connectionType)) {
            return new ConnectionControl();
        }
        else {
            return null;
        }
    }
}


