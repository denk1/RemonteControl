package com.example.den.remontecontrol;

public class CommandControl {
    private Connection connectionControl;

    public CommandControl(Connection cntCtr) {
        connectionControl = cntCtr;
    }

    public boolean racingDown(int coeffForce) {
        connectionControl.sendCommand("{\"action\":\"racing_down\", \"params\":{\"throttle_proc\":" + coeffForce + "}}");
        return true;
    }

    public boolean racingUp() {
        connectionControl.sendCommand("{\"action\":\"racing_up\", \"params\":{}}");
        return true;
    }

    public boolean stoppingDown(int coeffForce) {
        connectionControl.sendCommand("{\"action\":\"stopping_down\", \"params\":{\"throttle_proc\":" + coeffForce + "}}");
        return true;
    }

    public boolean stoppingUp() {
        connectionControl.sendCommand("{\"action\":\"stopping_up\", \"params\":{}}");
        return true;
    }

    public boolean turnLeftDown(int angle) {
        connectionControl.sendCommand("{\"action\":\"turn_left_down\", \"params\":{\"steering_angle\":" + angle * 10 + "}}");
        return true;
    }

    public boolean turnLeftUp() {
        connectionControl.sendCommand("{\"action\":\"turn_left_up\"}");
        return true;
    }

    public boolean turnRightDown(int angle) {
        connectionControl.sendCommand("{\"action\":\"turn_right_down\", \"params\":{\"steering_angle\":" + angle * 10 + "}}");
        return true;
    }

    public boolean turnRightUp() {
        connectionControl.sendCommand("{\"action\":\"turn_right_up\", \"params\":{}}");
        return true;
    }

    public boolean handBreakDown() {
        connectionControl.sendCommand("{\"action\":\"turn_right_down\", \"params\":[]}");
        return true;
    }

    public void setActiveConn() {
        connectionControl.setActivated();
    }

    public void setDeactiveConn() {
        connectionControl.setDisactivated();
    }
}
