module com.meghd2.customjsonreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires org.json;
    requires oauth2.useragent;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires com.google.gson;
    requires MaterialFX;
    requires VirtualizedFX;
    requires kotlin.stdlib;
    requires org.eclipse.jgit;
    requires jdk.httpserver;


    opens com.meghd2.customjsonreader to javafx.fxml;
    opens model to com.google.gson;

    exports com.meghd2.customjsonreader;
}