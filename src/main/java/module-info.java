module com.mycompany.mysqlc {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires java.base;
    
    opens com.mycompany.mysqlc to javafx.fxml;
    exports com.mycompany.mysqlc;
}
