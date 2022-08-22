package com.meghd2.customjsonreader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AppProperties;
import model.UserProperties;

import java.io.IOException;

/**
 * Starting point for ConfEdit application
 *
 * @author Meghnath Dey
 * */
public class MainApplication extends Application {

    /**
     * Stores the stage for the launcher window
     * */
    public static Stage launchStage;
    /**
     * Store the currentLoader for the application. Changes based on which part of the flow the application is on.
     * */
    public static FXMLLoader currentLoader;

    /**
     * Stores the current user as well as any other app properties used with in the application, such as organizations, repositories, etc.
     * */
    public static AppProperties appProperties = new AppProperties();

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("launch-view.fxml"));
        currentLoader = fxmlLoader;
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        launchStage = stage;
        stage.setTitle("JSON Reader");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}