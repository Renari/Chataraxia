package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.client.Client;
import com.arimil.chataraxia.server.ClientData;
import com.arimil.chataraxia.server.Server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

public class ChatMessage extends Message {

    private String from;
    private String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    @Override
    protected void client(Socket sender) {
        if(from != null) {
            Client.controller.addMessage(message, from);
        } else {
            Client.controller.addMessage(message);
        }
    }

    @Override
    protected void server(Socket sender) {
        ClientData client = Server.clients.get(sender);
        if(client.isAuth()) {
            this.from = client.getName();
            for (Map.Entry<Socket, ClientData> entry : Server.clients.entrySet())
                try {
                    ObjectOutputStream outputStream = entry.getValue().getOutputStream();
                    outputStream.writeObject(this);
                    outputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
            try {
                client.getOutputStream().writeObject(new ChatMessage("Please login with '/login <username> <password>' before sending messages"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
