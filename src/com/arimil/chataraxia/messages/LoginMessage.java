package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.server.ServerData;
import com.arimil.chataraxia.server.Server;
import com.arimil.chataraxia.server.Sha256;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;

public class LoginMessage extends Message {

    String name;
    String pass;

    public LoginMessage(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    @Override
    protected void client(Socket sender) {

    }

    @Override
    protected void server(Socket sender) {
        ServerData serverData = Server.clients.get(sender);
        File file = FileSystems.getDefault().getPath("users/" + name).toFile();
        File map = FileSystems.getDefault().getPath("maps/map0").toFile();
        try {
            ObjectOutputStream outputStream = serverData.getOutputStream();
            if (file.exists()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                if (br.readLine().equals(Sha256.getPasswordHash(pass))) {
                    outputStream.writeObject(new ChatMessage("Logged in successfully"));
                    br.close();
                } else {
                    outputStream.writeObject(new ChatMessage("Invalid username or password"));
                    br.close();
                    return;
                }
            } else {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
                bw.write(Sha256.getPasswordHash(pass));
                outputStream.writeObject(new ChatMessage("Account created"));
                bw.close();
            }
            outputStream.reset();
            BufferedReader mr = new BufferedReader(new InputStreamReader(new FileInputStream(map), StandardCharsets.UTF_8));
            int maxX = mr.readLine().length();
            int maxY = (int) mr.lines().count() + 1;
            int x = (int)(Math.random() * maxX) + 1;
            int y = (int)(Math.random() * maxY) + 1;
            serverData.setPosition(x, y);
            outputStream.writeObject(new MoveMessage(x, y));
            outputStream.reset();
            System.out.println("setting position: " + x + ", " + y);
            serverData.setAuth(true);
            serverData.setName(name);
            Server.updateClientData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
