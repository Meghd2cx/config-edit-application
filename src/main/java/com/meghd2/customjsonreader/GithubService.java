package com.meghd2.customjsonreader;

import com.google.gson.Gson;
import model.Organization;
import model.Repository;
import model.UserProperties;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class GithubService {

    private static final CloseableHttpClient client = HttpClients.createDefault();
    private static final Gson gson = new Gson();


    public static void updateAll() {
        updateAppUser();
        updateAppOrgs();
    }
    public static String getGithubUsername () {
        String username;
        try {
            URIBuilder builder = new URIBuilder("https://api.github.com/user");
            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.addHeader("Accept","application/vnd.github+json");
            httpGet.addHeader("Authorization","token " + MainApplication.appProperties.getUserProperties().getAccess_token());

            CloseableHttpResponse response = client.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(responseStr);
                throw new IOException();
            }
            System.out.println("Response string" + responseStr);
            JSONObject retJSON = new JSONObject(responseStr);
            username = retJSON.getString("login");

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return username;
    }
    public static ArrayList getOrganizationList() {
        ArrayList<Organization> orgs = null;
        try {
            URIBuilder builder = new URIBuilder("https://api.github.com/user/repos");
            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.addHeader("Accept","application/vnd.github+json");
            httpGet.addHeader("Authorization","token " + MainApplication.appProperties.getUserProperties().getAccess_token());

            CloseableHttpResponse response = client.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException();
            }
            JSONArray orgListJSON = new JSONArray(responseStr);

            orgs = new ArrayList<>();

            for (int i = 0; i < orgListJSON.length(); i++) {
                JSONObject org = orgListJSON.getJSONObject(i);
                Gson gson = new Gson();
                orgs.add(gson.fromJson(org.toString(),Organization.class));
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return orgs;
        }
        return orgs;
    }

    public static void updateAppUser() {
        try {
            URIBuilder builder = new URIBuilder("https://api.github.com/user");
            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.addHeader("Accept","application/vnd.github+json");
            httpGet.addHeader("Authorization","token " + MainApplication.appProperties.getUserProperties().getAccess_token());

            CloseableHttpResponse response = client.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println(responseStr);
                throw new IOException();
            }

            JSONObject retJSON = new JSONObject(responseStr);
            UserProperties appUser = MainApplication.appProperties.getUserProperties();

            appUser.setLogin(retJSON.getString("login"));
            appUser.setEmail(retJSON.get("email").toString());
            appUser.setId(retJSON.getInt("id"));

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        System.out.println("Local User updated");
        System.gc();
    }

    public static void updateAppOrgs() {
        try {
            //Sending GET request to repos API
            URIBuilder builder = new URIBuilder("https://api.github.com/user/repos");
            HttpGet httpGet = new HttpGet(builder.build());
            httpGet.addHeader("Accept","application/vnd.github+json");
            httpGet.addHeader("Authorization","token " + MainApplication.appProperties.getUserProperties().getAccess_token());

            CloseableHttpResponse response =  client.execute(httpGet);
            String responseStr = EntityUtils.toString(response.getEntity());

            //Iterates through all repos
            JSONArray repos = new JSONArray(responseStr);

            for (int i = 0; i < repos.length(); i++) {
                JSONObject repoJSON = repos.getJSONObject(i);
                String ownerOrg = getRepoOwnerLogin(repoJSON.getString("owner"));
                ArrayList<String> orgNameList = MainApplication.appProperties.getOrgNames();
                //Adds organization to AppProperties if not already added
                if (!orgNameList.contains(ownerOrg)) {
                    ArrayList<Organization> appOrgs = MainApplication.appProperties.getOrgs();
                    appOrgs.add(gson.fromJson(repoJSON.getString("owner"),Organization.class));
                }
                //Adds repo to owner organization
                Organization o = MainApplication.appProperties.getOrgs().get(orgNameList.indexOf(ownerOrg));
                o.addRepo(gson.fromJson(repoJSON.toString(), Repository.class));
            }

        } catch (URISyntaxException | IOException e) {

        }
        System.out.println("Local Organizations and Repositories updated");
        System.gc();
    }

    private static String getRepoOwnerLogin(String owner) {
        JSONObject ownerObj = new JSONObject(owner);
        return ownerObj.getString("login");
    }

    private void getRepoList() {

    }

    private static void updateRepo(Organization org) {

    }
}
