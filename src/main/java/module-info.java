module com.main.UMMLibrary {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens com.main.library to javafx.fxml;
    exports com.main.library.seeder;
    opens com.main.library.seeder to javafx.fxml;
    exports com.main.library.pages.admin;
    opens com.main.library.pages.admin to javafx.fxml;
    exports com.main.library.pages.student;
    opens com.main.library.pages.student to javafx.fxml;
    exports com.main.library.pages.book;
    opens com.main.library.pages.book to javafx.fxml;
    exports com.main.library.pages.latefee;
    opens com.main.library.pages.latefee to javafx.fxml;
    exports com.main.library.pages.admin.student;
    exports com.main.library.dto.admin;
    exports com.main.library.dto.admin.student;
    exports com.main.library.dto.book;
    exports com.main.library.dto.latefee;
    exports com.main.library.dto.student;
    exports com.main.library.session;
    exports com.main.library.pages.admin.book;
    opens com.main.library.pages.admin.book to javafx.fxml;
}