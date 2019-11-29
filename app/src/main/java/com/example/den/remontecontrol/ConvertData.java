package com.example.den.remontecontrol;

import java.nio.ByteBuffer;

public class ConvertData {
    public static double toDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }
}
