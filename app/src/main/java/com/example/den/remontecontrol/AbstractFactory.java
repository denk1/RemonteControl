package com.example.den.remontecontrol;

public interface AbstractFactory<T> {
    T create(String connectionType);
}

