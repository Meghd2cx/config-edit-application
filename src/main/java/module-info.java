module com.meghd2.customjsonreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens com.meghd2.customjsonreader to javafx.fxml;
    exports com.meghd2.customjsonreader;
}