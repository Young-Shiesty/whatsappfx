module com.mcnz.sql.whatsappfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires java.sql;
    requires java.desktop;
    requires org.hibernate.orm.core;
    requires static lombok;
    requires jbcrypt;
    requires java.validation;

    opens com.mcnz.sql.whatsappfx.entity;
    opens com.mcnz.sql.whatsappfx to javafx.fxml;
    exports com.mcnz.sql.whatsappfx;
}