/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.assets;

import javafx.stage.Stage;
import org.rami.sofof.ared.window.ExceptionsTrackerWindow;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class ExceptionsTracker {

    public static void report(Exception ex, ExceptionType type) {
        if (type.equals(ExceptionType.FATAL)) {
            System.err.println(ex);
            ex.printStackTrace();
            System.exit(1);
        } else {
            new ExceptionsTrackerWindow(ex.getClass().getSimpleName()+" "+ex.getMessage(), ex).start(new Stage());
        }
    }

    public static enum ExceptionType {
        FATAL,
        ERROR
    }

}
