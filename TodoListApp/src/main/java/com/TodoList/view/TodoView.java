package com.TodoList.view;

import com.TodoList.dao.TodoDAO;
import com.TodoList.model.Todo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Main view component for the Todo list application.
 * Manages the display and interaction with Todo items.
 */
public class TodoView extends BorderPane {
    private final TodoDAO todoDAO;
    private final ObservableList<Todo> todoList;
    // Initialize ListView directly in the declaration
    private final ListView<Todo> todoListView;

    // UI components
    private TextField titleField;
    private TextArea descriptionArea;
    private CheckBox completedCheckbox;
    private DatePicker dueDatePicker;
    private ComboBox<String> filterComboBox;
    private Button addButton;
    private Button saveButton;
    private Button deleteButton;

    // Current state
    private Todo currentlyEditedTodo;
    private boolean isEditing = false;

    /**
     * Creates a new TodoView.
     */
    public TodoView() {
        // Initialize the DAO and data
        todoDAO = new TodoDAO();
        todoList = FXCollections.observableArrayList(todoDAO.getAll());

        // Initialize todoListView in the constructor rather than in initializeComponents
        todoListView = new ListView<>(todoList);
        todoListView.setCellFactory(param -> new TodoListCell());
        todoListView.setPrefHeight(400);

        // Set up the UI components
        initializeComponents();
        layoutComponents();
        setupEventHandlers();

        // Initially disable details until a task is selected
        disableDetails(true);
    }

    /**
     * Initialize all UI components.
     */
    private void initializeComponents() {
        // Text fields
        titleField = new TextField();
        titleField.setPromptText("Task title");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task description");
        descriptionArea.setPrefRowCount(5);

        dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due date");

        completedCheckbox = new CheckBox("Completed");

        // Buttons
        addButton = new Button("New Task");
        saveButton = new Button("Save");
        deleteButton = new Button("Delete");

        // Filter dropdown
        filterComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "All Tasks", "Pending Tasks", "Completed Tasks"));
        filterComboBox.setValue("All Tasks");

        // Note: todoListView is now initialized in the constructor
    }

    /**
     * Arrange components in the layout.
     */
    private void layoutComponents() {
        // Top filter bar
        HBox filterBar = new HBox(10, new Label("Show:"), filterComboBox);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(Pos.CENTER_LEFT);
        setTop(filterBar);

        // Center list view
        todoListView.setPrefWidth(500);
        HBox.setHgrow(todoListView, Priority.ALWAYS);
        setCenter(todoListView);

        // Right details panel
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(10);
        detailsGrid.setVgap(10);
        detailsGrid.setPadding(new Insets(20));

        detailsGrid.add(new Label("Title:"), 0, 0);
        detailsGrid.add(titleField, 0, 1);

        detailsGrid.add(new Label("Description:"), 0, 2);
        detailsGrid.add(descriptionArea, 0, 3);

        detailsGrid.add(new Label("Due Date:"), 0, 4);
        detailsGrid.add(dueDatePicker, 0, 5);

        detailsGrid.add(completedCheckbox, 0, 6);

        // Button bar
        HBox buttonBar = new HBox(10, addButton, saveButton, deleteButton);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setAlignment(Pos.CENTER);

        detailsGrid.add(buttonBar, 0, 7);
        detailsGrid.setPrefWidth(300);

        setRight(detailsGrid);
    }

    /**
     * Set up event handlers for UI components.
     */
    private void setupEventHandlers() {
        // Filter combobox change
        filterComboBox.setOnAction(e -> updateTaskList());

        // ListView selection change
        todoListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // If editing and selection changes, confirm discarding changes
                    if (isEditing && oldValue != null && !oldValue.equals(newValue)) {
                        if (!confirmDiscardChanges()) {
                            // Reselect the previous item if user cancels
                            todoListView.getSelectionModel().select(oldValue);
                            return;
                        }
                    }
                    showTodoDetails(newValue);
                });

        // Double-click on list item to toggle completion
        todoListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Todo selectedTodo = todoListView.getSelectionModel().getSelectedItem();
                if (selectedTodo != null) {
                    selectedTodo.setCompleted(!selectedTodo.isCompleted());
                    if (todoDAO.update(selectedTodo)) {
                        todoListView.refresh();
                        showTodoDetails(selectedTodo);
                    }
                }
            }
        });

        // Button actions
        addButton.setOnAction(e -> addNewTodo());
        saveButton.setOnAction(e -> saveTodo());
        deleteButton.setOnAction(e -> deleteTodo());
    }

    /**
     * Updates the task list based on the selected filter.
     */
    private void updateTaskList() {
        todoList.clear();

        switch (filterComboBox.getValue()) {
            case "All Tasks":
                todoList.addAll(todoDAO.getAll());
                break;
            case "Pending Tasks":
                todoList.addAll(todoDAO.getPending());
                break;
            case "Completed Tasks":
                todoList.addAll(todoDAO.getCompleted());
                break;
        }
    }

    /**
     * Display details of selected todo.
     *
     * @param todo The todo item to display
     */
    private void showTodoDetails(Todo todo) {
        currentlyEditedTodo = todo;
        isEditing = false;

        if (todo != null) {
            titleField.setText(todo.getTitle());
            descriptionArea.setText(todo.getDescription());
            completedCheckbox.setSelected(todo.isCompleted());
            dueDatePicker.setValue(todo.getDueDate() != null ?
                    todo.getDueDate().toLocalDate() : null);
            disableDetails(false);
            isEditing = true;
        } else {
            titleField.clear();
            descriptionArea.clear();
            completedCheckbox.setSelected(false);
            dueDatePicker.setValue(null);
            disableDetails(true);
        }
    }

    /**
     * Enable/disable detail fields.
     *
     * @param disable true to disable fields, false to enable
     */
    private void disableDetails(boolean disable) {
        titleField.setDisable(disable);
        descriptionArea.setDisable(disable);
        completedCheckbox.setDisable(disable);
        dueDatePicker.setDisable(disable);
        saveButton.setDisable(disable);
        deleteButton.setDisable(disable);
    }

    /**
     * Add a new todo.
     */
    private void addNewTodo() {
        // If currently editing, confirm discard changes
        if (isEditing && !confirmDiscardChanges()) {
            return;
        }

        Todo newTodo = new Todo("New Task", "", LocalDateTime.now().plusDays(1));

        if (todoDAO.insert(newTodo)) {
            updateTaskList();
            todoListView.getSelectionModel().select(newTodo);
            showTodoDetails(newTodo);

            // Put focus on title field for immediate editing
            titleField.requestFocus();
            titleField.selectAll();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not create new task");
        }
    }

    /**
     * Save the current todo.
     */
    private void saveTodo() {
        Todo selectedTodo = currentlyEditedTodo;

        if (selectedTodo != null) {
            try {
                // Validate input
                String title = titleField.getText().trim();
                if (title.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                            "Title cannot be empty");
                    titleField.requestFocus();
                    return;
                }

                // Update the todo object with form values
                selectedTodo.setTitle(title);
                selectedTodo.setDescription(descriptionArea.getText());
                selectedTodo.setCompleted(completedCheckbox.isSelected());

                if (dueDatePicker.getValue() != null) {
                    selectedTodo.setDueDate(dueDatePicker.getValue().atTime(23, 59, 59));
                } else {
                    selectedTodo.setDueDate(null);
                }

                // Update in database
                if (todoDAO.update(selectedTodo)) {
                    // Show success feedback
                    showAlert(Alert.AlertType.INFORMATION, "Success",
                            "Task saved successfully");

                    // Refresh the list view
                    updateTaskList();
                    todoListView.refresh();
                    todoListView.getSelectionModel().select(selectedTodo);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not save changes");
                }
            } catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
            }
        }
    }

    /**
     * Delete the selected todo.
     */
    private void deleteTodo() {
        Todo selectedTodo = currentlyEditedTodo;

        if (selectedTodo != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to delete this task?",
                    ButtonType.YES, ButtonType.NO);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Delete \"" + selectedTodo.getTitle() + "\"?");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    if (todoDAO.delete(selectedTodo.getId())) {
                        updateTaskList();
                        todoListView.getSelectionModel().clearSelection();
                        currentlyEditedTodo = null;
                        isEditing = false;
                        disableDetails(true);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Could not delete task");
                    }
                }
            });
        }
    }

    /**
     * Confirm discarding changes if there are unsaved changes.
     *
     * @return true if user confirms or no changes, false if cancels
     */
    private boolean confirmDiscardChanges() {
        if (currentlyEditedTodo == null) {
            return true;
        }

        boolean hasChanges =
                !titleField.getText().equals(currentlyEditedTodo.getTitle()) ||
                        !descriptionArea.getText().equals(currentlyEditedTodo.getDescription()) ||
                        completedCheckbox.isSelected() != currentlyEditedTodo.isCompleted() ||
                        (dueDatePicker.getValue() != null && currentlyEditedTodo.getDueDate() != null &&
                                !dueDatePicker.getValue().equals(currentlyEditedTodo.getDueDate().toLocalDate())) ||
                        (dueDatePicker.getValue() == null && currentlyEditedTodo.getDueDate() != null) ||
                        (dueDatePicker.getValue() != null && currentlyEditedTodo.getDueDate() == null);

        if (hasChanges) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "You have unsaved changes. Discard them?",
                    ButtonType.YES, ButtonType.NO);
            alert.setTitle("Unsaved Changes");

            Optional<ButtonType> result = alert.showAndWait();
            return result.isPresent() && result.get() == ButtonType.YES;
        }

        return true;
    }

    /**
     * Helper method to show alerts.
     *
     * @param type The alert type
     * @param title The alert title
     * @param content The alert content
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}