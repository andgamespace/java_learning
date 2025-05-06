// src/main/java/com/TodoList/view/TodoView.java
package com.TodoList.view;

import com.TodoList.dao.TodoDAO;
import com.TodoList.model.Todo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;

public class TodoView extends BorderPane {
    private final TodoDAO todoDAO;
    private final ObservableList<Todo> todoList;
    private final ListView<Todo> todoListView;

    // UI components
    private TextField titleField;
    private TextArea descriptionArea;
    private CheckBox completedCheckbox;
    private DatePicker dueDatePicker;

    public TodoView() {
        // Initialize the DAO and data
        todoDAO = new TodoDAO();
        todoList = FXCollections.observableArrayList(todoDAO.getAll());

        // Initialize UI components
        titleField = new TextField();
        titleField.setPromptText("Task title");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task description");
        descriptionArea.setPrefRowCount(3);
        dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date");
        completedCheckbox = new CheckBox("Completed");

        // Create task list view
        // In TodoView.java, replace the todoListView initialization:
        todoListView = new ListView<>(todoList);
        todoListView.setCellFactory(param -> new TodoListCell());
        todoListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showTodoDetails(newValue));

        // Create button controls
        Button addButton = new Button("New Task");
        addButton.setOnAction(e -> addNewTodo());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveTodo());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteTodo());

        // Create layout
        VBox detailsBox = new VBox(10,
                new Label("Title:"), titleField,
                new Label("Description:"), descriptionArea,
                new Label("Due Date:"), dueDatePicker,
                completedCheckbox
        );
        detailsBox.setPadding(new Insets(10));

        HBox buttonBox = new HBox(10, addButton, saveButton, deleteButton);
        buttonBox.setPadding(new Insets(10));

        // Set up the main layout
        setCenter(todoListView);
        setRight(detailsBox);
        setBottom(buttonBox);

        // Set minimum width for the details section
        detailsBox.setMinWidth(200);
        detailsBox.setPrefWidth(250);

        // Initially disable details until a task is selected
        disableDetails(true);
    }

    // Display details of selected todo
    private void showTodoDetails(Todo todo) {
        if (todo != null) {
            titleField.setText(todo.getTitle());
            descriptionArea.setText(todo.getDescription());
            completedCheckbox.setSelected(todo.isCompleted());
            dueDatePicker.setValue(todo.getDueDate() != null ?
                    todo.getDueDate().toLocalDate() : null);
            disableDetails(false);
        } else {
            titleField.clear();
            descriptionArea.clear();
            completedCheckbox.setSelected(false);
            disableDetails(true);
        }
    }

    // Enable/disable detail fields
    private void disableDetails(boolean disable) {
        titleField.setDisable(disable);
        descriptionArea.setDisable(disable);
        completedCheckbox.setDisable(disable);
        dueDatePicker.setDisable(disable);
    }

    // Add a new todo
    private void addNewTodo() {
        Todo newTodo = new Todo("New Task", "", LocalDateTime.now().plusDays(1));

        if (todoDAO.insert(newTodo)) {
            todoList.add(0, newTodo);
            todoListView.getSelectionModel().select(newTodo);
        } else {
            showAlert("Error", "Could not create new task");
        }
    }

    // Save the current todo
    private void saveTodo() {
        Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();

        if (selectedTodo != null) {
            // Update the todo object with form values
            selectedTodo.setTitle(titleField.getText());
            selectedTodo.setDescription(descriptionArea.getText());
            selectedTodo.setCompleted(completedCheckbox.isSelected());
            if (dueDatePicker.getValue() != null) {
                selectedTodo.setDueDate(dueDatePicker.getValue().atTime(23, 59, 59));
            } else {
                selectedTodo.setDueDate(null);
            }
            // Update in database
            if (todoDAO.update(selectedTodo)) {
                // Refresh the list view
                todoListView.refresh();
            } else {
                showAlert("Error", "Could not save changes");
            }
        }
    }

    // Delete the selected todo
    private void deleteTodo() {
        Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();

        if (selectedTodo != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this task?",
                    ButtonType.YES, ButtonType.NO);

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (todoDAO.delete(selectedTodo.getId())) {
                        todoList.remove(selectedTodo);
                    } else {
                        showAlert("Error", "Could not delete task");
                    }
                }
            });
        }
    }

    // Helper method to show alerts
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}