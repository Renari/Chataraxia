package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.server.ClientData;
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
        File file = FileSystems.getDefault().getPath("users\\" + name).toFile();
        try {
            ObjectOutputStream outputStream = Server.clients.get(sender).getOutputStream();
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
            ClientData client = Server.clients.get(sender);
            client.setAuth(true);
            client.setName(name);
            outputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
