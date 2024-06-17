package com.main.library.pages.admin.book;

import com.main.library.config.Config;
import com.main.library.dto.book.BookDTO;
import com.main.library.pages.admin.AdminMenu;
import com.main.library.pages.book.BookList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class AddBook extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        // Label List Books
        Label listBooksLabel = new Label("Add Book");
        listBooksLabel.setFont(new Font("System Bold", 36));
        AnchorPane.setLeftAnchor(listBooksLabel, 264.0);
        AnchorPane.setTopAnchor(listBooksLabel, 34.0);

        // Back Button
        Button backButton = new Button("Back");
        backButton.setFont(new Font("System Bold", 14));
        backButton.setPrefSize(119, 40);
        AnchorPane.setLeftAnchor(backButton, 14.0);
        AnchorPane.setTopAnchor(backButton, 446.0);

        backButton.setOnAction(actionEvent -> {
            AdminMenu adminMenu = new AdminMenu();
            adminMenu.start(primaryStage);
        });

        // Title Label
        Label titleLabel = new Label("Title");
        titleLabel.setFont(new Font(18));
        AnchorPane.setLeftAnchor(titleLabel, 74.0);
        AnchorPane.setTopAnchor(titleLabel, 113.0);

        // Author Label
        Label authorLabel = new Label("Author");
        authorLabel.setFont(new Font(18));
        AnchorPane.setLeftAnchor(authorLabel, 74.0);
        AnchorPane.setTopAnchor(authorLabel, 192.0);

        // Category Label
        Label categoryLabel = new Label("Category");
        categoryLabel.setFont(new Font(18));
        AnchorPane.setLeftAnchor(categoryLabel, 419.0);
        AnchorPane.setTopAnchor(categoryLabel, 113.0);

        // Stock Label
        Label stockLabel = new Label("Stock");
        stockLabel.setFont(new Font(18));
        AnchorPane.setLeftAnchor(stockLabel, 421.0);
        AnchorPane.setTopAnchor(stockLabel, 192.0);

        // Title TextField
        TextField titleTextField = new TextField();
        titleTextField.setPrefSize(257, 35);
        AnchorPane.setLeftAnchor(titleTextField, 82.0);
        AnchorPane.setTopAnchor(titleTextField, 140.0);

        // Author TextField
        TextField authorTextField = new TextField();
        authorTextField.setPrefSize(257, 35);
        AnchorPane.setLeftAnchor(authorTextField, 82.0);
        AnchorPane.setTopAnchor(authorTextField, 219.0);

        // Category ComboBox
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPrefSize(201, 35);
        AnchorPane.setLeftAnchor(categoryComboBox, 430.0);
        AnchorPane.setTopAnchor(categoryComboBox, 140.0);
        categoryComboBox.getItems().addAll("History", "Fiction", "Text");
        categoryComboBox.getSelectionModel().selectFirst();

        // Stock TextField
        TextField stockTextField = new TextField();
        stockTextField.setPrefSize(201, 35);
        AnchorPane.setLeftAnchor(stockTextField, 430.0);
        AnchorPane.setTopAnchor(stockTextField, 219.0);

        // Error Label
        Label errorLabel = new Label();
        errorLabel.setFont(new Font(14));
        errorLabel.setTextFill(Color.RED);
        errorLabel.setPrefSize(537, 20);
        AnchorPane.setLeftAnchor(errorLabel, 88.0);
        AnchorPane.setTopAnchor(errorLabel, 384.0);

        // Submit Button
        Button submitButton = new Button("Submit");
        submitButton.setFont(new Font("System Bold", 18));
        submitButton.setPrefSize(165, 53);
        AnchorPane.setLeftAnchor(submitButton, 274.0);
        AnchorPane.setTopAnchor(submitButton, 302.0);

        submitButton.setOnAction(actionEvent -> {
            errorLabel.setText("");
            String title = titleTextField.getText();
            String author = authorTextField.getText();
            String category = categoryComboBox.getSelectionModel().getSelectedItem();

            int stock;
            try {
                stock = Integer.parseInt(stockTextField.getText());
            }catch (Exception e) {
                errorLabel.setText("Stock must be digits");
                return;
            }

            if(title.isEmpty()){
                errorLabel.setText("Title empty");
                return;
            }

            if(author.isEmpty()) {
                errorLabel.setText("Author empty");
                return;
            }

            if(category.isEmpty()) {
                errorLabel.setText("Stock empty");
                return;
            }

            BookDTO book = new BookDTO(UUID.randomUUID().toString(), title, author, category, stock);
            insertBook(book);

            BookList bookList = new BookList();
            bookList.start(primaryStage);
        });

        // Adding all components to the root pane
        root.getChildren().addAll(
                listBooksLabel, backButton, titleLabel, authorLabel,
                categoryLabel, stockLabel, titleTextField, authorTextField,
                categoryComboBox, stockTextField, errorLabel, submitButton
        );

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Book List");
        primaryStage.show();
    }

    private static void insertBook(BookDTO book) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "INSERT INTO books (id, title, author, category, stock) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, book.getId());
                pstmt.setString(2, book.getTitle());
                pstmt.setString(3, book.getAuthor());
                pstmt.setString(4, book.getCategory());
                pstmt.setInt(5, book.getStock());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
