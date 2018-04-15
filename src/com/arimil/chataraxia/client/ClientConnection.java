package com.arimil.chataraxia.client;

import com.arimil.chataraxia.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientConnection implements Runnable {

    private String host;
    private int port;
    Socket socket = new Socket();
    private ObjectOutputStream outputStream;

    ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void send(Object obj) {
        try {
            if(outputStream != null) {
                outputStream.writeObject(obj);
                outputStream.reset();
            } else {
                Client.controller.addMessage("Connect before trying to send messages '/connect <host> <port>'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(host, port));
            Client.controller.addMessage("Connected to " + host + " on " + port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            while (socket.isConnected()) {
                Object obj = inputStream.readObject();
                Message msg = (Message)obj;
                msg.process(socket);
            }
        } catch (IOException e) {
            Client.controller.addMessage("Unable to connect to " + host + " on " + port);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
