/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.rami.sofof.ared.assets.ExceptionsTracker;

/**
 *
 * @author LENOVO PC
 */
public abstract class Window extends Application implements Initializable {
    
    private static ResourceBundle defaultResourceBundle;
    private static String defaultFXMLPath;
    private static String defaultCSSPath;
    private static String defaultTheme;
    protected Stage stage;
    protected Parent root;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public static void setDefaultResourceBundle(ResourceBundle defaultResourceBundle) {
        Window.defaultResourceBundle = defaultResourceBundle;
    }
    
    public static void setDefaultFXMLPath(String defaultFxmlPath) {
        Window.defaultFXMLPath = defaultFxmlPath;
    }
    
    public static void setDefaultCSSPath(String defaultCSSPath) {
        Window.defaultCSSPath = defaultCSSPath;
    }
    
    public static void setDefaultTheme(String defaultTheme) {
        Window.defaultTheme = defaultTheme;
    }
    
    public static String getDefaultTheme() {
        return defaultTheme;
    }
    
    protected static FXMLLoader loadFXML(String name, Initializable controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(Window.class.getResource("/" + defaultFXMLPath + "/" + name + ".fxml"), defaultResourceBundle);
        loader.setController(controller);
        loader.load();
        ((Parent) loader.getRoot()).getStylesheets().add(Window.class.getResource("/" + defaultCSSPath + "/" + defaultTheme + ".css").toExternalForm());
        return loader;
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = loadFXML(this.getClass().getSimpleName(), this);
            root = loader.getRoot();
            stage = primaryStage;
            start();
        } catch (Exception ex) {
            handleException(ex, stage);
        }
    }
    
    protected abstract void start();
    
    protected void handleException(Exception ex, Stage stage) {
        if(this instanceof ExceptionsTrackerWindow){
            ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.FATAL);
        }else
        ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
    }
    
}
