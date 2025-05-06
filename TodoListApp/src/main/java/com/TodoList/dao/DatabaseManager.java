package com.TodoList.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections and initialization.
 * Implements the Singleton pattern for efficient connection management.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:todos.db";
    private static volatile DatabaseManager instance;
    private Connection connection;

    /**
     * Private constructor to enforce the Singleton pattern.
     * Initializes the database connection and sets up tables.
     */
    private DatabaseManager() {
        try {
            // Ensure the SQLite JDBC driver is loaded
            Class.forName("org.sqlite.JDBC");

            // Create database connection with pragmas for better SQLite performance
            connection = DriverManager.getConnection(DB_URL + "?journal_mode=WAL&synchronous=NORMAL");

            // Enable foreign keys support
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }

            // Create tables if they don't exist
            initDatabase();

            System.out.println("Database connection established.");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
            throw new RuntimeException("Failed to load database driver", e);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Gets the singleton instance of the DatabaseManager.
     * Ensures thread safety with double-checked locking pattern.
     *
     * @return The singleton DatabaseManager instance
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    /**
     * Gets the active database connection.
     * Reestablishes connection if needed.
     *
     * @return A valid database connection
     * @throws SQLException if connection cannot be established
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(DB_URL);
                    System.out.println("Database connection reestablished.");
                }
            }
        }
        return connection;
    }

    /**
     * Creates necessary database tables if they don't exist.
     *
     * @throws SQLException if table creation fails
     */
    private void initDatabase() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create todos table with more complete schema
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS todos (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT NOT NULL, " +
                            "description TEXT, " +
                            "completed INTEGER DEFAULT 0, " + // SQLite doesn't have boolean, using INTEGER (0/1)
                            "due_date TEXT, " +              // Store dates as ISO-8601 strings
                            "created_at TEXT NOT NULL, " +
                            "updated_at TEXT" +
                            ")"
            );

            // Create indexes for faster querying
            statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_completed ON todos(completed)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_due_date ON todos(due_date)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_todos_created_at ON todos(created_at)");
        }
    }

    /**
     * Starts a transaction on the current connection.
     *
     * @throws SQLException if setting auto-commit mode fails
     */
    public void beginTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    /**
     * Commits the current transaction.
     *
     * @throws SQLException if commit fails
     */
    public void commitTransaction() throws SQLException {
        getConnection().commit();
        getConnection().setAutoCommit(true);
    }

    /**
     * Rolls back the current transaction.
     *
     * @throws SQLException if rollback fails
     */
    public void rollbackTransaction() throws SQLException {
        getConnection().rollback();
        getConnection().setAutoCommit(true);
    }

    /**
     * Closes the database connection when the application exits.
     */
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