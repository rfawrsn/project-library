package com.main.library.pages.admin;

import com.main.library.dto.admin.AdminDTO;
import com.main.library.session.AdminSession;
import com.main.library.pages.student.StudentLogin;
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

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AdminLogin extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        // Create the Labels
        Label titleLabel = new Label("Login Admin");
        titleLabel.setFont(new Font("System Bold", 36));
        titleLabel.setLayoutX(239);
        titleLabel.setLayoutY(54);

        Label usernameLabel = new Label("Username");
        usernameLabel.setFont(new Font(18));
        usernameLabel.setLayoutX(136);
        usernameLabel.setLayoutY(126);

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
        TextField usernameTextField = new TextField();
        usernameTextField.setLayoutX(163);
        usernameTextField.setLayoutY(154);
        usernameTextField.setPrefSize(381, 40);

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
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            if(username.isEmpty()) {
                errorLabel.setText("Username empty");
                return;
            }

            if(password.isEmpty()) {
                errorLabel.setText("Password empty");
                return;
            }

            try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
                if (conn != null) {
                    String adminAuthenticationMessage = authenticateAdmin(conn, username, password);

                    if (!adminAuthenticationMessage.equalsIgnoreCase("success")) {
                        errorLabel.setText(adminAuthenticationMessage);
                        return;
                    }

                    AdminMenu adminMenu = new AdminMenu();
                    adminMenu.start(primaryStage);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        });

        Button studentLoginButton = new Button("Login Student");
        studentLoginButton.setFont(new Font("System Bold", 14));
        studentLoginButton.setLayoutX(14);
        studentLoginButton.setLayoutY(446);
        studentLoginButton.setPrefSize(119, 40);

        studentLoginButton.setOnAction(actionEvent -> {
            StudentLogin studentLogin = new StudentLogin();
            studentLogin.start(primaryStage);
        });

        // Add all components to the AnchorPane
        root.getChildren().addAll(titleLabel, usernameLabel, passwordLabel, usernameTextField, passwordField, loginButton, studentLoginButton, errorLabel);

        // Create the Scene and set it on the Stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login Form");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String authenticateAdmin(Connection conn, String username, String password) throws SQLException {
        String getStudentQuery = "SELECT * FROM users WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(getStudentQuery);

        try {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                return "Credential not found!";
            }

            String hashedPassword = rs.getString("password");
            if (!BCrypt.checkpw(password, hashedPassword)) {
                return "Password incorrect.";
            }

            AdminDTO user = new AdminDTO(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("username")
            );

            // Simpan data admin ke session
            AdminSession.getInstance().setAdmin(user);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return "success";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
