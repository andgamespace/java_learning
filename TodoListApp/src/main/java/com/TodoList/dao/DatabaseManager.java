// src/main/java/com/TodoList/dao/DatabaseManager.java
package com.TodoList.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:todos.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            // Ensure the SQLite JDBC driver is loaded
            Class.forName("org.sqlite.JDBC");

            // Create database connection
            connection = DriverManager.getConnection(DB_URL);

            // Create tables if they don't exist
            initDatabase();

            System.out.println("Database connection established.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    // Singleton pattern to ensure only one database connection
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create todos table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS todos (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT, " +
                            "completed INTEGER DEFAULT 0, " + // SQLite doesn't have boolean, using INTEGER (0/1)
                            "due_date TEXT, " +              // Store dates as ISO-8601 strings
                            "created_at TEXT NOT NULL" +
                            ")"
            );
        }
    }

    // Close the database connection when the application exits
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database: " + e.getMessage());
            }
        }
    }
}