package com.meghd2.customjsonreader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;

public class launcherController {

    @FXML
    Button signIn;

    public static Stage primaryStage = new Stage();

    @FXML
    protected void onLaunchButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("file-view.fxml"));
        Parent root = loader.load();

        //Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Rectangle bounds = new Rectangle(1920, 1080);
        Scene scene = new Scene(root,bounds.getWidth(),bounds.getHeight());
        //Sets main fxml as current scene

         mainController controller = loader.getController();

         primaryStage.setScene(scene);
         primaryStage.setResizable(true);
         primaryStage.setMaximized(true);
         MainApplication.launchStage.close();
         primaryStage.show();
    }

    @FXML
    protected void onSignInButtonClick() {
        signIn.setText("Signed In");
        signIn.setDisable(true);
        signIn.setTextFill(Color.valueOf("00FF00"));
    }
}