// src/main/java/com/TodoList/dao/TodoDAO.java
package com.TodoList.dao;

import com.TodoList.model.Todo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private final Connection connection;

    public TodoDAO() {
        this.connection = DatabaseManager.getInstance().getConnection();
    }

    // Create a new todo item
    public boolean insert(Todo todo) {
        String sql = "INSERT INTO todos (title, description, completed, due_date, created_at) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setInt(3, todo.isCompleted() ? 1 : 0);
            statement.setString(4, todo.getDueDate() != null ? todo.getDueDate().toString() : null);
            statement.setString(5, todo.getCreatedAt().toString());

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

    // Get all todo items
    public List<Todo> getAll() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos ORDER BY created_at DESC";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                todos.add(extractTodoFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting todos: " + e.getMessage());
        }

        return todos;
    }

    // Get todo item by ID
    public Todo getById(int id) {
        String sql = "SELECT * FROM todos WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractTodoFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting todo by id: " + e.getMessage());
        }

        return null;
    }

    // Update a todo item
    public boolean update(Todo todo) {
        String sql = "UPDATE todos SET title = ?, description = ?, completed = ?, due_date = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, todo.getTitle());
            statement.setString(2, todo.getDescription());
            statement.setInt(3, todo.isCompleted() ? 1 : 0);
            statement.setString(4, todo.getDueDate() != null ? todo.getDueDate().toString() : null);
            statement.setInt(5, todo.getId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating todo: " + e.getMessage());
            return false;
        }
    }

    // Delete a todo item
    public boolean delete(int id) {
        String sql = "DELETE FROM todos WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting todo: " + e.getMessage());
            return false;
        }
    }

    // Helper method to extract a Todo from a ResultSet
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