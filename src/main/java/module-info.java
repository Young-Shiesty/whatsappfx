module com.mcnz.sql.whatsappfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.mcnz.sql.whatsappfx to javafx.fxml;
    exports com.mcnz.sql.whatsappfx;
}