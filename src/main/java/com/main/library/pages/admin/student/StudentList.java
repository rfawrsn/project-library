package com.main.library.pages.admin.student;

import com.main.library.dto.admin.student.StudentListDTO;
import com.main.library.config.Config;
import com.main.library.pages.admin.AdminMenu;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class StudentList extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        // Create the Label
        Label titleLabel = new Label("List Student");
        titleLabel.setFont(new Font("System Bold", 36));
        titleLabel.setLayoutX(14.0);
        titleLabel.setLayoutY(16.0);

        // Create the Back Button
        Button backButton = new Button("Back");
        backButton.setFont(new Font("System Bold", 14));
        backButton.setLayoutX(14);
        backButton.setLayoutY(446);
        backButton.setPrefSize(119, 40);

        backButton.setOnAction(actionEvent -> {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.start(primaryStage);
        });

//      Create the TableView and its columns
        TableView<StudentListDTO> tableView = new TableView<>();
        tableView.setPrefSize(638, 322);

        TableColumn<StudentListDTO, Number> indexColumn = new TableColumn<>("Num");
        indexColumn.setPrefWidth(40);
        indexColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(col.getValue()) + 1));
        TableColumn<StudentListDTO, String> column1 = new TableColumn<>("Name");
        column1.setPrefWidth(140);
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<StudentListDTO, String> column2 = new TableColumn<>("NIM");
        column2.setPrefWidth(80);
        column2.setCellValueFactory(new PropertyValueFactory<>("nim"));
        TableColumn<StudentListDTO, String> column3 = new TableColumn<>("Faculty");
        column3.setPrefWidth(80);
        column3.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        TableColumn<StudentListDTO, String> column4 = new TableColumn<>("Major");
        column4.setPrefWidth(80);
        column4.setCellValueFactory(new PropertyValueFactory<>("major"));

        tableView.getColumns().addAll(indexColumn, column1, column2, column3, column4);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<StudentListDTO> studentData = fetchStudents();
        tableView.setItems(studentData);

//      Create the ScrollPane
        ScrollPane scrollPane = new ScrollPane(tableView);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setLayoutX(31);
        scrollPane.setLayoutY(111);
        scrollPane.setPrefSize(638, 322);

        Label searchLabel = new Label("Search:");
        searchLabel.setLayoutX(319.0);
        searchLabel.setLayoutY(43.0);
        searchLabel.setFont(new Font(18.0));

        TextField searchField = new TextField();
        searchField.setLayoutX(395.0);
        searchField.setLayoutY(39.0);
        searchField.setPrefSize(193.0, 35.0);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<StudentListDTO> filteredData = FXCollections.observableArrayList();
            for (StudentListDTO student : studentData) {
                if (student.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                        student.getNim().toLowerCase().contains(newValue.toLowerCase()) ||
                        student.getFaculty().toLowerCase().contains(newValue.toLowerCase()) ||
                        student.getMajor().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredData.add(student);
                }
            }
            tableView.setItems(filteredData);
            tableView.refresh();
        });

//      Add all components to the AnchorPane
        root.getChildren().addAll(titleLabel, backButton, scrollPane, searchLabel, searchField);

        // Create the Scene and set it on the Stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("List Student");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<StudentListDTO> fetchStudents() {
        ObservableList<StudentListDTO> studentBorrowedBookData = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "SELECT * FROM students";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("name");
                    String nim = rs.getString("nim");
                    String faculty = rs.getString("faculty");
                    String major = rs.getString("major");

                    studentBorrowedBookData.add(new StudentListDTO(name, nim, faculty, major));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentBorrowedBookData;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
