package model;

import java.io.File;

public class LoadedFile {

    private String repo;
    private String branch;
    private File file;



    public LoadedFile(String repo, String branch, File file) {
        this.repo = repo;
        this.branch = branch;
        this.file = file;
    }




}
