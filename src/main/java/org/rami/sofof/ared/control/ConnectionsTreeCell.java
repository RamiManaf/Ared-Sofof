/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.control;

import com.sefr.sofof.SofofException;
import java.util.ResourceBundle;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.stage.Stage;
import org.rami.sofof.ared.assets.ExceptionsTracker;
import org.rami.sofof.ared.assets.Session;
import org.rami.sofof.ared.pojo.Connection;
import org.rami.sofof.ared.window.PropertiesWindow;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ConnectionsTreeCell extends TreeCell<Connection> {

    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle");
    private final TextField textfield;
    private ContextMenu menu;

    public ConnectionsTreeCell() {
        super();
        textfield = new TextField();
        textfield.setOnAction((evt) -> {
            getItem().nameProperty().set(textfield.getText());
            cancelEdit();
        });
        createMenu();
        setFocusTraversable(false);
    }

    private void createMenu() {
        MenuItem[] items = new MenuItem[5];
        items[0] = new MenuItem(defaultBundle.getString("CONNECT"));
        items[0].setOnAction((evt) -> {
            try {
                getItem().connect();
                MenuItem disconnect = new MenuItem(defaultBundle.getString("DISCONNECT"));
                disconnect.setOnAction((evt1) -> {
                    try {
                        getItem().disconnect();
                        if (getItem().equals(Session.activeConnectionProperty().get())) {
                            Session.activeConnectionProperty().set(null);
                        }
                        menu.getItems().set(0, items[0]);
                    } catch (SofofException ex) {
                        ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
                    }
                });
                menu.getItems().set(0, disconnect);
            } catch (SofofException ex) {
                try {
                    getItem().disconnect();
                } catch (SofofException ex1) {
                    System.err.println(ex);
                    ex.printStackTrace();
                }
                ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
            }
        });
        items[1] = new MenuItem(defaultBundle.getString("EXECUTE_ON"));
        items[1].setOnAction((evt) -> {
            try {
                if (!getItem().connectProperty().get()) {
                    getItem().connect();
                }
                Session.activeConnectionProperty().set(getItem());
            } catch (SofofException ex) {
                ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
            }
        });
        items[2] = new MenuItem(defaultBundle.getString("DELETE"));
        items[2].setOnAction((evt) -> {
            if (getItem().equals(Session.activeConnectionProperty().get())) {
                Session.activeConnectionProperty().set(null);
            }
            if (getItem().connectProperty().get()) {
                try {
                    getItem().disconnect();
                } catch (SofofException ex) {
                    ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
                }
            }
            getTreeView().getRoot().getChildren().remove(getTreeItem());
        });
        items[3] = new MenuItem(defaultBundle.getString("RENAME"));
        items[3].setOnAction((evt) -> {
            getTreeView().edit(getTreeItem());
        });
        items[4] = new MenuItem(defaultBundle.getString("PROPERTIES"));
        items[4].setOnAction((evt) -> {
            new PropertiesWindow(getItem()).start(new Stage());
        });
        menu = new ContextMenu(items);
    }

    @Override
    protected void updateItem(Connection item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
        if (!empty) {
                menu.getItems().get(4).disableProperty().bind(item.connectProperty());
                textProperty().bind(item.nameProperty());
                graphicProperty().bind(getTreeItem().graphicProperty());
                setContentDisplay(ContentDisplay.LEFT);
                setContextMenu(menu);
        }else{
            textProperty().unbind();
            graphicProperty().unbind();
            setText(null);
            setGraphic(null);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        graphicProperty().unbind();
        textfield.setText(getItem().nameProperty().get());
        setGraphic(textfield);
        textfield.selectAll();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        graphicProperty().bind(getTreeItem().graphicProperty());
        setContentDisplay(ContentDisplay.LEFT);
    }

}
