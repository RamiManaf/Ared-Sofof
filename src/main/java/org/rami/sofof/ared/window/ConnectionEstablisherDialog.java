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
public class ConnectionEstablisherDialog extends Dialog<Connection> {
    
    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle");
    
    @FXML
    private TextField path, port, username;
    @FXML
    private PasswordField password;
    @FXML
    private CheckBox startServer, ssl;

    public ConnectionEstablisherDialog() {
        super();
    }
    
    @Override
    protected void start() {
        startServer.selectedProperty().addListener((observable, oldValue, newValue) -> {
            username.setDisable(newValue);
            password.setDisable(newValue);
        });
        stage.setTitle(defaultBundle.getString("CONNECTION_ESTABLISHER"));
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @Override
    protected Connection getValue() {
        if(successful){
            if(startServer.isSelected()){
                return new Connection(path.getText(), Integer.parseInt(port.getText()), ssl.isSelected());
            }else{
                return new Connection(path.getText(), Integer.parseInt(port.getText()), username.getText(), password.getText(), ssl.isSelected());
            }
        }else return null;
    }
    
    @FXML
    private void browsePath(){
        File path = new DirectoryChooser().showDialog(stage);
        if(path != null){
            this.path.setText(path.getAbsolutePath());
        }
    }
    
    @FXML
    private void create(){
        successful = true;
        stage.close();
    }
    
    @FXML
    private void cancel(){
        stage.close();
    }

}
