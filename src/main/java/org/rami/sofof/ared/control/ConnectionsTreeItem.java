/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.control;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.rami.sofof.ared.assets.Session;
import org.rami.sofof.ared.pojo.Connection;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ConnectionsTreeItem extends TreeItem<Connection> {

    private static final String CONNECTED_ICON = "/org/rami/sofof/ared/icon/database-connected.png";
    private static final String DISCONNECTED_ICON = "/org/rami/sofof/ared/icon/database-disconnected.png";
    private static final String ACTIVE_ICON = "/org/rami/sofof/ared/icon/database-execute.png";

    public ConnectionsTreeItem(Connection connection) {
        super(connection);
        triggerImageListener();
        chooseImage();
    }

    private void triggerImageListener() {
        Session.activeConnectionProperty().addListener((observable, oldValue, newValue) -> {
            if (getValue().equals(newValue)) {
                setGraphic(imageCreator(ACTIVE_ICON, 20, 20));
            }
        });
        getValue().connectProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                setGraphic(imageCreator(CONNECTED_ICON, 20, 20));
            } else {
                setGraphic(imageCreator(DISCONNECTED_ICON, 20, 20));
            }
        });
    }

    private void chooseImage() {
        if (getValue().equals(Session.activeConnectionProperty().get())) {
            setGraphic(imageCreator(ACTIVE_ICON, 20, 20));
        } else if (getValue().connectProperty().get()) {
            setGraphic(imageCreator(CONNECTED_ICON, 20, 20));
        } else {
            setGraphic(imageCreator(DISCONNECTED_ICON, 20, 20));
        }
    }
    
    private static ImageView imageCreator(String url, int height, int width){
        ImageView img = new ImageView(url);
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }

}
