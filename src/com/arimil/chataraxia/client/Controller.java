package com.arimil.chataraxia.client;

import com.arimil.chataraxia.messages.ChatMessage;
import com.arimil.chataraxia.messages.LoginMessage;
import com.arimil.chataraxia.messages.MoveMessage;
import com.arimil.chataraxia.server.ClientData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.List;

public class Controller {

    public TextField textField;
    public WebView webView;
    public ListView clientList;
    public GridPane miniMapGrid;
    public Label coords;
    private WebEngine engine;
    ClientConnection connection;

    @FXML
    public void initialize() {
        engine = webView.getEngine();
        engine.loadContent("<!DOCTYPE html><html><head><meta charset=utf-8><title>ChatView</title><style>body{background-color:#585c5f;color:#fff}.sender{color:#0ff;font-weight:700}</style></head><body><div id=messageArea></div><script>function isScrollbarVisible(){return document.body.clientHeight>window.innerHeight}function isAtBottomOfPage(){return!isScrollbarVisible()||window.innerHeight+window.scrollY>=document.body.offsetHeight}function addMessage(e,n){var i=document.createElement(\"div\");i.innerHTML=void 0!==n?'<span class=\"sender\">'+n+\":</span> \"+e:e;var o=isAtBottomOfPage();document.getElementById(\"messageArea\").appendChild(i),o&&window.scrollTo(0,document.body.scrollHeight)}</script></body></html>");
    }

    private String stringToHtmlString(String s) {
        StringBuilder sb = new StringBuilder();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    void connect(String ip, int port) {
        if (connection == null || !connection.socket.isConnected()) {
            connection = new ClientConnection(ip, port);
            new Thread(connection).start();
        }
    }

    public void updateMap(List<ClientData> clients, int x, int y) {
        coords.setText(x + ", " + y);
        File file = FileSystems.getDefault().getPath("maps\\map0").toFile();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            String line = br.readLine();
            int maxX = line.length();
            int maxY = (int) br.lines().count() + 1;

            char[][] map = new char[maxX][maxY];
            map[0] = line.toCharArray();
            int index = 0;
            while ((line = br.readLine()) != null && line.length() != 0) {
                index++;
                map[index] = line.toCharArray();
            }
            int startY = getStartPosition(y, maxY);
            int startX = getStartPosition(x, maxX);
            Platform.runLater(() -> {
                miniMapGrid.getChildren().retainAll(miniMapGrid.getChildren().get(0));
                for (int i = 0; i < 11; i++) {
                    for (int j = 0; j < 11; j++) {
                        String locationValue = String.valueOf(map[i + startX][j + startY]); // map data by default
                        for (ClientData client : clients) {
                            if (startX + i == client.getX() - 1 && startY + j == client.getY() - 1) {
                                // someone is at this location set the value to an asterisk
                                locationValue = "*";
                            }
                        }
                        if (startX + i == x - 1 && startY + j == y - 1) {
                            // this person is at this location set the location value to an O
                            locationValue = "O";
                        }
                        Label gridLabel = new Label(locationValue);
                        gridLabel.setPrefWidth(18);
                        gridLabel.setMaxWidth(Double.POSITIVE_INFINITY);
                        gridLabel.setMinWidth(Double.NEGATIVE_INFINITY);
                        gridLabel.setAlignment(Pos.CENTER);
                        miniMapGrid.add(gridLabel, i, j);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getStartPosition(int pos, int maxPos) {
        int start;
        if (pos < 6) {
            start = 0;
        } else if (pos > maxPos - 6) {
            start = maxPos - 11;
        } else {
            start = pos - 6;
        }
        return start;
    }

    public void updateClientData(List<ClientData> clients, int x, int y) {
        Platform.runLater(() -> {
            // update online list
            clientList.getItems().clear();
            for (ClientData client : clients) {
                clientList.getItems().add(client.getName());
            }
            updateMap(clients, x, y);
        });
    }

    public void addMessage(String message) {
        addMessage(message, null);
    }

    public void addMessage(String message, String from) {
        Platform.runLater(() -> {
            if (from != null) {
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
                if (command.length >= 3 && connection != null) {
                    connection.send(new LoginMessage(command[1], command[2]));
                }
                break;
            default:
                addMessage("Unknown command: " + message);
        }
    }

    @FXML
    private void keyListener(KeyEvent event) {
        if (textField.isFocused()) {
            if (event.getCode() == KeyCode.ENTER) {
                if (textField.getText().length() > 0) {
                    if (textField.getText(0, 1).equals("/")) {
                        handleSlashCommand(textField.getText());
                    } else {
                        connection.send(new ChatMessage(textField.getText()));
                    }
                }
                textField.setText("");
                Scene scene = textField.getScene();
                scene.getRoot().requestFocus();
                event.consume();
            }
        } else if ((event.getCode() == KeyCode.UP ||
                event.getCode() == KeyCode.DOWN ||
                event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.RIGHT) &&
                !textField.isFocused()) {
            if (event.getCode() == KeyCode.UP) {
                connection.send(new MoveMessage(MoveMessage.DIRECTION.UP));
            } else if (event.getCode() == KeyCode.RIGHT) {
                connection.send(new MoveMessage(MoveMessage.DIRECTION.RIGHT));
            } else if (event.getCode() == KeyCode.DOWN) {
                connection.send(new MoveMessage(MoveMessage.DIRECTION.DOWN));
            } else if (event.getCode() == KeyCode.LEFT) {
                connection.send(new MoveMessage(MoveMessage.DIRECTION.LEFT));
            }
            event.consume();
        } else {
            if (event.getCode() == KeyCode.ENTER) {
                Scene scene = textField.getScene();
                scene.getRoot().requestFocus();
                event.consume();
            }
        }
    }
}
