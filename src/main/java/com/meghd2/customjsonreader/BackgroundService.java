package com.meghd2.customjsonreader;

import model.UserProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

public class BackgroundService implements Runnable {

    private BackgroundServiceType type ;
    private String [] options ;

    public BackgroundService (BackgroundServiceType type, String ... options) {
        this.type = type;
        this.options = options;
    }

    @Override
    public void run() {
        switch (type) {
            case VALIDATEUSER -> validateUserLoop();
            case FETCHREPO -> fetchRepo();
            case VALIDATEJSON -> validateJSON();
            case SAVELOCAL -> saveLocal();
            default -> System.out.println("Not a valid background service");
        }

    }

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

    private boolean validateSignin() {

        MainApplication.userProperties = retriveUserInfo();
        if (MainApplication.userProperties == null) {
            return false;
        }
        return true;
    }
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
                //TODO: Add remaining OAuth returned fields + update UserProperties
                retJSON.getString("access_token");
                //TODO: finish verification process
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

    private void saveLocal() {

    }

    private void validateJSON() {
        appController mCCopy = MainApplication.currentLoader.getController();
        System.out.println(mCCopy.selectedFileField.toString());
    }

    private void fetchRepo() {
    }
}

