package com.arimil.chataraxia;

import com.arimil.chataraxia.client.Client;
import com.arimil.chataraxia.server.Server;
import javafx.application.Application;

import java.util.Scanner;

public class Main {

    public static boolean isServerSide = false;

    public static void main(String[] args) {
        if(args.length >= 2 && args[0].equals("--server")) {
            isServerSide = true;
            int port;
            try {
                port = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                System.out.println("Unable to parse " + args[1] + " as integer, the format is --server <port>");
                return;
            }
            System.out.println("Starting server on port: " + args[1]);
            Server server = new Server(port);
            new Thread(server).start();
            Scanner s = new Scanner(System.in);
            while(!server.isStopped()) {
                String input = s.nextLine();
                switch (input) {
                    case "shutdown" :
                        server.stop();
                        break;
                    case "connected" :
                        System.out.println("Connections: " + Server.clients.size());
                        break;
                    default:
                        System.out.println("Unknown command: " + input);
                }
            }
        } else {
            Application.launch(Client.class, args);
        }
    }
}
