module com.example.chaotopia {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.chaotopia to javafx.fxml;
    exports com.example.chaotopia;
}