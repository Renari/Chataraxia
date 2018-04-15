package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.server.Server;
import com.arimil.chataraxia.server.ServerData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class MoveMessage extends Message {
    public enum DIRECTION {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    private DIRECTION direction;
    private int x, y;

    public MoveMessage(DIRECTION direction) {
        this.direction = direction;
    }

    public MoveMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    protected void client(Socket sender) {}

    @Override
    protected void server(Socket sender) {
        ServerData serverData = Server.clients.get(sender);
        if(!serverData.isAuth()) {
            return;
        }
        ObjectOutputStream outputStream = serverData.getOutputStream();
        File file = FileSystems.getDefault().getPath("maps\\map0").toFile();
        int mapXSize;
        int mapYSize;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            mapXSize = br.readLine().length();
            mapYSize = (int) br.lines().count() + 1;
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        int newX = serverData.getX();
        int newY = serverData.getY();
        switch (direction) {
            case UP:
                if(serverData.getY() > 1) {
                    newY -= 1;
                    serverData.setPosition(newX, newY);
                }
                break;
            case RIGHT:
                if(serverData.getX() < mapXSize) {
                    newX += 1;
                    serverData.setPosition(newX, newY);
                }
                break;
            case DOWN:
                if(serverData.getY() < mapYSize) {
                    newY += 1;
                    serverData.setPosition(newX, newY);
                }
                break;
            case LEFT:
                if(serverData.getX() > 1) {
                    newX -= 1;
                    serverData.setPosition(newX, newY);
                }
                break;
        }
        Server.updateClientData();
    }
}
