/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.pojo;

import com.sefr.sofof.Database;
import com.sefr.sofof.Server;
import com.sefr.sofof.Session;
import com.sefr.sofof.SofofException;
import com.sefr.sofof.permission.User;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class Connection implements Serializable {

    private static final long serialVersionUID = 932567825288638L;

    private SimpleStringProperty name;
    private transient SimpleBooleanProperty connect;
    private String path;
    private int port;
    private boolean startServer;
    private String username;
    private String password;
    private boolean ssl;
    private transient Session session;
    private transient Server server;

    public Connection() {
        init();
    }

    private void init() {
        name = new SimpleStringProperty(this, "name", "");
        connect = new SimpleBooleanProperty(this, "connect", false);
    }

    public Connection(String path, int port, boolean ssl) {
        this();
        name.set(path);
        this.path = path;
        this.port = port;
        this.startServer = true;
        this.username = "Rami";
        this.password = "Password";
        this.ssl = ssl;
    }

    public Connection(String path, int port, String username, String password, boolean ssl) {
        this(path, port, ssl);
        this.startServer = false;
        this.username = username;
        this.password = password;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (name.get().equals(this.path)) {
            name.set(path);
        }
        this.path = path;
    }

    public boolean isStartServer() {
        return startServer;
    }

    public void setStartServer(boolean startServer) {
        this.startServer = startServer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public Session getSession() {
        return session;
    }

    public Server getServer() {
        return server;
    }

    public void connect() throws SofofException {
        User user = new User(username, password);
        if (startServer) {
            File database = new File(path);
            if (!database.exists()) {
                Database.createDatabase(database);
            }
            server = new Server(database, port, ssl);
            server.addUser(user);
            server.startUp();
        }
        session = new Database(startServer ? "localhost" : path, port).startSession(user, ssl);
        connect.set(true);
    }

    public void disconnect() throws SofofException {
        if (session != null) {
            session.close();
        }
        if (server != null) {
            server.shutdown();
        }
        connect.set(false);
    }

    public void activate() throws SofofException {
        if (!connect.get()) {
            connect();
        }
        org.rami.sofof.ared.assets.Session.activeConnectionProperty().set(this);
    }

    public void deactivate() {
        org.rami.sofof.ared.assets.Session.activeConnectionProperty().set(null);
    }

    public SimpleBooleanProperty connectProperty() {
        return connect;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(name.get());
        out.writeUTF(path);
        out.writeInt(port);
        out.writeUTF(username);
        out.writeUTF(password);
        out.writeBoolean(startServer);
        out.writeBoolean(ssl);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        init();
        name.set(in.readUTF());
        path = in.readUTF();
        port = in.readInt();
        username = in.readUTF();
        password = in.readUTF();
        startServer = in.readBoolean();
        ssl = in.readBoolean();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
    }

}
