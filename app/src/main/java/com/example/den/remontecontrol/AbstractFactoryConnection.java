package com.example.den.remontecontrol;

public class AbstractFactoryConnection implements AbstractFactory<Connection> {
    @Override
    public Connection create(String connectionType) {

        if("web-socket".equalsIgnoreCase(connectionType)) {
            return new ConnectionControl();
        }
        else {
            return null;
        }
    }
}


