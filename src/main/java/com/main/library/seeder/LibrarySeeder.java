package com.main.library.seeder;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class LibrarySeeder {
    private static final String URL = "jdbc:sqlite:library_db.db";

    public static void main(String[] args) {
        System.out.println("Seeding database...");
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                seedUsers(conn);
                seedBooks(conn);
                seedStudents(conn);
            }
            System.out.println("Database seeded successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void seedUsers(Connection conn) throws SQLException {
        String sql = "INSERT INTO users (id, name, username, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "Admin Perpus 1");
            pstmt.setString(3, "admin");
            pstmt.setString(4, BCrypt.hashpw("admin", BCrypt.gensalt()));
            pstmt.executeUpdate();
        }
    }

    private static void seedBooks(Connection conn) throws SQLException {
        String sql = "INSERT INTO books (id, title, author, category, stock) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "1984");
            pstmt.setString(3, "George Orwell");
            pstmt.setString(4, "History");
            pstmt.setInt(5, 5);
            pstmt.executeUpdate();

            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "To Kill a Mockingbird");
            pstmt.setString(3, "Harper Lee");
            pstmt.setString(4, "Fiction");
            pstmt.setInt(5, 3);
            pstmt.executeUpdate();

            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "The Great Gatsby");
            pstmt.setString(3, "F. Scott Fitzgerald");
            pstmt.setString(4, "Fiction");
            pstmt.setInt(5, 2);
            pstmt.executeUpdate();
        }
    }

    private static void seedStudents(Connection conn) throws SQLException {
        String sql = "INSERT INTO students (id, name, nim, faculty, major, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "radan");
            pstmt.setString(3, "202210370311208");
            pstmt.setString(4, "Teknik");
            pstmt.setString(5, "Informatika");
            pstmt.setString(6, BCrypt.hashpw("password", BCrypt.gensalt()));
            pstmt.executeUpdate();
        }
    }
}

