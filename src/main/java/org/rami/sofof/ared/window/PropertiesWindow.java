/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.window;

import java.io.File;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.rami.sofof.ared.pojo.Connection;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class PropertiesWindow extends Window {

    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle");
    
    @FXML
    private TextField path, port, username;
    @FXML
    private PasswordField password;
    @FXML
    private CheckBox startServer, ssl;

    private Connection connection;

    public PropertiesWindow(Connection connection) {
        super();
        this.connection = connection;
    }

    @Override
    protected void start() {
        path.setText(connection.getPath());
        port.setText(connection.getPort() + "");
        username.setText(connection.getUsername());
        password.setText(connection.getPassword());
        startServer.setSelected(connection.isStartServer());
        ssl.setSelected(connection.isSsl());
        stage.setScene(new Scene(root));
        stage.setTitle(defaultBundle.getString("PROPERTIES"));
        stage.show();
    }

    @FXML
    private void browsePath() {
        File path = new DirectoryChooser().showDialog(stage);
        if (path != null) {
            this.path.setText(path.getAbsolutePath());
        }
    }

    @FXML
    private void create() {
        connection.setPath(path.getText());
        connection.setPort(Integer.parseInt(port.getText()));
        connection.setUsername(username.getText());
        connection.setPassword(password.getText());
        connection.setStartServer(startServer.isSelected());
        connection.setSsl(ssl.isSelected());
        stage.close();
    }

    @FXML
    private void cancel() {
        stage.close();
    }

}
