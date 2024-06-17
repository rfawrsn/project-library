package com.main.library.pages.book;

import com.main.library.session.AdminSession;
import com.main.library.dto.book.BookDTO;
import com.main.library.pages.admin.AdminMenu;
import com.main.library.pages.student.StudentMenu;
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
import java.util.ArrayList;
import java.util.UUID;

public class BookList extends Application {

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(700, 500);

        Label titleLabel = new Label("Book List");
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
        scrollPane.setPrefSize(639.0, 329.0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        // TableView setup
        TableView<BookDTO> tableView = new TableView<>();
        tableView.setPrefSize(639.0, 329.0);

        TableColumn<BookDTO, Number> indexColumn = new TableColumn<>("Num");
        indexColumn.setPrefWidth(40);
        indexColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(col.getValue()) + 1));
        TableColumn<BookDTO, String> column2 = new TableColumn<>("Title");
        column2.setPrefWidth(80);
        column2.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<BookDTO, String> column3 = new TableColumn<>("Author");
        column3.setPrefWidth(80);
        column3.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<BookDTO, String> column4 = new TableColumn<>("Category");
        column4.setPrefWidth(80);
        column4.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<BookDTO, Integer> column5 = new TableColumn<>("Stock");
        column5.setPrefWidth(80);
        column5.setCellValueFactory(new PropertyValueFactory<>("stock"));
        TableColumn<BookDTO, Void> borrowColumn = new TableColumn<>("Action");
        borrowColumn.setPrefWidth(80);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<BookDTO> bookListData = fetchBooksFromDatabase();
        tableView.setItems(bookListData);

        scrollPane.setContent(tableView);

        Label searchLabel = new Label("Search:");
        searchLabel.setLayoutX(319.0);
        searchLabel.setLayoutY(43.0);
        searchLabel.setFont(new Font(18.0));

        TextField searchField = new TextField();
        searchField.setLayoutX(395.0);
        searchField.setLayoutY(39.0);
        searchField.setPrefSize(193.0, 35.0);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<BookDTO> filteredData = FXCollections.observableArrayList();
            for (BookDTO book : bookListData) {
                if (book.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(newValue.toLowerCase()) ||
                        book.getCategory().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredData.add(book);
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

        Callback<TableColumn<BookDTO, Void>, TableCell<BookDTO, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<BookDTO, Void> call(final TableColumn<BookDTO, Void> param) {
                final TableCell<BookDTO, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Borrow");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            BookDTO book = getTableView().getItems().get(getIndex());
                            if (book.getStock() > 0) {
                                book.setStock(book.getStock() - 1);
                                updateBookStockInDatabase(book.getId(), book.getStock());
                                createBorrowedBookRecord(book.getId());
                                getTableView().refresh();

                                StudentMenu studentMenu = new StudentMenu();
                                studentMenu.start(primaryStage);
                            } else {
                                errorLabel.setText("Book out of stock.");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (StudentSession.getInstance().getStudent() != null) {
                            ArrayList<String> borrowedBookIds = fetchBorrowedBookIds();
                            if (empty || getTableView().getItems().get(getIndex()).getStock() <= 0 || borrowedBookIds.contains(getTableView().getItems().get(getIndex()).getId())) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        } else {
                            setGraphic(null);
                        }
                    }
                };
                return cell;
            }
        };

        borrowColumn.setCellFactory(cellFactory);

        if (StudentSession.getInstance().getStudent() != null) {
            tableView.getColumns().addAll(indexColumn, column2, column3, column4, column5, borrowColumn);
        } else if (AdminSession.getInstance().getUser() != null) {
            tableView.getColumns().addAll(indexColumn, column2, column3, column4, column5);
        }

        root.getChildren().addAll(titleLabel, backButton, scrollPane, searchLabel, searchField, errorLabel);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Book List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static ArrayList<String> fetchBorrowedBookIds() {
        ArrayList<String> borrowedBookIds = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "SELECT book_id FROM borrowed_books WHERE student_id = ? AND returned_at IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, StudentSession.getInstance().getStudent().getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    borrowedBookIds.add(rs.getString("book_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowedBookIds;
    }

    private ObservableList<BookDTO> fetchBooksFromDatabase() {
        ObservableList<BookDTO> bookListData = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String category = rs.getString("category");
                int stock = rs.getInt("stock");
                bookListData.add(new BookDTO(id, title, author, category, stock));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookListData;
    }

    private void updateBookStockInDatabase(String bookId, int newStock) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "UPDATE books SET stock = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, newStock);
                pstmt.setString(2, bookId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createBorrowedBookRecord(String bookId)
    {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "INSERT INTO borrowed_books (id, book_id, student_id, deadline_at) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, UUID.randomUUID().toString());
                pstmt.setString(2, bookId);
                pstmt.setString(3, StudentSession.getInstance().getStudent().getId());
                java.time.LocalDateTime deadline = java.time.LocalDateTime.now().plusDays(7);
                String deadlineStr = deadline.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                pstmt.setString(4, deadlineStr);
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
