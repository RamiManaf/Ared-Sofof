/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.window;

import java.io.IOException;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author LENOVO PC
 */
public abstract class Dialog<V> extends Window {
    
    protected boolean successful;

    public Dialog() {
        super();
        this.successful = false;
    }
    
    public V show(Stage owner) throws IOException{
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        start(stage);
        return getValue();
    }
    
    protected abstract V getValue();
    
    public boolean wasSuccessful(){
        return successful;
    }
    
}
