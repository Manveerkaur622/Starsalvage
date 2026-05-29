package starsalvage.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import starsalvage.engine.Direction;
import starsalvage.engine.GameEngine;
import starsalvage.engine.GamePersistence;
import starsalvage.engine.MoveResult;

import java.io.File;

public class StarSalvageApp extends Application {
    private GameEngine engine;
    private GridPane boardGrid;
    private Label statusLabel;
    private TextArea logArea;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.engine = new GameEngine();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setTop(titleBar());
        root.setCenter(boardPanel());
        root.setRight(controlPanel());
        root.setBottom(statusPanel());

        render();
        Scene scene = new Scene(root, 850, 560);
        stage.setTitle("StarSalvage");
        stage.setScene(scene);
        stage.show();
    }

    private HBox titleBar() {
        Label title = new Label("StarSalvage");
        title.setFont(Font.font(24));
        Label goal = new Label("Collect all salvage crates (*) and reach the exit airlock (E).");
        HBox box = new HBox(16, title, goal);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(0, 0, 12, 0));
        return box;
    }

    private GridPane boardPanel() {
        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(6);
        boardGrid.setVgap(6);
        return boardGrid;
    }

    private VBox controlPanel() {
        Button north = new Button("North");
        Button south = new Button("South");
        Button west = new Button("West");
        Button east = new Button("East");
        Button wait = new Button("Wait");
        Button undo = new Button("Undo");
        Button save = new Button("Save");
        Button load = new Button("Load");
        Button newGame = new Button("New game");

        north.setOnAction(e -> doAction(engine.move(Direction.NORTH)));
        south.setOnAction(e -> doAction(engine.move(Direction.SOUTH)));
        west.setOnAction(e -> doAction(engine.move(Direction.WEST)));
        east.setOnAction(e -> doAction(engine.move(Direction.EAST)));
        wait.setOnAction(e -> doAction(engine.waitTurn()));
        undo.setOnAction(e -> doAction(engine.undo()));
        save.setOnAction(e -> saveGame());
        load.setOnAction(e -> loadGame());
        newGame.setOnAction(e -> {
            engine = new GameEngine();
            appendLog("New mission started.");
            render();
        });

        GridPane movement = new GridPane();
        movement.setHgap(4);
        movement.setVgap(4);
        movement.add(north, 1, 0);
        movement.add(west, 0, 1);
        movement.add(wait, 1, 1);
        movement.add(east, 2, 1);
        movement.add(south, 1, 2);

        VBox actions = new VBox(8, movement, undo, save, load, newGame, legend());
        actions.setPadding(new Insets(0, 0, 0, 16));
        actions.setPrefWidth(240);
        return actions;
    }

    private Label legend() {
        Label label = new Label("Legend\nP player\n* salvage\n+ medkit\nS shield\nE exit\nd/A/H enemy states");
        label.setWrapText(true);
        return label;
    }

    private VBox statusPanel() {
        statusLabel = new Label();
        statusLabel.setWrapText(true);
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefRowCount(5);
        VBox panel = new VBox(8, statusLabel, logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);
        panel.setPadding(new Insets(12, 0, 0, 0));
        return panel;
    }

    private void doAction(MoveResult result) {
        appendLog(result.getMessage());
        render();
        if (engine.getState().isFinished()) {
            String outcome = engine.getState().isWon() ? "Mission complete" : "Mission failed";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, outcome + "\n" + result.getMessage());
            alert.setTitle("StarSalvage");
            alert.setHeaderText(outcome);
            alert.showAndWait();
        }
    }

    private void saveGame() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save StarSalvage game");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save files", "*.save"));
        chooser.setInitialFileName("starsalvage.save");
        File file = chooser.showSaveDialog(primaryStage);
        if (file == null) {
            return;
        }
        try {
            GamePersistence.save(engine, file.toPath());
            appendLog("Saved game to " + file.getName());
        } catch (Exception ex) {
            showError("Save failed", ex.getMessage());
        }
    }

    private void loadGame() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load StarSalvage game");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save files", "*.save"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file == null) {
            return;
        }
        try {
            engine = GamePersistence.load(file.toPath());
            appendLog("Loaded game from " + file.getName());
            render();
        } catch (Exception ex) {
            showError("Load failed", ex.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.showAndWait();
    }

    private void appendLog(String message) {
        if (logArea != null) {
            logArea.appendText(message + System.lineSeparator());
        }
    }

    private void render() {
        boardGrid.getChildren().clear();
        char[][] board = engine.boardSymbols();
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[row].length; column++) {
                Label cell = new Label(String.valueOf(board[row][column]));
                cell.setFont(Font.font(22));
                cell.setAlignment(Pos.CENTER);
                cell.setMinSize(72, 72);
                cell.setStyle("-fx-border-color: #333333; -fx-background-color: #f4f6fb;");
                boardGrid.add(cell, column, row);
            }
        }
        statusLabel.setText(engine.statusText());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
