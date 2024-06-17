package com.main.library.pages.book;

import com.main.library.dto.student.StudentBorrowedBookDTO;
import com.main.library.pages.admin.AdminMenu;
import com.main.library.pages.student.StudentMenu;
import com.main.library.session.AdminSession;
import com.main.library.session.StudentSession;
import com.main.library.config.Config;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.*;

public class BorrowedBookList extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(800, 500);

        Label titleLabel = new Label("Borrowed Book");
        titleLabel.setLayoutX(14.0);
        titleLabel.setLayoutY(16.0);
        titleLabel.setFont(new Font("System Bold", 36.0));

        Button backButton = new Button("Back");
        backButton.setLayoutX(14.0);
        backButton.setLayoutY(446.0);
        backButton.setPrefSize(119.0, 40.0);
        backButton.setFont(new Font("System Bold", 14.0));

        backButton.setOnAction(actionEvent -> {
            if (StudentSession.getInstance().getStudent() != null) {
                StudentMenu studentMenu = new StudentMenu();
                studentMenu.start(primaryStage);
            } else if (AdminSession.getInstance().getUser() != null) {
                AdminMenu adminMenu = new AdminMenu();
                adminMenu.start(primaryStage);
            }
        });

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setLayoutX(33.0);
        scrollPane.setLayoutY(102.0);
        scrollPane.setPrefSize(699.0, 329.0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        TableView<StudentBorrowedBookDTO> tableView = new TableView<>();
        tableView.setPrefSize(699.0, 329.0);

        TableColumn<StudentBorrowedBookDTO, Number> indexColumn = new TableColumn<>("Num");
        indexColumn.setPrefWidth(60);
        indexColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(col.getValue()) + 1));
        TableColumn<StudentBorrowedBookDTO, String> column2 = new TableColumn<>("Book Title");
        column2.setPrefWidth(80);
        column2.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<StudentBorrowedBookDTO, String> column3 = new TableColumn<>("Borrowed At");
        column3.setPrefWidth(80);
        column3.setCellValueFactory(new PropertyValueFactory<>("borrowedDate"));
        TableColumn<StudentBorrowedBookDTO, String> column4 = new TableColumn<>("Deadline Date");
        column4.setPrefWidth(80);
        column4.setCellValueFactory(new PropertyValueFactory<>("deadlineDate"));
        TableColumn<StudentBorrowedBookDTO, String> column5 = new TableColumn<>("Status");
        column5.setPrefWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<StudentBorrowedBookDTO, Void> borrowColumn = new TableColumn<>("Action");
        borrowColumn.setPrefWidth(100);
        TableColumn<StudentBorrowedBookDTO, Void> studentNimColumn = new TableColumn<>("Student NIM");
        studentNimColumn.setPrefWidth(80);
        studentNimColumn.setCellValueFactory(new PropertyValueFactory<>("studentNim"));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<StudentBorrowedBookDTO> studentBorrowedBookData = fetchStudentBorrowedBooks();
        tableView.setItems(studentBorrowedBookData);

        scrollPane.setContent(tableView);

        Label searchLabel = new Label("Search:");
        searchLabel.setLayoutX(349.0);
        searchLabel.setLayoutY(43.0);
        searchLabel.setFont(new Font(18.0));

        TextField searchField = new TextField();
        searchField.setLayoutX(435.0);
        searchField.setLayoutY(43.0);
        searchField.setPrefSize(193.0, 35.0);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<StudentBorrowedBookDTO> filteredData = FXCollections.observableArrayList();
            ObservableList<StudentBorrowedBookDTO> updatedStudentBorrowedBookData = fetchStudentBorrowedBooks();
            for (StudentBorrowedBookDTO borrowedBook : updatedStudentBorrowedBookData) {
                boolean matchesSearch = borrowedBook.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                        borrowedBook.getStatus().toLowerCase().contains(newValue.toLowerCase());

                if (AdminSession.getInstance().getUser() != null) {
                    matchesSearch = matchesSearch || borrowedBook.getStudentNim().toLowerCase().contains(newValue.toLowerCase());
                }

                if (matchesSearch) {
                    filteredData.add(borrowedBook);
                }
            }
            tableView.setItems(filteredData);
            tableView.refresh();
        });

        Label errorLabel = new Label();
        errorLabel.setLayoutX(395.0);
        errorLabel.setLayoutY(77.0);
        errorLabel.setPrefSize(292.0, 17.0);
        errorLabel.setTextFill(Color.RED);

        Callback<TableColumn<StudentBorrowedBookDTO, Void>, TableCell<StudentBorrowedBookDTO, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<StudentBorrowedBookDTO, Void> call(final TableColumn<StudentBorrowedBookDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Returned");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StudentBorrowedBookDTO borrowedBook = getTableView().getItems().get(getIndex());
                            if (!borrowedBook.isReturned()) {
                                updateBookStockInDatabase(borrowedBook.getBookId());
                                insertReturnDate(borrowedBook.getId());

                                int dateDiff = calculateDateDiff(borrowedBook.getId());
                                if (dateDiff > 0) {
                                    createFeeData(borrowedBook, dateDiff);
                                }

                                errorLabel.setText("Book returned successfully.");
                                tableView.setItems(fetchStudentBorrowedBooks());
                                tableView.refresh();
                            } else {
                                errorLabel.setText("Book is already returned.");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getItems().get(getIndex()).isReturned() || getTableView().getItems().get(getIndex()).getStatus().equals("Returned")) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        borrowColumn.setCellFactory(cellFactory);

        if (StudentSession.getInstance().getStudent() != null) {
            tableView.getColumns().addAll(indexColumn, column2, column3, column4, column5);
        } else if (AdminSession.getInstance().getUser() != null) {
            tableView.getColumns().addAll(indexColumn, studentNimColumn, column2, column3, column4, column5, borrowColumn);
        }


        root.getChildren().addAll(titleLabel, searchLabel, searchField, backButton, scrollPane, errorLabel);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Borrowed Books");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<StudentBorrowedBookDTO> fetchStudentBorrowedBooks() {
        ObservableList<StudentBorrowedBookDTO> studentBorrowedBookData = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {

            String query;
            if (StudentSession.getInstance().getStudent() != null) {
                query = "SELECT bb.id AS id, b.id AS bookId, b.title AS title, s.nim AS studentNim, bb.borrowed_at AS borrowedDate, " +
                        "bb.deadline_at AS deadlineDate, bb.returned_at AS returnedDate " +
                        "FROM borrowed_books bb " +
                        "JOIN books b ON bb.book_id = b.id " +
                        "JOIN students s ON bb.student_id = s.id " +
                        "WHERE bb.student_id = ?";
            } else if (AdminSession.getInstance().getUser() != null) {
                query = "SELECT bb.id AS id, b.id AS bookId, b.title AS title, s.nim AS studentNim, bb.borrowed_at AS borrowedDate, " +
                        "bb.deadline_at AS deadlineDate, bb.returned_at AS returnedDate " +
                        "FROM borrowed_books bb " +
                        "JOIN books b ON bb.book_id = b.id " +
                        "JOIN students s ON bb.student_id = s.id ";
                ;
            } else {
                return studentBorrowedBookData;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                if (StudentSession.getInstance().getStudent() != null) {
                    pstmt.setString(1, StudentSession.getInstance().getStudent().getId());
                }
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String id = rs.getString("id");
                    String bookId = rs.getString("bookId");
                    String studentNim = rs.getString("studentNim");
                    String title = rs.getString("title");
                    String borrowedDate = rs.getString("borrowedDate");
                    String deadlineDate = rs.getString("deadlineDate");
                    boolean isReturned = rs.getTimestamp("returnedDate") != null;
                    String status = isReturned ? "Returned" : "Not Returned";

                    studentBorrowedBookData.add(new StudentBorrowedBookDTO(id, bookId, studentNim, title, borrowedDate, deadlineDate, isReturned, status));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentBorrowedBookData;
    }


    private void insertReturnDate(String borrowedBookId) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "UPDATE borrowed_books SET returned_at = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                java.time.LocalDateTime deadline = java.time.LocalDateTime.now();
                String returnDate = deadline.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                pstmt.setString(1, returnDate);
                pstmt.setString(2, borrowedBookId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateBookStockInDatabase(String borrowedBookId) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            int currentStock = getReturnedBookStock(borrowedBookId);
            String updateStock = "UPDATE books SET stock = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateStock)) {
                pstmt.setInt(1, currentStock + 1);
                pstmt.setString(2, borrowedBookId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getReturnedBookStock(String borrowedBookId) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "SELECT stock FROM books WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, borrowedBookId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int calculateDateDiff(String borrowedBookId) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String dateDiffQuery = "SELECT julianday(returned_at) - julianday(deadline_at) AS diff FROM borrowed_books WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(dateDiffQuery)) {
                pstmt.setString(1, borrowedBookId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("diff");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void createFeeData(StudentBorrowedBookDTO borrowedBook, int dateDiff) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "INSERT INTO late_fees (id, borrowed_book_id, amount, note) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, java.util.UUID.randomUUID().toString());
                pstmt.setString(2, borrowedBook.getId());
                pstmt.setInt(3, dateDiff * 2000);
                pstmt.setString(4, "Late " + dateDiff + " day(s) for " + borrowedBook.getTitle());
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
