package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.server.Server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        File file = FileSystems.getDefault().getPath("users\\"+name).toFile();
        try {
            ObjectOutputStream outputStream = Server.clients.get(sender).getOutputStream();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
            String passwordHash = new String(hash, StandardCharsets.UTF_8);
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                if(br.readLine().equals(passwordHash)) {
                    Server.clients.get(sender).setAuth(true);
                } else {
                    outputStream.writeObject(new ChatMessage("Invalid username or password"));
                }
            } else {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(passwordHash);
                Server.clients.get(sender).setAuth(true);
                outputStream.writeObject(new ChatMessage("Logged in successfully"));
            }
            outputStream.reset();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
