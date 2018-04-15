package com.arimil.chataraxia.server;

import com.arimil.chataraxia.messages.ClientUpdate;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Server implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    public static ConcurrentMap<Socket, ServerData> clients = new ConcurrentHashMap<>();

    public Server(int port) {
        this.serverPort = port;
    }

    public static void updateClientData() {
        List<ClientData> clientData = new ArrayList<>();
        Collection<ServerData> clients = Server.clients.values();
        for (ServerData client : clients) {
            if(client.isAuth()) {
                clientData.add(new ClientData(client.getName(), client.getX(), client.getY()));
            }
        }
        for (ServerData client : clients) {
            if(client.isAuth()) {
                ObjectOutputStream outputStream = client.getOutputStream();
                try {
                    outputStream.writeObject(new ClientUpdate(clientData, client.getX(), client.getY()));
                    outputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void run() {
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("Received connection from: " + clientSocket.getInetAddress());
                clients.put(clientSocket, new ServerData());
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException("Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket)).start();
        }
        System.out.println("Server Stopped.");
    }


    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
