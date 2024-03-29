package com.meghd2.customjsonreader;

import com.google.gson.Gson;
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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

import model.UserProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class defines all necessary functions and elements present on the launch-view. All elements annotated by @FXML is referenced by its associated
 * FXML element in the launch-view.fxml file. All functions annotated by @FXML are also referenced by an FXML element in the launch-view.fxml file.
 *
 * @author Meghnath Dey
 *
 * TODO: Improve background thread verification method. Currently operated on interrupting thread at timed interval
 * TODO: Create singleton thread based out of Main that runs login polling from app start, automatically killing itself after user is validated and saved
 * */

public class launcherController implements Initializable{

    /** Represent the GitHub sign-in button*/
    @FXML
    Button signIn;
    /** Represent the launch app button*/
    @FXML
    Button launchApp;

    /** Stores primaryStage for use with main app and Github authentication window launches*/
    public static Stage primaryStage = new Stage();
    /**
     * GitHub API response properties
     * */
    String githubDomain;
    String clientId;
    String clientSecret;
    String redirectUri;
    String scope;
    String grantType;
    String device_code = "";
    String user_code = "";


    /**
     * Run automatically when associated launch-view.fxml is loaded.
     * */
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        disableAppLaunch();
        if(validateLocalUser()) {
            enableAppLaunch();
            return;
        }
        disableAppLaunch();
    }
    /** Creates FXMLLoader object and loads app-view.fxml resource into application's current loader.
     * Sets scene and stage for main application and sets the primary stage as the app.
     * */
    @FXML
    protected void onLaunchButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("app-view.fxml"));
        Parent root = loader.load();
        MainApplication.currentLoader = loader;

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

    /**
     * Event specific function, loads GitHub API keys into local variables. Requests OAuth verification code using GET request from GitHub API.
     * Starts validateUser background thread. Then shows the GitHub popup to allow the user to copy the code and sign in using a web browser.
     * */
    @FXML
    protected void onSignInButtonClick() {
        try {
            loadProperties();
            requestVerificationCode();
            BackgroundService validateUserService = new BackgroundService(BackgroundServiceType.VALIDATEUSER, clientId, device_code);
            Thread validateUserThread = new Thread(validateUserService, "validateUser");
            validateUserThread.start();
            showGithubPopup(user_code);
            primaryStage.setOnCloseRequest(event -> verifyUserAuthentication(validateUserThread));
        }
        catch (Exception e) {
            e.printStackTrace();
           System.out.println("Login Failed");
        }

    }

    /** Enables or disables login based on validateUser thread status */
    private void verifyUserAuthentication(Thread th) {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!th.isAlive()) {
            enableAppLaunch();
            primaryStage.setOnCloseRequest(null);
        }
        else {
            th.stop();
            disableAppLaunch();
        }
    }
    /** Methods used for validating signed in user*/
    private boolean validateLocalUser() {
        try {
            File userFile = new File("src/main/resources/AppData/userProperties.json");
            Scanner userFileScanner = new Scanner(userFile);
            String userJSON = userFileScanner.nextLine();
            Gson gson = new Gson();
            MainApplication.appProperties.setUserProperties(gson.fromJson(userJSON,UserProperties.class));
            return true;
        }
        catch (FileNotFoundException | JSONException | NoSuchElementException e) {
            return false;
        }

    }

    /**
     * Run when signin is successful and app launch is allowed by enabling launch button and disabling sign in button
     * */
    private void enableAppLaunch() {
        signIn.setText("Signed In");
        signIn.setDisable(true);
        signIn.setTextFill(Color.valueOf("00FF00"));
        launchApp.setDisable(false);
    }

    /**
     * Run when signin is not successful and app launch is not allowed by disabling launch button and enabling sign in button
     * */
    private void disableAppLaunch() {
        launchApp.setDisable(true);
        signIn.setDisable(false);
        signIn.setTextFill(Color.BLACK);
    }


    /** Methods used for Github OAuth Verification */
    private void requestVerificationCode() throws Exception {

        CloseableHttpClient client = HttpClients.createDefault();

        URIBuilder builder = new URIBuilder("https://github.com/login/device/code");
        builder.setParameter("client_id", clientId);
        builder.setParameter("scope","repo user project");
        HttpPost httpPost = new HttpPost(builder.build());
        httpPost.setHeader(new JSONHeader());
        CloseableHttpResponse response = client.execute(httpPost);
        if (response.getStatusLine().getStatusCode() != 200){
            throw new Exception("Github Authentication Failed");
        }
        String responseStr = EntityUtils.toString(response.getEntity());
        JSONObject retJSON = new JSONObject(responseStr);
        user_code = retJSON.getString("user_code");
        device_code = retJSON.getString("device_code");
        System.out.println(user_code);

        StringSelection stringSelection = new StringSelection(user_code);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        client.close();
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

    /** Displays new window with user_code and link for sign in*/
    private void showGithubPopup(String user_code) {

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
        Label instructionLabel = new Label("Paste the device code into:");
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

