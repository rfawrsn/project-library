package com.main.library.pages.admin.student;

import com.main.library.config.Config;
import com.main.library.dto.student.StudentDTO;
import com.main.library.pages.admin.AdminMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AddStudent extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        Label titleLabel = new Label("Add Student");
        titleLabel.setLayoutX(264.0);
        titleLabel.setLayoutY(34.0);
        titleLabel.setFont(new Font("System Bold", 36.0));

        Button backButton = new Button("Back");
        backButton.setLayoutX(14.0);
        backButton.setLayoutY(446.0);
        backButton.setPrefSize(119.0, 40.0);
        backButton.setFont(new Font("System Bold", 14.0));

        backButton.setOnAction(actionEvent -> {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.start(primaryStage);
        });

        Label nameLabel = new Label("Name");
        nameLabel.setLayoutX(74.0);
        nameLabel.setLayoutY(113.0);
        nameLabel.setFont(new Font(18.0));

        Label nimLabel = new Label("NIM");
        nimLabel.setLayoutX(74.0);
        nimLabel.setLayoutY(192.0);
        nimLabel.setFont(new Font(18.0));

        Label facultyLabel = new Label("Faculty");
        facultyLabel.setLayoutX(376.0);
        facultyLabel.setLayoutY(113.0);
        facultyLabel.setFont(new Font(18.0));

        Label majorLabel = new Label("Major");
        majorLabel.setLayoutX(378.0);
        majorLabel.setLayoutY(192.0);
        majorLabel.setFont(new Font(18.0));

        TextField nameField = new TextField();
        nameField.setLayoutX(82.0);
        nameField.setLayoutY(140.0);
        nameField.setPrefSize(257.0, 35.0);

        TextField nimField = new TextField();
        nimField.setLayoutX(82.0);
        nimField.setLayoutY(219.0);
        nimField.setPrefSize(257.0, 35.0);

        TextField facultyField = new TextField();
        facultyField.setLayoutX(387.0);
        facultyField.setLayoutY(140.0);
        facultyField.setPrefSize(257.0, 35.0);

        TextField majorField = new TextField();
        majorField.setLayoutX(387.0);
        majorField.setLayoutY(219.0);
        majorField.setPrefSize(257.0, 35.0);

        Label errorLabel = new Label();
        errorLabel.setLayoutX(88.0);
        errorLabel.setLayoutY(384.0);
        errorLabel.setPrefSize(537.0, 20.0);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setFont(new Font(14.0));

        Button submitButton = new Button("Submit");
        submitButton.setLayoutX(274.0);
        submitButton.setLayoutY(302.0);
        submitButton.setPrefSize(165.0, 53.0);
        submitButton.setFont(new Font("System Bold", 18.0));

        submitButton.setOnAction(actionEvent -> {
            String name = nameField.getText();
            String nim = nimField.getText();
            String faculty = facultyField.getText();
            String major = majorField.getText();

            if(name.isEmpty()) {
                errorLabel.setText("Name empty");
                return;
            }

            if(nim.isEmpty()) {
                errorLabel.setText("NIM empty");
                return;
            }

            if(faculty.isEmpty()) {
                errorLabel.setText("Faculty empty");
                return;
            }

            if(major.isEmpty()) {
                errorLabel.setText("Major empty");
            }

            if(nim.length() != 15) {
                errorLabel.setText("NIM must be 15 digits");
                return;
            }

            if(!nim.matches("\\d+")) {
                errorLabel.setText("NIM must be digits");
                return;
            }

            StudentDTO student = new StudentDTO(UUID.randomUUID().toString(), name, nim, faculty, major, BCrypt.hashpw(generatePassword(nim, major), BCrypt.gensalt()));
            insertStudent(student);

            StudentList studentList = new StudentList();
            studentList.start(primaryStage);
        });

        root.getChildren().addAll(
                titleLabel, backButton, nameLabel, nimLabel, facultyLabel, majorLabel,
                nameField, nimField, facultyField, majorField, errorLabel, submitButton
        );

        Scene scene = new Scene(root);
        primaryStage.setTitle("Add Student");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static void insertStudent(StudentDTO student) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "INSERT INTO students (id, name, nim, faculty, major, password) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, student.getId());
                pstmt.setString(2, student.getName());
                pstmt.setString(3, student.getNim());
                pstmt.setString(4, student.getFaculty());
                pstmt.setString(5, student.getMajor());
                pstmt.setString(6, student.getPassword());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String generatePassword(String nim, String major) {
        String lastSixDigits = nim.substring(nim.length() - 6);
        String firstThreeLetters = major.substring(0, 3).toLowerCase();

        return lastSixDigits + firstThreeLetters;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
