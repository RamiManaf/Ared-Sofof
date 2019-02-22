/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.window;

import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ExceptionsTrackerWindow extends Window {

    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle");
    
    @FXML
    private Label message;
    @FXML
    private TextArea exception;
    private String text;
    private Throwable tracked;

    public ExceptionsTrackerWindow(String text, Exception tracked) {
        this.text = text;
        this.tracked = tracked;
    }

    @Override
    protected void start() {
        message.setText(text);
        String stack = "";
        do {
            if (!stack.isEmpty()) {
                stack += "Caused by " + tracked.getClass().getSimpleName() + " " + tracked.getMessage() + "\n";
            }
            for (StackTraceElement element : tracked.getStackTrace()) {
                stack = stack + "at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")" + "\n";
            }
        } while ((tracked = tracked.getCause()) != null);
        exception.setText(stack);
        stage.setTitle(defaultBundle.getString("ERROR"));
        stage.setScene(new Scene(root));
        stage.show();
    }

}
