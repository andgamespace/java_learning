// Create a new class:
// src/main/java/com/TodoList/view/TodoListCell.java
package com.TodoList.view;

import com.TodoList.model.Todo;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class TodoListCell extends ListCell<Todo> {
    private final HBox content;
    private final Text title;
    private final Text description;
    private final CheckBox checkBox;

    public TodoListCell() {
        title = new Text();
        title.getStyleClass().add("todo-title");

        description = new Text();
        description.getStyleClass().add("todo-description");

        checkBox = new CheckBox();
        checkBox.getStyleClass().add("todo-checkbox");

        VBox textVBox = new VBox(5, title, description);
        HBox.setHgrow(textVBox, Priority.ALWAYS);

        content = new HBox(10, checkBox, textVBox);
        content.getStyleClass().add("todo-cell");
    }

    @Override
    protected void updateItem(Todo todo, boolean empty) {
        super.updateItem(todo, empty);

        if (empty || todo == null) {
            setGraphic(null);
        } else {
            title.setText(todo.getTitle());
            description.setText(todo.getDescription());
            checkBox.setSelected(todo.isCompleted());

            // Prevent checkbox from changing selection immediately
            checkBox.setOnAction(event -> {
                event.consume(); // Prevent default handling
            });

            setGraphic(content);
        }
    }
}