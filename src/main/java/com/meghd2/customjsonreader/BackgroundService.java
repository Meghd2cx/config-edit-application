package com.meghd2.customjsonreader;

public class BackgroundService implements Runnable{

    BackgroundServiceType type ;

    public BackgroundService (BackgroundServiceType type, String ... options) {
        this.type = type;
//        if (options.length == 1) {
//
//        }

    }

    @Override
    public void run() {
        switch (type) {
            case FETCHREPO -> fetchRepo();
            case VALIDATEJSON -> validateJSON();
            case SAVELOCAL -> saveLocal();
            default -> System.out.println("Not a valid background service");
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

