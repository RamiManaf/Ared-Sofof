/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.assets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import org.rami.sofof.ared.pojo.Connection;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class Session implements Serializable {

    private static final long serialVersionUID = 36291688;
    
    private List<Connection> connections;
    private HashMap pool;
    private transient SimpleObjectProperty<Connection> activeConnection;
    private static Session session;

    private Session() {
        connections = new LinkedList<>();
        pool = new HashMap();
        activeConnection = new SimpleObjectProperty<>(this, "activeConnection", null);
    }

    public static void init() {
        File lastSession = new File(".session");
        if (lastSession.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(lastSession))){
                session = (Session) in.readObject();
            } catch (IOException | ClassNotFoundException ex) {
                session = new Session();
            }
        } else {
            session = new Session();
        }
    }

    public static void save() {
        File save = new File(".session");
        try {
            if (!save.exists()) {
                save.createNewFile();
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(save));
            out.writeObject(session);
        } catch (IOException ex) {
            ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(connections);
        out.writeObject(pool);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        connections = (List<Connection>) in.readObject();
        pool = (HashMap) in.readObject();
        activeConnection = new SimpleObjectProperty<>(this, "activeConnection", null);
    }

    public static void put(String key, Object value) {
        session.pool.put(value, value);
    }

    public static Object get(String key) {
        return session.pool.get(key);
    }

    public static void setConnections(List<Connection> connections) {
        session.connections = connections;
    }

    public static List<Connection> getConnections() {
        return session.connections;
    }

    public static SimpleObjectProperty<Connection> activeConnectionProperty() {
        return session.activeConnection;
    }

}
