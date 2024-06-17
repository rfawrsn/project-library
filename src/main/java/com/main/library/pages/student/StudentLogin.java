package com.main.library.pages.student;

import com.main.library.pages.admin.AdminLogin;
import com.main.library.dto.student.StudentDTO;
import com.main.library.session.StudentSession;
import com.main.library.config.Config;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

public class StudentLogin extends Application {
    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        Label titleLabel = new Label("Login Student");
        titleLabel.setFont(new Font("System Bold", 36));
        titleLabel.setLayoutX(231);
        titleLabel.setLayoutY(55);

        Label nimLabel = new Label("NIM");
        nimLabel.setFont(new Font(18));
        nimLabel.setLayoutX(136);
        nimLabel.setLayoutY(126);

        Label passwordLabel = new Label("Password");
        passwordLabel.setFont(new Font(18));
        passwordLabel.setLayoutX(139);
        passwordLabel.setLayoutY(212);

        Label errorLabel = new Label();
        errorLabel.setFont(new Font(18));
        errorLabel.setLayoutX(157);
        errorLabel.setLayoutY(395);
        errorLabel.setPrefSize(381, 17);
        errorLabel.setTextFill(Color.RED);

        // Create the TextFields
        TextField nimTextField = new TextField();
        nimTextField.setLayoutX(163);
        nimTextField.setLayoutY(154);
        nimTextField.setPrefSize(381, 40);

        PasswordField passwordField = new PasswordField();
        passwordField.setLayoutX(163);
        passwordField.setLayoutY(245);
        passwordField.setPrefSize(381, 40);

        // Create the Buttons
        Button loginButton = new Button("Login");
        loginButton.setFont(new Font("System Bold", 18));
        loginButton.setLayoutX(279);
        loginButton.setLayoutY(318);
        loginButton.setPrefSize(149, 61);

        loginButton.setOnAction(actionEvent -> {
            errorLabel.setText("");
            String nim = nimTextField.getText();
            String password = passwordField.getText();

            if(nim.isEmpty()) {
                errorLabel.setText("NIM empty.");
                return;
            }

            if(password.isEmpty()) {
                errorLabel.setText("Password empty.");
                return;
            }

            try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
                if (conn != null) {
                    String studentAuthenticationMessage = authenticateStudent(conn, nim, password);

                    if (!studentAuthenticationMessage.equalsIgnoreCase("success")) {
                        errorLabel.setText(studentAuthenticationMessage);
                        return;
                    }

                    StudentMenu studentMenu = new StudentMenu();
                    studentMenu.start(primaryStage);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });

        Button adminLoginButton = new Button("Login Admin");
        adminLoginButton.setFont(new Font("System Bold", 14));
        adminLoginButton.setLayoutX(14);
        adminLoginButton.setLayoutY(446);
        adminLoginButton.setPrefSize(119, 40);

        adminLoginButton.setOnAction(actionEvent -> {
            AdminLogin adminLogin = new AdminLogin();
            adminLogin.start(primaryStage);
        });

        root.getChildren().addAll(titleLabel, nimLabel, passwordLabel, nimTextField, passwordField, loginButton, adminLoginButton, errorLabel);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Login Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String authenticateStudent(Connection conn, String nim, String password) throws SQLException {
        String getStudentQuery = "SELECT * FROM students WHERE nim = ?";
        PreparedStatement pstmt = conn.prepareStatement(getStudentQuery);

        try {
            pstmt.setString(1, nim);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return "Student not found!";
            }

            String hashedPassword = rs.getString("password");
            if (!BCrypt.checkpw(password, hashedPassword)) {
                return "Password incorrect.";
            }

            StudentDTO student = new StudentDTO(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("nim"),
                    rs.getString("faculty"),
                    rs.getString("major"),
                    rs.getString("password")
            );

            // Simpan data mahasiswa ke session
            StudentSession.getInstance().setStudent(student);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "success";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
