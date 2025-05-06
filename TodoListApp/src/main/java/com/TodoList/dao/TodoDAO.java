package com.TodoList.dao;

import com.TodoList.model.Todo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Todo items.
 * Handles all database operations related to Todo entities.
 */
public class TodoDAO {
    private final DatabaseManager dbManager;

    /**
     * Creates a new TodoDAO instance.
     */
    public TodoDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Creates a new todo item in the database.
     *
     * @param todo The todo item to insert
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if todo is null or has invalid properties
     */
    public boolean insert(Todo todo) {
        if (todo == null) {
            throw new IllegalArgumentException("Todo cannot be null");
        }

        String sql = "INSERT INTO todos (title, description, completed, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setInt(3, todo.isCompleted() ? 1 : 0);
            statement.setString(4, todo.getDueDate() != null ? todo.getDueDate().toString() : null);

            LocalDateTime now = LocalDateTime.now();
            statement.setString(5, todo.getCreatedAt() != null ? todo.getCreatedAt().toString() : now.toString());
            statement.setString(6, now.toString());

            int rowsInserted = statement.executeUpdate();

            // Get the auto-generated ID and set it in the Todo object
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        todo.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error inserting todo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all todo items from the database.
     *
     * @return A list of all todo items
     */
    public List<Todo> getAll() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos ORDER BY created_at DESC";

        try (Connection conn = dbManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting todos: " + e.getMessage());
        }

        return todos;
    }

    /**
     * Retrieves a todo item by its ID.
     *
     * @param id The ID of the todo item
     * @return An Optional containing the todo item if found, or empty if not found
     */
    public Optional<Todo> getById(int id) {
        String sql = "SELECT * FROM todos WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractTodoFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting todo by id: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Updates an existing todo item.
     *
     * @param todo The todo item to update
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if todo is null or has invalid properties
     */
    public boolean update(Todo todo) {
        if (todo == null || todo.getId() <= 0) {
            throw new IllegalArgumentException("Todo cannot be null and must have a valid ID");
        }

        String sql = "UPDATE todos SET title = ?, description = ?, completed = ?, " +
                "due_date = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setInt(3, todo.isCompleted() ? 1 : 0);
            statement.setString(4, todo.getDueDate() != null ? todo.getDueDate().toString() : null);
            statement.setString(5, LocalDateTime.now().toString());
            statement.setInt(6, todo.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating todo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a todo item by its ID.
     *
     * @param id The ID of the todo item to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "DELETE FROM todos WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting todo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all completed todo items.
     *
     * @return A list of completed todo items
     */
    public List<Todo> getCompleted() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos WHERE completed = 1 ORDER BY created_at DESC";

        try (Connection conn = dbManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting completed todos: " + e.getMessage());
        }

        return todos;
    }

    /**
     * Gets all pending (not completed) todo items.
     *
     * @return A list of pending todo items
     */
    public List<Todo> getPending() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos WHERE completed = 0 ORDER BY due_date ASC, created_at DESC";

        try (Connection conn = dbManager.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending todos: " + e.getMessage());
        }

        return todos;
    }

    /**
     * Helper method to extract a Todo from a ResultSet.
     *
     * @param resultSet The ResultSet to extract from
     * @return A Todo object
     * @throws SQLException if extraction fails
     */
    private Todo extractTodoFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        boolean completed = resultSet.getInt("completed") == 1;

        String dueDateStr = resultSet.getString("due_date");
        LocalDateTime dueDate = dueDateStr != null ? LocalDateTime.parse(dueDateStr) : null;

        LocalDateTime createdAt = LocalDateTime.parse(resultSet.getString("created_at"));

        return new Todo(id, title, description, completed, dueDate, createdAt);
    }
}