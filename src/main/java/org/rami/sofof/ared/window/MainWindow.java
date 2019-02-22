/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.sofof.ared.window;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.rami.sofof.ared.assets.ExceptionsTracker;
import org.rami.sofof.ared.assets.Session;
import org.rami.sofof.ared.control.ConnectionsTreeItem;
import org.rami.sofof.ared.control.ConnectionsTreeCell;
import org.rami.sofof.ared.control.ResultsView;
import org.rami.sofof.ared.pojo.Connection;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class MainWindow extends Window {

    private static final ResourceBundle defaultBundle = ResourceBundle.getBundle("org/rami/sofof/ared/locale/DefaultBundle");
    private static final String[] KEYWORDS = new String[]{
        "break", "case", "catch", "continue", "debugger",
        "default", "delete", "do", "else", "finally", "for",
        "function", "if", "in", "instanceof", "new", "return",
        "switch", "this", "throw", "try", "typeof", "var", "void",
        "while", "with"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^\"\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    @FXML
    private TreeView connectionsTree;
    @FXML
    private Button run, add;
    @FXML
    private ComboBox scriptingLanguageChooser;
    @FXML
    private TextArea logger;
    @FXML
    private ResultsView result;
    private CodeArea codeArea;
    private ExecutorService executor;

    @Override
    protected void start() {
        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        // call when no longer need it: `cleanupWhenFinished.unsubscribe();`
        ((BorderPane) root).setCenter(new StackPane(new VirtualizedScrollPane<>(codeArea)));
        codeArea.setOnKeyReleased((event) -> {
            if (event.isControlDown() && event.getCode().equals(KeyCode.I)) {
                addConnection();
            } else if (event.getCode().equals(KeyCode.F5)) {
                run();
            } else {
                ConnectionsTreeItem selected = (ConnectionsTreeItem) connectionsTree.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    if (event.getCode().equals(KeyCode.DELETE)) {
                        connectionsTree.getRoot().getChildren().remove(selected);
                    }
                }
            }
        });
        codeArea.requestFocus();
        //tree config
        connectionsTree.setFocusTraversable(false);
        TreeItem rootItem = new TreeItem();
        Session.getConnections().forEach((connection) -> rootItem.getChildren().add(new ConnectionsTreeItem(connection)));
        connectionsTree.setRoot(rootItem);
        connectionsTree.setCellFactory((param) -> {
            return new ConnectionsTreeCell();
        });
        //toolbar
        run.setTooltip(new Tooltip(defaultBundle.getString("RUN")));
        add.setTooltip(new Tooltip(defaultBundle.getString("ADD")));
        scriptingLanguageChooser.setTooltip(new Tooltip(defaultBundle.getString("SCRIPTING_ENGINE")));
        Session.activeConnectionProperty().addListener((observable, oldValue, newValue) -> {
            run.setDisable(newValue == null);
        });
        for (ScriptEngineFactory factory : new ScriptEngineManager().getEngineFactories()) {
            scriptingLanguageChooser.getItems().add(factory.getLanguageName());
        }
        if (scriptingLanguageChooser.getItems().isEmpty()) {
            scriptingLanguageChooser.setDisable(true);
        } else {
            scriptingLanguageChooser.getSelectionModel().selectFirst();
        }
        //scene
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle(defaultBundle.getString("ARED_SOFOF"));
        stage.setOnHiding((evt) -> {
            LinkedList<Connection> connections = new LinkedList<>();
            connectionsTree.getRoot().getChildren().forEach((Object connection) -> connections.add(((TreeItem<Connection>) connection).getValue()));
            Session.setConnections(connections);
            Session.save();
        });
        stage.show();
    }

    @Override
    public void stop() {
        executor.shutdown();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        final String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.<String>emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.<String>emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @FXML
    private void addConnection() {
        try {
            connectionsTree.getRoot().getChildren().add(new ConnectionsTreeItem(new ConnectionEstablisherDialog().show(stage)));
        } catch (IOException ex) {
            ExceptionsTracker.report(ex, ExceptionsTracker.ExceptionType.ERROR);
        }
    }

    @FXML
    private void run() {
        String language = (String) scriptingLanguageChooser.getSelectionModel().getSelectedItem();
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = null;
        for (ScriptEngineFactory factory : manager.getEngineFactories()) {
            if (factory.getLanguageName().equals(language)) {
                engine = factory.getScriptEngine();
                break;
            }
        }
        if (engine == null) {
            ExceptionsTracker.report(new Exception("unable to find the engine for " + language), ExceptionsTracker.ExceptionType.ERROR);
        } else {
            logger.setText("");
            Connection active = Session.activeConnectionProperty().get();
            if (active.isStartServer()) {
                engine.put("server", active.getServer());
            }
            engine.put("session", active.getSession());
            long start = System.currentTimeMillis();
            try {
                engine.eval(codeArea.getText());
                Object printed = engine.getBindings(ScriptContext.ENGINE_SCOPE).get("printed");
                if (printed != null && (printed instanceof String)) {
                    addToLog((String) printed + "\n");
                }
                addToLog("Successful in " + (System.currentTimeMillis() - start) + "ms\n");
                Object resultSet = engine.get("result");
                String resultMessage;
                if (resultSet == null || !(resultSet instanceof List)) {
                    resultMessage = "no result found";
                } else {
                    resultMessage = "result found and will be viewed";
                    result.setData((List) resultSet);
                }
                addToLog(resultMessage, "\n");
            } catch (Throwable ex) {
                String stack = "";
                do {
                    if (!stack.isEmpty()) {
                        stack += "Caused by " + ex.getClass().getSimpleName() + " " + ex.getMessage() + "\n";
                    } else {
                        stack += ex.getClass().getSimpleName() + " " + ex.getMessage() + "\n";
                    }
                    for (StackTraceElement element : ex.getStackTrace()) {
                        stack = stack + "    at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + ":" + element.getLineNumber() + ")" + "\n";
                    }
                } while ((ex = ex.getCause()) != null);
                addToLog("Failed in ", String.valueOf(System.currentTimeMillis() - start), "ms\n", stack);
            }
        }
    }

    private void addToLog(String text) {
        logger.setText(logger.getText() + text);
    }

    private void addToLog(String... text) {
        for (String element : text) {
            addToLog(element);
        }
    }

}
