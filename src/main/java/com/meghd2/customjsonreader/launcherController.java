package com.meghd2.customjsonreader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import model.UserProperties;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class launcherController implements Initializable{

    @FXML
    Button signIn;
    @FXML
    Button launchApp;

    public static Stage primaryStage = new Stage();

    String githubDomain;
    String clientId;
    String clientSecret;
    String redirectUri;
    String scope;
    String grantType;
    String user_code = "";

    @Override
    public void initialize (URL location, ResourceBundle resources) {
        launchApp.setDisable(true);
        validateSignin();
    }
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
    protected void onSignInButtonClick() {
        try {
            loadProperties();
            user_code = requestVerificationCode();
            showGithubPopup(user_code);
            validateSignin();
        }
        catch (Exception e) {
            e.printStackTrace();
           System.out.println("Login Failed");
        }

    }

    private boolean validateSignin() {
        try {
            retriveUserInfo();

            signIn.setText("Signed In");
            signIn.setDisable(true);
            signIn.setTextFill(Color.valueOf("00FF00"));
            launchApp.setDisable(false);
            retriveUserInfo();

            //TODO: Define user and app properties
            MainApplication.userProperties = new UserProperties();


        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void retriveUserInfo() {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://github.com/login/oauth/access_token");
            builder.setParameter("client_id",clientId);
            builder.setParameter("device_code",user_code);
            builder.setParameter("grant_type","urn:ietf:params:oauth:grant-type:device_code");
            HttpPost httpPost = new HttpPost(builder.build());
            httpPost.setHeader(new JSONHeader());

            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200){
                throw new IOException("Github Authentication Failed");
            }
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject retJSON = new JSONObject(responseStr);

            //TODO: finish verification process
            System.out.println(responseStr);


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String requestVerificationCode() throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        URIBuilder builder = new URIBuilder("https://github.com/login/device/code");
        builder.setParameter("client_id", clientId);
        builder.setParameter("scope","repo");
        HttpPost httpPost = new HttpPost(builder.build());
        httpPost.setHeader(new JSONHeader());
        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new Exception("Github Authentication Failed");
        }
        String responseStr = EntityUtils.toString(response.getEntity());
        JSONObject retJSON = new JSONObject(responseStr);
        String user_code = retJSON.getString("user_code");
        System.out.println(user_code);

        client.close();

        return user_code;
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

    private void showGithubPopup(String user_code) throws URISyntaxException {

//        Popup popup = new Popup();
        //Creating popup pane
        VBox vertical = new VBox();

        Scene popup = new Scene(vertical);

        //Setting pane style and other settings
        vertical.setFillWidth(true);
        vertical.setAlignment(Pos.CENTER);
        vertical.setPadding(new Insets(20, 10, 10 , 10));
        vertical.setStyle("-fx-background-color: F8F0E3; -fx-arc-height: 10; -fx-arc-width: 10");

        //Creating title element
        Label popupTitle = new Label("Sign-in to Github");
        popupTitle.setAlignment(Pos.CENTER);
        popupTitle.setCenterShape(true);
        popupTitle.setStyle("-fx-font-size: 20; -fx-alignment: center;");

        //Creating user_code label
        Label deviceCodeLabel = new Label(user_code);
        deviceCodeLabel.setAlignment(Pos.CENTER);
        deviceCodeLabel.setStyle("-fx-font-size: 20");

        //Creating instructions label
        Label instructionLabel = new Label("Type the device code into:");
        instructionLabel.setAlignment(Pos.CENTER);

        Hyperlink githubLink = new Hyperlink();
        githubLink.setText("Github - Device Verification");
        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();


        githubLink.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/login/device"));
            }
            catch (Exception e) {
                System.out.println("Login Failed");
            }
        });

        //Adding elements to pane
        vertical.getChildren().addAll(popupTitle, deviceCodeLabel, instructionLabel, githubLink);

        primaryStage.setScene(popup);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

