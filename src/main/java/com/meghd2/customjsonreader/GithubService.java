package com.meghd2.customjsonreader;

import com.google.gson.Gson;
import model.Organization;
import model.Repository;
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

    public static void updateApplicationUser() {

    }

    public static void updateAppRepos() {
//        try {
//            URIBuilder builder = new URIBuilder("https://api.github.com/user/repos");
//            HttpGet httpGet = new HttpGet(builder.build());
//            httpGet.addHeader("Accept","application/vnd.github+json");
//            httpGet.addHeader("Authorization","token " + MainApplication.appProperties.getUserProperties().getAccess_token());
//
//            CloseableHttpResponse response =  client.execute(httpGet);
//            String responseStr = EntityUtils.toString(response.getEntity());
//
//            JSONArray repos = new JSONArray(responseStr);
//            for (int i = 0; i < repos.length(); i++) {
//                JSONObject repoJSON = repos.getJSONObject(i);
//                String ownerOrg = getRepoOwnerLogin(repoJSON.getString("owner"));
//                ArrayList<String> orgNameList = MainApplication.appProperties.getOrgNames();
//                if (orgNameList.contains(ownerOrg)) {
//                    Organization o = MainApplication.appProperties.getOrgs().get(orgNameList.indexOf(ownerOrg));
//                    Gson gson = new Gson();
//                    o.addRepo(gson.fromJson(repoJSON.toString(), Repository.class));
//                }
//
//            }
//
//        } catch (URISyntaxException | IOException e) {
//
//        }
//
//        System.gc();

    }

    private static String getRepoOwnerLogin(String owner) {
        JSONObject ownerObj = new JSONObject(owner);
        return ownerObj.getString("login");
    }
}
