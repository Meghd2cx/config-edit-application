package com.meghd2.customjsonreader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.UserProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;


/**
 * BackgroundService class implementing Runnable interface. Allows for consistent creation and interface with Background/multi-threaded services.
 *
 * @author Meghnath Dey
 * */
public class BackgroundService implements Runnable {

    /** Current type of BackgroundService */
    private final BackgroundServiceType type ;
    /** String array with all necessary function args*/
    private final String [] options ;

    /**
     * Default and only constructor for Background Service
     *
     * @param type Type of Background Service being instantiated
     * @param options Arguments needed to run specified background service
     * */
    public BackgroundService (BackgroundServiceType type, String ... options) {
        this.type = type;
        this.options = options;
    }

    /**
     * Runnable interface override function. Used by Thread class to create background thread with specified type and arguments
     * */
    @Override
    public void run() {
        switch (type) {
            case VALIDATEUSER -> validateUserLoop();
            case FETCHREPO -> fetchRepo();
            case VALIDATEJSON -> validateJSON();
            case SAVELOCAL -> saveLocal();
            case LOCALJSONSERVER -> startLocalJSONServer();
            default -> System.out.println("Not a valid background service");
        }
    }

    /**
     * Creates local HTTP server that hosts given JSON content string for use with webView and JSON object view in app-view.fxml view panel
     * */
    private void startLocalJSONServer() {
        try {
            String selectedFileContents = options[0];
            HttpServer server = null;
            server = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
            server.createContext("/test", new MyHandler(selectedFileContents));
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Static Handler class used to respond to HTTP server request
     * @author Meghnath Dey
     * */
    static class MyHandler implements HttpHandler {

        private String jsonResp;
        public MyHandler(String jsonResp) {
            this.jsonResp = jsonResp;
        }
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = jsonResp;
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     * On GitHub authentication, validates whether device code has been entered every 5 seconds
     * @return Whether user has been validated
     * */
    private boolean validateUserLoop() {
        while (!validateSignin()) {
            try {
                System.out.println("Attempting user validation");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("User OAuth thread failed");
            }
        }
        return true;
    }

    /**
     * @return Whether current user is validated
     * */
    private boolean validateSignin() {

        MainApplication.appProperties.setUserProperties(retriveUserInfo());
        if (MainApplication.appProperties.getUserProperties() == null) {
            return false;
        }
        return true;
    }
    /**
     * Sends GitHub POST request to validate whether user has input device flow code yet.
     * @return UserProperties object with validated user's properties or null if not validated
     * */
    private UserProperties retriveUserInfo() {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            URIBuilder builder = new URIBuilder("https://github.com/login/oauth/access_token");
            builder.setParameter("client_id",options[0]);
            builder.setParameter("device_code",options[1]);
            builder.setParameter("grant_type","urn:ietf:params:oauth:grant-type:device_code");
            HttpPost httpPost = new HttpPost(builder.build());
            httpPost.setHeader(new JSONHeader());

            CloseableHttpResponse response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() != 200){
                throw new IOException("Github Authentication Failed");
            }
            String responseStr = EntityUtils.toString(response.getEntity());
            JSONObject retJSON = new JSONObject(responseStr);
            try {
                retJSON.getString("access_token");
                System.out.println(responseStr);
                UserProperties loggedinUser = new UserProperties();
                loggedinUser.setAccess_token(retJSON.getString("access_token"));
                loggedinUser.setToken_type(retJSON.getString("token_type"));
                loggedinUser.setScope(retJSON.getString("scope"));

                saveUserToFile(responseStr);

                return loggedinUser;
            }
            catch (JSONException e ) {
                System.err.println(responseStr);
                return null;
            }


        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Saves authenticated user's OAuth details to file
     * @param userResponse User OAuth details returned from GitHub user authentication API*/
    private void saveUserToFile (String userResponse) {

        try {
            File userFile = new File("src/main/resources/AppData/userProperties.json");
            if (!userFile.exists()) {
                userFile.createNewFile();
            }
            FileWriter userWriter = new FileWriter(userFile);
            PrintWriter printWriter = new PrintWriter(userWriter);
            printWriter.print(userResponse);
            printWriter.close();
            userWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("User not saved to file");
        }
    }

    /**
     * TODO: Saves current state of JSON file to local repository
     * */
    private void saveLocal() {

    }

    /**
     * TODO: Background method meant to validate JSON contents in real-time
     * */
    private void validateJSON() {
        appController mCCopy = MainApplication.currentLoader.getController();
        System.out.println(mCCopy.selectedFileField.toString());
    }

    /**
     * TODO: Fetches most recent version of local repositories on app startup and refresh
     * */
    private void fetchRepo() {

    }
}

