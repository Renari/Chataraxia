package com.arimil.chataraxia.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Client extends Application {

    public static Controller contoller;
    public static Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setTitle("Chataraxia");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        List<String> args = getParameters().getRaw();
        if(args.size() >= 2) {
            try {
                String ip = args.get(0);
                int port = Integer.parseInt(args.get(1));
                ((Controller) loader.getController()).connect(ip, port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port passed for automatic connection.");
                System.out.println("Unable to connect to server with specified arguments.");
            }
        }
    }

}
