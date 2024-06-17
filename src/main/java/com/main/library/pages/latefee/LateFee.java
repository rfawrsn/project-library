package com.main.library.pages.latefee;

import com.main.library.session.AdminSession;
import com.main.library.pages.admin.AdminMenu;
import com.main.library.pages.student.StudentMenu;
import com.main.library.dto.latefee.LateFeeDTO;
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
import java.util.Objects;

public class LateFee extends Application {

    static final String PAID = "Paid";

    @Override
    public void start(Stage primaryStage) {
        AnchorPane root = new AnchorPane();
        root.setPrefSize(816, 500);

        Label titleLabel = new Label("Late Fees");
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
        scrollPane.setPrefSize(720, 329.0);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        TableView<LateFeeDTO> tableView = new TableView<>();
        tableView.setPrefSize(720, 329.0);

        TableColumn<LateFeeDTO, Number> indexColumn = new TableColumn<>("Num");
        indexColumn.setPrefWidth(60);
        indexColumn.setCellValueFactory(col -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(col.getValue()) + 1));
        TableColumn<LateFeeDTO, String> column2 = new TableColumn<>("Book Title");
        column2.setPrefWidth(80);
        column2.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        TableColumn<LateFeeDTO, String> column3 = new TableColumn<>("Amount");
        column3.setPrefWidth(80);
        column3.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<LateFeeDTO, String> column4 = new TableColumn<>("Status");
        column4.setPrefWidth(80);
        column4.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<LateFeeDTO, String> column5 = new TableColumn<>("Note");
        column5.setPrefWidth(80);
        column5.setCellValueFactory(new PropertyValueFactory<>("note"));
        TableColumn<LateFeeDTO, Void> changeStatusColumn = new TableColumn<>("Action");
        changeStatusColumn.setPrefWidth(100);
        TableColumn<LateFeeDTO, Void> studentNimColumn = new TableColumn<>("Student NIM");
        studentNimColumn.setPrefWidth(80);
        studentNimColumn.setCellValueFactory(new PropertyValueFactory<>("studentNim"));

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<LateFeeDTO> studentLateFrees = fetchStudentLateFees();
        tableView.setItems(studentLateFrees);

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
            ObservableList<LateFeeDTO> filteredData = FXCollections.observableArrayList();
            ObservableList<LateFeeDTO> updatedLateFeesData = fetchStudentLateFees();
            for (LateFeeDTO lateFees : updatedLateFeesData) {
                if (lateFees.getStudentNim().toLowerCase().contains(newValue.toLowerCase()) ||
                        lateFees.getBookTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                        lateFees.getStatus().toLowerCase().contains(newValue.toLowerCase())) {
                    filteredData.add(lateFees);
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

        Callback<TableColumn<LateFeeDTO, Void>, TableCell<LateFeeDTO, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<LateFeeDTO, Void> call(final TableColumn<LateFeeDTO, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Paid");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            LateFeeDTO lateFee = getTableView().getItems().get(getIndex());
                            if (!Objects.equals(lateFee.getStatus(), PAID)) {
                                changeStatusToPaid(lateFee.getId());

                                errorLabel.setText("Late fee is paid.");
                                tableView.setItems(fetchStudentLateFees());
                                tableView.refresh();;
                            } else {
                                errorLabel.setText("Fee is already paid!");
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableView().getItems().get(getIndex()).getStatus().equals(PAID)) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };

        changeStatusColumn.setCellFactory(cellFactory);

        if (StudentSession.getInstance().getStudent() != null) {
            tableView.getColumns().addAll(indexColumn, column2, column3, column4, column5);
        } else if (AdminSession.getInstance().getUser() != null) {
            tableView.getColumns().addAll(indexColumn, column2, studentNimColumn, column3, column4, column5, changeStatusColumn);
        }

        if (StudentSession.getInstance().getStudent() != null) {
            root.getChildren().addAll(titleLabel, backButton, scrollPane);
        } else if (AdminSession.getInstance().getUser() != null) {
            root.getChildren().addAll(titleLabel, backButton, scrollPane, searchLabel, searchField, errorLabel);
        }


        Scene scene = new Scene(root);
        primaryStage.setTitle("Borrowed Books");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ObservableList<LateFeeDTO> fetchStudentLateFees() {
        ObservableList<LateFeeDTO> studentLateFeesData = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {

            String query;
            if (StudentSession.getInstance().getStudent() != null) {
                query = "SELECT lf.id AS id, b.title AS bookTitle, s.nim AS studentNim, lf.amount AS amount, lf.status AS status, lf.note AS note " +
                        "FROM late_fees lf " +
                        "JOIN borrowed_books bb ON lf.borrowed_book_id = bb.id " +
                        "JOIN books b ON bb.book_id = b.id " +
                        "JOIN students s ON bb.student_id = s.id " +
                        "WHERE bb.student_id = ?";
            } else if (AdminSession.getInstance().getUser() != null) {
                query = "SELECT lf.id AS id, b.title AS bookTitle, s.nim AS studentNim, lf.amount AS amount, lf.status AS status, lf.note AS note " +
                        "FROM late_fees lf " +
                        "JOIN borrowed_books bb ON lf.borrowed_book_id = bb.id " +
                        "JOIN books b ON bb.book_id = b.id " +
                        "JOIN students s ON bb.student_id = s.id";
            } else {
                return studentLateFeesData;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                if (StudentSession.getInstance().getStudent() != null) {
                    pstmt.setString(1, StudentSession.getInstance().getStudent().getId());
                }
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    String id = rs.getString("id");
                    String bookTitle = rs.getString("bookTitle");
                    String studentNim = rs.getString("studentNim");
                    int amount = rs.getInt("amount");
                    String status = rs.getString("status");
                    String note = rs.getString("note");

                    studentLateFeesData.add(new LateFeeDTO(id, bookTitle, studentNim, amount, status, note));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentLateFeesData;
    }


    private static void changeStatusToPaid(String lateFeeId) {
        try (Connection conn = DriverManager.getConnection(Config.getDbUrl())) {
            String query = "UPDATE late_fees SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, PAID);
                pstmt.setString(2, lateFeeId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
