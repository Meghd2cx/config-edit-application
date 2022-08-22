package com.meghd2.customjsonreader;

import io.github.palexdev.materialfx.controls.MFXToggleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;


import model.Repository;
import org.json.*;
/**
 * This class defines all necessary functions and elements present on the main app-view. All elements annotated by @FXML is referenced by its associated
 * FXML element in the app-view.fxml file. All functions annotated by @FXML are also referenced by an FXML element in the app-view.fxml file.
 *
 * @author Meghnath Dey
 * */
public class appController implements Initializable {

    /** Represents the textArea where the selected file's raw contents are displayed. */
    @FXML
    TextArea selectedFileField;
    /** Represents the selectable listView representation of the current selected repository. */
    @FXML
    ListView fileTree;
    /** Represents the label found at the center of the Files panel. Used for file selection errors or other such notices.*/
    @FXML
    Label fileTreeLabel;
    /** Spinning progress indicator used when selecting repositories. */
    @FXML
    ProgressIndicator fileProgressSpinner;
    /** Represents top label in Files panel. Originally used to show which file is open in fileTree. */
    @FXML
    Label filePanelLabel;
    /** Label used to display user's GitHub username */
    @FXML
    Label accountNameLabel;
    /** HBox used to hold org and repo dropdowns. */
    @FXML
    HBox orgRepoHBox;
    /** ComboBox used to select repository */
    @FXML
    ComboBox repoComboBox;
    /** ComboBox used to select organization */
    @FXML
    ComboBox orgComboBox;
    /** Toggle switch between raw content and JSON object mode*/
    @FXML
    MFXToggleButton viewToggleButton;
    /** WebView used to load webpage for JSON object mode. */
    @FXML
    WebView webView;
    /** Label used to indicate whether selected doc is valid JSON or not*/
    @FXML
    Label centerLowerLabel;
    /** Stores the root folder of currently selected repository */
    private File rootFolder;
    /** Contains all files and sub-path files of selected repositories, with tabs based on depth of the file in the file tree*/
    private ArrayList<String> subFilePaths = new ArrayList<String>();
    /** Contains current file's contents in String format */
    String selectedFileContents = "";

    /**
     * Run automatically when associated app-view.fxml is loaded.
     * */
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        System.gc();
        if (rootFolder == null) {
            fileTree.setVisible(false);
            fileTreeLabel.setVisible(true);
            fileTreeLabel.setText("No Folder Selected");
        }

        fileTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        viewToggleButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onViewToggleButton();
            }
        });

        //Personalization through Github api
        GithubService.updateAll();
        updateOrgsDropdown();
        updateReposDropdown();

        //TODO: Use AppProperties object, not returned Objects from GithubService
        accountNameLabel.setText(GithubService.getGithubUsername());
        GithubService.getOrganizationList();

    }

    /** Event specific function to toggling viewToggleButton. Switches view panel between raw file content and rendered WebView with JSON object view.
     * Created basic local http server in background thread for WebView to reference contents of the currently selected JSON file.
     * */
   public void onViewToggleButton() {
       if (selectedFileField.isVisible()) {
           selectedFileField.setVisible(false);
           webView.setVisible(true);
           WebEngine webEngine = webView.getEngine();
           webEngine.setJavaScriptEnabled(true);
           BackgroundService jsonServer = new BackgroundService(BackgroundServiceType.LOCALJSONSERVER, selectedFileContents);
           Thread jsonServerThread = new Thread(jsonServer, "localJSONServer");
           jsonServerThread.start();
           webEngine.load("http://jsonviewer.stack.hu/#http://127.0.0.1:5000");
       }
       else {
           selectedFileField.setVisible(true);
           webView.setVisible(false);
       }

   }



    /** Event specific button run when open file is selected through menu bar items. Used largely for debugging view without need for GitHub repositories.
     * Can be removed once in-appp automatic GitHub cloning is properly implemented.
     * */
    @FXML
    public void onOpenFileClick () {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        rootFolder = directoryChooser.showDialog(MainApplication.launchStage);
        filePanelLabel.setText("Files - " + rootFolder.getName());
        fileTreeLabel.setVisible(false);
        fileProgressSpinner.setVisible(true);
        reloadFileTree();
        fileProgressSpinner.setVisible(false);
        fileTree.setVisible(true);
    }
    /** Reloads file tree view once repo is selected.*/
    public void reloadFileTree () {
        fileTree.getItems().clear();
        subFilePaths.clear();
        addToFileTree(rootFolder.listFiles(),0);
    }
    /** Adds given array of files to fileTree object.
     *
     * @param files List of all files on given sub-folder level that are to be added to the fileView
     * @param depth Numerical value of current sub-folder depth, to be used by getIndentedFilename func
     * */
    public void addToFileTree (File [] files ,int depth){
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().contains(".git")) {
                    continue;
                }
                fileTree.getItems().add(getIndentedFilename(file,depth));
                subFilePaths.add(file.getAbsolutePath());
                addToFileTree(file.listFiles(), depth + 1); // Recursive
            } else {
                subFilePaths.add(file.getAbsolutePath());
                fileTree.getItems().add(getIndentedFilename(file,depth));
            }
        }
    }
    /**
     * @return File name with additional whitespace at the front, depicting the depth of the file/folder from the root folder/repository
     * @param f Current file to be processed
     * @param depth Depth of the current file from the root folder
     * */
    private String getIndentedFilename (File f, int depth) {
        String ret = "";
        for (int i = 0; i < depth; i++) {
            ret += "    ";
        }

        return ret + f.getName();
    }
    /**
     * Retrives selectedFile from fileView based on the index of the selected file.
     *
     * */
    @FXML
    public void getSelectedFile () throws IOException {
        System.gc();
        int fileIndex = fileTree.getSelectionModel().getSelectedIndex();
        String selectedFilePath = subFilePaths.get(fileIndex);
        Path selectedFile = Paths.get(selectedFilePath);
        String fileContents = "";
        try {
           fileContents = Files.readString(selectedFile, StandardCharsets.US_ASCII);
        }
        catch (Exception e) {
            //TODO: State in UI that valid file was not selected
        }
        selectedFileField.setText(fileContents);
        selectedFileContents = fileContents;
        System.out.println(isValid(fileContents));
    }

    /**
     * @return Boolean whether json follows valid JSON syntax
     * @param json JSON to be validated
     * */
    private boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                centerLowerLabel.setText("Invalid JSON");
                centerLowerLabel.setTextFill(Paint.valueOf("#ff0000"));
                return false;
            }
        }
        centerLowerLabel.setText("Valid JSON");
        centerLowerLabel.setTextFill(Paint.valueOf("#00FF00"));


        return true;
    }
    /** Adds user's GitHub organizations to dropdown from MainApplication.appProperties */
    private void updateOrgsDropdown() {
        orgComboBox.getItems().addAll(MainApplication.appProperties.getOrgNames());
    }
    /** Adds user's GitHub organizations for selected repository to dropdown */
    @FXML
    private void updateReposDropdown() {
        Object currentOrg = orgComboBox.getValue();
        System.out.println("Current org: " + currentOrg);
        if (currentOrg == null || currentOrg == "") {
            return;
        }
        else {
            ArrayList<Repository> repos = MainApplication.appProperties.getOrg(currentOrg.toString()).getRepos();
            for (Repository r : repos) {
                repoComboBox.getItems().add(r.getName());

            }
        }

        repoComboBox.getItems().add("vizio-scfs-deployments");
    }
    /**
     * Event specific function run when repository is selected from dropdown. Sets fileTreeLabel and fileProgressSpinner visibility to false and reloadsFileTree
     * */
    @FXML
    private void onSelectRepo () {
        Object currentRepo = repoComboBox.getValue();
        Object currentOrg = orgComboBox.getValue();
        if (currentRepo == null) {
            return;
        }
        //TODO: Implement service to automatically clone repository when selected using OAuth token
//        GithubService.cloneRepository(
//                MainApplication.appProperties.getOrg(currentOrg.toString())
//                        .getRepo(currentRepo.toString())
//        );
        rootFolder = new File("src/main/resources/gitRepos/" + currentRepo.toString());
        fileTreeLabel.setVisible(false);
        fileProgressSpinner.setVisible(true);
        reloadFileTree();
        fileProgressSpinner.setVisible(false);
        fileTree.setVisible(true);
    }
}
