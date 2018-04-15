package com.arimil.chataraxia.client;

import com.arimil.chataraxia.messages.ChatMessage;
import com.arimil.chataraxia.messages.LoginMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;

public class Controller {

    public TextField textField;
    public WebView webView;
    public ListView clientList;
    private WebEngine engine;
    ClientConnection connection;

    @FXML
    public void initialize() {
        engine = webView.getEngine();
        engine.loadContent("<!DOCTYPE html><html><head><meta charset=utf-8><title>ChatView</title><style>body{background-color:#585c5f;color:#fff}.sender{color:#0ff;font-weight:700}</style></head><body><div id=messageArea></div><script>function isScrollbarVisible(){return document.body.clientHeight>window.innerHeight}function isAtBottomOfPage(){return!isScrollbarVisible()||window.innerHeight+window.scrollY>=document.body.offsetHeight}function addMessage(e,n){var i=document.createElement(\"div\");i.innerHTML=void 0!==n?'<span class=\"sender\">'+n+\":</span> \"+e:e;var o=isAtBottomOfPage();document.getElementById(\"messageArea\").appendChild(i),o&&window.scrollTo(0,document.body.scrollHeight)}</script></body></html>");
    }

    private String stringToHtmlString(String s){
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("\\'"); break;
                default:  sb.append(c); break;
            }
        }
        return sb.toString();
    }

    void connect(String ip, int port) {
        if(connection == null || !connection.socket.isConnected()) {
            connection = new ClientConnection(ip, port);
            new Thread(connection).start();
        }
    }

    public void updateUserList(List<String> users) {
        Platform.runLater(() -> {
           clientList.getItems().clear();
            for (String name : users) {
                clientList.getItems().add(name);
            }
        });
    }

    public void addMessage(String message) {
        addMessage(message, null);
    }

    public void addMessage(String message, String from) {
        Platform.runLater(() -> {
            if(from != null) {
                engine.executeScript("addMessage('" + stringToHtmlString(message) + "', '" + stringToHtmlString(from) + "')");
            } else {
                engine.executeScript("addMessage('" + stringToHtmlString(message) + "')");
            }
        });
    }

    private void handleSlashCommand(String message) {
        String[] command = message.split(" ");
        switch (command[0]) {
            case "/connect":
                if (command.length >= 3) {
                    try {
                        int port = Integer.parseInt(command[2]);
                        connect(command[1], port);
                    } catch (NumberFormatException e) {
                        addMessage("Invalid port specified");
                    }
                } else {
                    addMessage("/connect <host> <port>");
                }
                break;
            case "/login":
                if (command.length >= 3) {
                    connection.send(new LoginMessage(command[1], command[2]));
                }
                break;
            default:
                addMessage("Unknown command: " + message);
        }
    }

    @FXML
    private void keyListener(KeyEvent event) {
        if(textField.isFocused()) {
            if (event.getCode() == KeyCode.ENTER) {
                if(textField.getText().length() == 0) {
                    Scene scene = textField.getScene();
                    scene.getRoot().requestFocus();
                } else if(textField.getText(0, 1).equals("/")) {
                    handleSlashCommand(textField.getText());
                } else {
                    connection.send(new ChatMessage(textField.getText()));
                }
                event.consume();
            }
        } else if((event.getCode() == KeyCode.UP ||
                event.getCode() == KeyCode.DOWN ||
                event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.RIGHT) &&
                !textField.isFocused()) {
            // handle movement packet
            System.out.println("got arrowkey event");
            event.consume();
        } else {
            if (event.getCode() == KeyCode.ENTER) {
                textField.requestFocus();
                event.consume();
            }
        }
    }
}
