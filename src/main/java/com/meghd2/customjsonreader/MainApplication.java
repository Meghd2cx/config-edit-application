package com.meghd2.customjsonreader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AppProperties;
import model.UserProperties;

import java.io.IOException;

public class MainApplication extends Application {

    public static Stage launchStage;
    public static FXMLLoader currentLoader;

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