/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.control;

import com.sefr.sofof.ID;
import java.lang.reflect.Field;
import java.util.LinkedList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import org.rami.sofof.ared.assets.ExceptionsTracker;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class LazyObjectTreeItem extends TreeItem {

    private static final String OBJECT_IMAGE = "/org/rami/sofof/ared/icon/cube.png";
    private static final String FIELD_IMAGE = "/org/rami/sofof/ared/icon/network.png";
    private static final String NULL_IMAGE = "/org/rami/sofof/ared/icon/phi.png";
    private static final String ID_IMAGE = "/org/rami/sofof/ared/icon/fingerprint.png";

    private String name;
    private LinkedList references;

    public LazyObjectTreeItem(String name, Object value, Node graphic, LazyObjectTreeItem parent) {
        super(value, graphic);
        this.name = name;
        if (parent == null) {
            references = new LinkedList();
        }
        if (value != null && !value.getClass().getPackage().getName().startsWith("java.lang")) {
            try {
                for (Field field : value.getClass().getFields()) {
                    if (parent != null) {
                        if (parent.containsReference(value)) {
                            value = "ARED_FOUND_REFERENCE " + value;
                        }
                    }
                    String imgURL;
                    if (field.get(value) == null) {
                        imgURL = NULL_IMAGE;
                    } else if (field.get(value) instanceof ID) {
                        imgURL = ID_IMAGE;
                    } else {
                        imgURL = FIELD_IMAGE;
                    }
                    if (value != "ARED_FOUND_REFERENCE") {
                        getChildren().add(new LazyObjectTreeItem(field.getName(), field.get(value), imageCreator(imgURL, 20, 20), parent == null ? this : parent));
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
            }
        }
    }

    public LazyObjectTreeItem(String name, Object value) {
        this(name, value, null, null);
        setGraphic(imageCreator(OBJECT_IMAGE, 20, 20));
    }

    public String getName() {
        return name;
    }

    private boolean containsReference(Object item) {
        if (item == null) {
            return false;
        }
        if (references.contains(item)) {
            return true;
        } else {
            references.add(item);
            return false;
        }
    }

    private static ImageView imageCreator(String url, int height, int width) {
        ImageView img = new ImageView(url);
        img.setFitHeight(height);
        img.setFitWidth(width);
        return img;
    }

}
