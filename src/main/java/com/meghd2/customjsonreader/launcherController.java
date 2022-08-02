package com.meghd2.customjsonreader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class launcherController {

    @FXML
    Button signIn;

    public static Stage primaryStage = new Stage();

    String githubDomain;
    String clientId;
    String clientSecret;
    String redirectUri;
    String scope;
    String grantType;

    @FXML
    protected void onLaunchButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("app-view.fxml"));
        Parent root = loader.load();
        MainApplication.currentLoader = loader;
        //Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Rectangle bounds = new Rectangle(1920, 1080);
        Scene scene = new Scene(root,bounds.getWidth(),bounds.getHeight());
        //Sets main fxml as current scene

         appController controller = loader.getController();

         primaryStage.setScene(scene);
         primaryStage.setResizable(true);
         primaryStage.setMaximized(true);
         MainApplication.launchStage.close();
         primaryStage.show();
    }
    @FXML
    protected void onSignInButtonClick() throws URISyntaxException, IOException {
        signIn.setText("Signed In");
        signIn.setDisable(true);


        try {
            loadProperties();
            String device_id = requestVerificationCode();
            signIn.setTextFill(Color.valueOf("00FF00"));
            showGithubPopup(device_id);
        }
        catch (Exception e) {
            e.printStackTrace();
           System.out.println("Login Failed");
        }

    }

    private String requestVerificationCode() throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        URIBuilder builder = new URIBuilder("https://github.com/login/device/code");
        builder.setParameter("client_id", clientId);

        HttpPost httpPost = new HttpPost(builder.build());
        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new Exception("Github Authentication Failed");
        }
        String device_id = EntityUtils.toString(response.getEntity());
        System.out.println(device_id);
        client.close();

        return device_id;
    }

    /**
     * Loads our config info from the app.properties file
     * @throws IOException
     */
    public void loadProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("app.properties");
        Properties appProps = new Properties();
        appProps.load(inputStream);
        githubDomain = appProps.getProperty("githubDomain");
        clientId = appProps.getProperty("githubClientId");
        clientSecret = appProps.getProperty("githubClientSecret");
        redirectUri = appProps.getProperty("redirectUri");
        scope = appProps.getProperty("scope");
        grantType = appProps.getProperty("grantType");
    }

    private void showGithubPopup(String device_id) {
        final Popup popup = new Popup();
////        Popup popup1 = new Scene();
//        popup.getContent().addAll(new Rectangle(MainApplication.launchStage.getWidth(), MainApplication.launchStage.getHeight(),  Color.AQUAMARINE));
//        VBox vertical = new VBox();
//
//        vertical.setFillWidth(true);
//        vertical.setAlignment(Pos.BASELINE_CENTER);
//
//        Label popupTitle = new Label("Sign-in to Github");
//        popupTitle.setAlignment(Pos.BASELINE_CENTER);
//        popupTitle.setStyle("-fx-font-size: 30; -fx-alignment: center;");
//
//        vertical.getChildren().add(popupTitle);
//
//        popup.getContent().add(vertical);
//        popup.sizeToScene();
//        popup.show(MainApplication.launchStage);
    }
}

