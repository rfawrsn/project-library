package com.main.library.pages.student;

import com.main.library.pages.book.BookList;
import com.main.library.pages.book.BorrowedBookList;
import com.main.library.pages.latefee.LateFee;
import com.main.library.session.StudentSession;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class StudentMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        Label titleLabel = new Label("Student Menu");
        titleLabel.setLayoutX(243.0);
        titleLabel.setLayoutY(60.0);
        titleLabel.setFont(new Font("System Bold", 36.0));

        Button logoutButton = new Button("Logout");
        logoutButton.setLayoutX(14.0);
        logoutButton.setLayoutY(446.0);
        logoutButton.setPrefSize(119.0, 40.0);
        logoutButton.setFont(new Font("System Bold", 14.0));

        logoutButton.setOnAction(actionEvent -> {
            StudentLogin studentLogin = new StudentLogin();
            StudentSession.getInstance().purgeSession();
            studentLogin.start(primaryStage);
        });

        Button borrowBookButton = new Button("Borrow Book");
        borrowBookButton.setLayoutX(275.0);
        borrowBookButton.setLayoutY(150.0);
        borrowBookButton.setPrefSize(177.0, 68.0);
        borrowBookButton.setFont(new Font("System Bold", 18.0));

        borrowBookButton.setOnAction(actionEvent -> {
            BookList bookList = new BookList();
            bookList.start(primaryStage);
        });

        Button borrowedBookButton = new Button("Borrowed Book");
        borrowedBookButton.setLayoutX(275.0);
        borrowedBookButton.setLayoutY(250.0);
        borrowedBookButton.setPrefSize(177.0, 68.0);
        borrowedBookButton.setFont(new Font("System Bold", 18.0));

        borrowedBookButton.setOnAction(actionEvent -> {
            BorrowedBookList borrowed = new BorrowedBookList();
            borrowed.start(primaryStage);
        });

        Button lateFeesButton = new Button("Late Fees");
        lateFeesButton.setLayoutX(275.0);
        lateFeesButton.setLayoutY(350.0);
        lateFeesButton.setPrefSize(177.0, 68.0);
        lateFeesButton.setFont(new Font("System Bold", 18.0));

        lateFeesButton.setOnAction(actionEvent -> {
            LateFee lateFee = new LateFee();
            lateFee.start(primaryStage);
        });

        root.getChildren().addAll(titleLabel, logoutButton, borrowBookButton, borrowedBookButton, lateFeesButton);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Student Menu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
