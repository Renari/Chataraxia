package com.arimil.chataraxia.server;

import java.io.ObjectOutputStream;

public class ServerData {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    private boolean auth = false;

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }

    void setOutputStream(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private ObjectOutputStream outputStream;

    private int x, y;

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
