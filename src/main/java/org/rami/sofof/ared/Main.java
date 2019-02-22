/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared;

import java.util.ResourceBundle;
import javafx.application.Application;
import org.rami.sofof.ared.assets.ExceptionsTracker;
import org.rami.sofof.ared.assets.Session;
import org.rami.sofof.ared.window.MainWindow;
import org.rami.sofof.ared.window.Window;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class Main {

    public static void main(String[] args) {
        try {
            Session.init();
            Window.setDefaultFXMLPath("org/rami/sofof/ared/fxml");
            Window.setDefaultCSSPath("org/rami/sofof/ared/css");
            Window.setDefaultTheme("DefaultTheme");
            Window.setDefaultResourceBundle(ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle"));
            Application.launch(MainWindow.class, args);
        } catch (Exception ex) {
            ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.FATAL);
        }
    }

}
