/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.control;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ResultsTreeCell extends TreeCell<Object> {

    

    private final TextField objectValue;

    public ResultsTreeCell() {
        super();
        objectValue = new TextField();
        objectValue.setOnAction((evt) -> {
            cancelEdit();
        });
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            LazyObjectTreeItem treeItem = (LazyObjectTreeItem) getTreeItem();
            setText(treeItem.getName());
            setGraphic(treeItem.getGraphic());
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            setText(null);
            setGraphic(null);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit(); //To change body of generated methods, choose Tools | Templates.
        objectValue.setText(getItem().getClass().getSimpleName() +"," +getItem().toString());
        setGraphic(objectValue);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(((LazyObjectTreeItem)getTreeItem()).getGraphic());
        setContentDisplay(ContentDisplay.LEFT);
    }

}
