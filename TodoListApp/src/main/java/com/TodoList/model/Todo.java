package com.TodoList.model;
// TodoListApp/src/main/java/com/TodoList/model/Todo.java
import java.time.LocalDateTime;

public class Todo {
    private int id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    //constructor for new todos
    // @param title: the title of the todo
    // @param description: the description of the todo
    // @param dueDate: the due date of the todo
    // @param completed: the completion status of the todo
    // @param createdAt: the creation date of the todo
    // @param id: the id of the todo
    public Todo(String title, String description, LocalDateTime dueDate){
        this.title = title;
        this.completed = false;
        this.description = description;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }
    //constructor for loading todos from a database
    public Todo(int id, String title, String description, boolean completed, LocalDateTime dueDate, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }
    //Getters and Setters
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public boolean isCompleted(){
        return completed;
    }
    public void setCompleted(boolean completed){
        this.completed = completed;
    }
    public LocalDateTime getDueDate(){
        return dueDate;
    }
    public void setDueDate(LocalDateTime dueDate){
        this.dueDate = dueDate;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
    @Override
    public String toString(){
        return title + (completed ? " (Done)" : "");
    }
}
