package com.arimil.chataraxia.server;

import com.arimil.chataraxia.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class WorkerRunnable implements Runnable{

    private Socket clientSocket;

    WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            ObjectInputStream input  = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            Server.clients.get(clientSocket).setOutputStream(output);
            while(clientSocket.isConnected()) {
                Object obj = input.readObject();
                Message msg = (Message) obj;
                msg.process(clientSocket);
            }
            output.close();
            input.close();
        } catch (SocketException e) {
            Server.clients.remove(clientSocket);
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}