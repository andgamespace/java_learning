package com.TodoList.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a Todo item in the application.
 * Contains all the relevant information about a task.
 */
public class Todo {
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

    /**
     * Constructor for creating new todos.
     *
     * @param title The title of the todo (must not be null or empty)
     * @param description The description of the todo
     * @param dueDate The due date of the todo
     * @throws IllegalArgumentException if title is null or empty
     */
    public Todo(String title, String description, LocalDateTime dueDate) {
        setTitle(title); // Use setter for validation
        this.description = description != null ? description : "";
        this.completed = false;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor for loading todos from a database.
     *
     * @param id The unique identifier
     * @param title The title of the todo
     * @param description The description of the todo
     * @param completed The completion status of the todo
     * @param dueDate The due date of the todo
     * @param createdAt The creation date of the todo
     * @throws IllegalArgumentException if title is null or empty
     */
    public Todo(int id, String title, String description, boolean completed,
                LocalDateTime dueDate, LocalDateTime createdAt) {
        this.id = id;
        setTitle(title); // Use setter for validation
        this.description = description != null ? description : "";
        this.completed = completed;
        this.dueDate = dueDate;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the todo.
     *
     * @param title The new title (must not be null or empty)
     * @throws IllegalArgumentException if title is null or empty
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.title = title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Determines if the task is overdue based on the current time.
     *
     * @return true if the task is overdue, false otherwise or if no due date
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate) && !completed;
    }

    @Override
    public String toString() {
        return title + (completed ? " (Done)" : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id &&
                completed == todo.completed &&
                Objects.equals(title, todo.title) &&
                Objects.equals(description, todo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, completed);
    }
}