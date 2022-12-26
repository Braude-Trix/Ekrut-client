package client;

import gui.workers.OperationalWorkerGui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class OperationalWorker extends Application {
    public static Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        OperationalWorker.primaryStage = primaryStage;
        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/workers/OperationalWorkerHomePage_Default.fxml"));
            anchorPane = loader.load();
            OperationalWorkerGui.controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Operational Worker"); // set window title
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}