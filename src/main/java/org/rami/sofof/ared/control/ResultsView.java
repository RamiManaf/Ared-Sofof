/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.control;

import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ResultsView extends TreeView {

    private List data;

    public ResultsView() {
        super();
        data = new LinkedList();
        setShowRoot(false);
        setEditable(true);
        setCellFactory((param) -> {
            return new ResultsTreeCell();
        });
        TreeItem root = new TreeItem();
        setRoot(root);
    }

    public ResultsView(List data) {
        this();
        setData(data);
    }

    public void setData(List data) {
        this.data.addAll(data);
        getRoot().getChildren().clear();
        data.forEach((item) -> {
            getRoot().getChildren().add(new LazyObjectTreeItem(item.getClass().getSimpleName().concat(" ").concat(String.valueOf(data.indexOf(item))), item));
        });
    }

    public List getData() {
        return data;
    }

}
