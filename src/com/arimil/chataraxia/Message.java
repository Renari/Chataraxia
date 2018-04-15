package com.arimil.chataraxia;

import java.io.Serializable;
import java.net.Socket;

public abstract class Message implements Serializable {
    public void process(Socket sender) {
        if(Main.isServerSide) {
            server(sender);
        } else {
            client(sender);
        }
    }
    protected abstract void client(Socket sender);
    protected abstract void server(Socket sender);
}
