package com.arimil.chataraxia.client;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Controller {

    public TextField textField;
    public WebView webView;
    private WebEngine engine;

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
                default:  sb.append(c); break;
            }
        }
        return sb.toString();
    }

    @FXML
    public void initialize() {
        engine = webView.getEngine();
        engine.loadContent("<!DOCTYPE html><html><head><meta charset=utf-8><title>ChatView</title><style>body{background-color:#585c5f;color:#fff}.sender{color:#0ff;font-weight:700}</style></head><body><div id=messageArea></div><script>function isScrollbarVisible(){return document.body.clientHeight>window.innerHeight}function isAtBottomOfPage(){return!isScrollbarVisible()||window.innerHeight+window.scrollY>=document.body.offsetHeight}function addMessage(e,n){var i=document.createElement(\"div\");i.innerHTML=void 0!==n?'<span class=\"sender\">'+n+\":</span> \"+e:e;var o=isAtBottomOfPage();document.getElementById(\"messageArea\").appendChild(i),o&&window.scrollTo(0,document.body.scrollHeight)}</script></body></html>");
    }

    public void addMessage(String message) {
        addMessage(message, null);
    }

    public void addMessage(String message, String from) {
        WebEngine engine = webView.getEngine();

        if(from != null) {
            engine.executeScript("addMessage('" + stringToHtmlString(message) + "', '" + stringToHtmlString(from) + "')");
        } else {
            engine.executeScript("addMessage('" + stringToHtmlString(message) + "')");
        }
    }

    @FXML
    private void keyListener(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            if (textField.isFocused()) {
                addMessage(textField.getText(), "test");
                addMessage("this is a system message");
                textField.setText(""); // clear text after message is sent

                // remove focus from text field
                Scene scene = textField.getScene();
                scene.getRoot().requestFocus();
            } else {
                // if enter is pressed focus the text field
                textField.requestFocus();
            }
            event.consume();
        }
    }
}
