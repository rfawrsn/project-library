package com.main.library.pages.admin;

import com.main.library.pages.admin.book.AddBook;
import com.main.library.pages.admin.student.StudentList;
import com.main.library.pages.admin.student.AddStudent;
import com.main.library.pages.book.BookList;
import com.main.library.pages.book.BorrowedBookList;
import com.main.library.pages.latefee.LateFee;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AdminMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the AnchorPane
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        // Create the Labels
        Label titleLabel = new Label("Admin Menu");
        titleLabel.setFont(new Font("System Bold", 36));
        titleLabel.setLayoutX(239);
        titleLabel.setLayoutY(54);

        // Create the Buttons
        Button logoutButton = new Button("Logout");
        logoutButton.setFont(new Font("System Bold", 14));
        logoutButton.setLayoutX(14);
        logoutButton.setLayoutY(446);
        logoutButton.setPrefSize(119, 40);

        logoutButton.setOnAction(actionEvent -> {
            AdminLogin adminLogin = new AdminLogin();
            adminLogin.start(primaryStage);
        });

        Button listStudentButton = new Button("List Student");
        listStudentButton.setFont(new Font("System Bold", 18));
        listStudentButton.setLayoutX(103);
        listStudentButton.setLayoutY(150);
        listStudentButton.setPrefSize(212, 76);

        listStudentButton.setOnAction(actionEvent -> {
            StudentList studentList = new StudentList();
            studentList.start(primaryStage);
        });

        Button listBookButton = new Button("List Book");
        listBookButton.setFont(new Font("System Bold", 18));
        listBookButton.setLayoutX(103);
        listBookButton.setLayoutY(250);
        listBookButton.setPrefSize(212, 76);

        listBookButton.setOnAction(actionEvent -> {
            BookList bookList = new BookList();
            try {
                bookList.start(primaryStage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Button addStudentButton = new Button("Add Student");
        addStudentButton.setFont(new Font("System Bold", 18));
        addStudentButton.setLayoutX(393);
        addStudentButton.setLayoutY(150);
        addStudentButton.setPrefSize(212, 76);

        addStudentButton.setOnAction(actionEvent -> {
            AddStudent addStudent = new AddStudent();
            addStudent.start(primaryStage);
        });

        Button addBookButton = new Button("Add Book");
        addBookButton.setFont(new Font("System Bold", 18));
        addBookButton.setLayoutX(393);
        addBookButton.setLayoutY(250);
        addBookButton.setPrefSize(212, 76);

        addBookButton.setOnAction(actionEvent -> {
            AddBook addBook = new AddBook();
            addBook.start(primaryStage);
        });

        Button lateFeesButton = new Button("Late Fees");
        lateFeesButton.setFont(new Font("System Bold", 18));
        lateFeesButton.setLayoutX(393);
        lateFeesButton.setLayoutY(350);
        lateFeesButton.setPrefSize(212, 76);

        lateFeesButton.setOnAction(actionEvent -> {
            LateFee lateFee = new LateFee();
            lateFee.start(primaryStage);
        });

        Button borrowedBookListButton = new Button("Borrowed Books");
        borrowedBookListButton.setFont(new Font("System Bold", 18));
        borrowedBookListButton.setLayoutX(103);
        borrowedBookListButton.setLayoutY(350);
        borrowedBookListButton.setPrefSize(212, 76);

        borrowedBookListButton.setOnAction(actionEvent -> {
            BorrowedBookList borrowedBooks = new BorrowedBookList();
            borrowedBooks.start(primaryStage);
        });


        // Add all components to the AnchorPane
        root.getChildren().addAll(titleLabel, logoutButton, listStudentButton, listBookButton, addStudentButton, addBookButton, lateFeesButton, borrowedBookListButton);

        // Create the Scene and set it on the Stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Admin Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
