package com.arimil.chataraxia;

import com.arimil.chataraxia.client.Client;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("--server")) {
            //start server on args[1] port
        } else {
            Application.launch(Client.class, args);
        }
    }
}
