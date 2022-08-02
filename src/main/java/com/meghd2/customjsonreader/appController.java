package com.meghd2.customjsonreader;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.*;

public class appController implements Initializable {

    @FXML
    MenuItem openFile;
    @FXML
    TextArea selectedFileField;
    @FXML
    ListView fileTree;
    @FXML
    Label fileTreeLabel;
    @FXML
    ProgressIndicator fileProgressSpinner;
    @FXML
    Label filePanelLabel;
    private File rootFolder;
    private ArrayList<String> subFilePaths = new ArrayList<String>();


    @Override
    public void initialize (URL location, ResourceBundle resources) {
        if (rootFolder == null) {
            fileTree.setVisible(false);
            fileTreeLabel.setVisible(true);
            fileTreeLabel.setText("No Folder Selected");
        }
        fileTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
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
    public void reloadFileTree () {
        fileTree.getItems().clear();
        subFilePaths.clear();
        addToFileTree(rootFolder.listFiles(),0);
    }
    public void addToFileTree (File [] files ,int depth){
        for (File file : files) {
            if (file.isDirectory()) {
                fileTree.getItems().add(getIndentedFilename(file,depth));
                subFilePaths.add(file.getAbsolutePath());
                addToFileTree(file.listFiles(), depth + 1); // Calls same method again.
            } else {
                subFilePaths.add(file.getAbsolutePath());
                fileTree.getItems().add(getIndentedFilename(file,depth));
            }
        }
    }
    private String getIndentedFilename (File f, int depth) {
        String ret = "";
        for (int i = 0; i < depth; i++) {
            ret += "    ";
        }

        return ret + f.getName();
    }
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
            //TODO: State in UI that JSON was not selected
        }
        selectedFileField.setText(fileContents);
        System.out.println(isValid(fileContents));
    }
    private boolean isValid(String json) {
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            try {
                new JSONArray(json);
            } catch (JSONException ne) {
                return false;
            }
        }
        return true;
    }

}
