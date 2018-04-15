package com.arimil.chataraxia.messages;

import com.arimil.chataraxia.Message;
import com.arimil.chataraxia.client.Client;

import java.net.Socket;
import java.util.List;

public class ClientList extends Message {

    private List<String> usernames;

    public ClientList(List<String> names) {
        usernames = names;
    }

    @Override
    protected void client(Socket sender) {
        Client.controller.updateUserList(usernames);
    }

    @Override
    protected void server(Socket sender) {

    }
}
