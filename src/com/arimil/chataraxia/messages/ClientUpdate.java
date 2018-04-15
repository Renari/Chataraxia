package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.client.Client;
import com.arimil.chataraxia.server.ClientData;

import java.net.Socket;
import java.util.List;

public class ClientUpdate extends Message {

    private List<ClientData> clientData;
    private int x;
    private int y;

    public ClientUpdate(List<ClientData> names, int x, int y) {
        clientData = names;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void client(Socket sender) {
        Client.controller.updateClientData(clientData, x, y);
    }

    @Override
    protected void server(Socket sender) {

    }
}
