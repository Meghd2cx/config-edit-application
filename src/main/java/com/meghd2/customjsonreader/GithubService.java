package com.meghd2.customjsonreader;

import com.google.gson.Gson;
import model.Organization;
import model.Repository;
import model.UserProperties;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

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
                String ownerOrg = getRepoOwnerLogin(repoJSON.getJSONObject("owner"));
                ArrayList<String> orgNameList = MainApplication.appProperties.getOrgNames();
                //Adds organization to AppProperties if not already added
                if (!orgNameList.contains(ownerOrg)) {
                    ArrayList<Organization> appOrgs = MainApplication.appProperties.getOrgs();
                    String ownerStr = repoJSON.getJSONObject("owner").toString();
                    appOrgs.add(gson.fromJson(ownerStr,Organization.class));

                    //Manually adding repos list since gson.fromJSON sets all vars not found in JSON as null
                    appOrgs.get(appOrgs.size()-1).setRepos(new ArrayList<Repository>());
                }
                //Adds repo to owner organization
                Organization o = MainApplication.appProperties.getOrg(ownerOrg);
                o.addRepo(gson.fromJson(repoJSON.toString(), Repository.class));
            }

        } catch (URISyntaxException | IOException e) {

        }
        System.out.println("Local Organizations and Repositories updated");
        System.gc();
    }

    private static String getRepoOwnerLogin(JSONObject owner) {
        return owner.getString("login");
    }

    public static void cloneRepository(Repository repo) {
//        try {
//            InputStream inputStream = GithubService.class.getClassLoader().getResourceAsStream("app.properties");
//            Properties appProps = new Properties();
//            appProps.load(inputStream);
//            String clientSecret = appProps.getProperty("githubClientSecret");
//
//            String repoUrl = "https://" + clientSecret + "@github.com/" + repo.getFull_name()+".git";
//            String cloneDirectoryPath = "src/main/resources/gitRepos/";
//
//            System.out.println("Cloning "+repoUrl+" into "+cloneDirectoryPath);
//            Git.cloneRepository()
//                    .setURI(repoUrl)
//                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
//                    .call();
//            System.out.println("Completed Cloning");
//
//            Git git = new Git();
//            String remoteUrl = "https://${token}@github.com/user/" + repo.getFull_name() + ".git";
//            CredentialsProvider credentialsProvider = (CredentialsProvider) new UsernamePasswordCredentialsProvider("${token}", "");
//            git.push().setRemote(remoteUrl).setCredentialsProvider(credentialsProvider).call();
//
//        } catch (GitAPIException | IOException e) {
//            System.out.println("Exception occurred while cloning repo");
//            e.printStackTrace();
//        }

    }
}
