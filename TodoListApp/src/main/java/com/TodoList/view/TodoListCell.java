package com.TodoList.view;

import com.TodoList.model.Todo;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Custom ListCell implementation for Todo items.
 * Provides a rich display of Todo information in the ListView.
 */
public class TodoListCell extends ListCell<Todo> {
    private final HBox content;
    private final Text title;
    private final TextFlow description;
    private final CheckBox checkBox;
    private final Text dueDate;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    /**
     * Creates a new TodoListCell.
     */
    public TodoListCell() {
        title = new Text();
        title.getStyleClass().add("todo-title");

        description = new TextFlow(new Text());
        description.getStyleClass().add("todo-description");

        dueDate = new Text();
        dueDate.getStyleClass().add("todo-due-date");

        checkBox = new CheckBox();
        checkBox.getStyleClass().add("todo-checkbox");

        VBox textVBox = new VBox(5, title, description, dueDate);
        HBox.setHgrow(textVBox, Priority.ALWAYS);

        content = new HBox(10, checkBox, textVBox);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getStyleClass().add("todo-cell");
    }

    @Override
    protected void updateItem(Todo todo, boolean empty) {
        super.updateItem(todo, empty);

        if (empty || todo == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Update title
            title.setText(todo.getTitle());

            // Bold the title if task is not completed
            title.setStyle(todo.isCompleted()
                    ? "-fx-font-weight: normal; -fx-strikethrough: true;"
                    : "-fx-font-weight: bold; -fx-strikethrough: false;");

            // Update description
            Text descText = new Text(todo.getDescription() != null && !todo.getDescription().isEmpty()
                    ? todo.getDescription() : "No description");
            description.getChildren().setAll(descText);

            // Update checkbox
            checkBox.setSelected(todo.isCompleted());

            // Disable direct checkbox interaction (let the controller handle it)
            checkBox.setMouseTransparent(true);
            checkBox.setFocusTraversable(false);

            // Update due date display
            if (todo.getDueDate() != null) {
                dueDate.setText("Due: " + todo.getDueDate().format(DATE_FORMATTER));

                // Highlight overdue tasks
                if (todo.isOverdue()) {
                    dueDate.setFill(Color.RED);
                    dueDate.setStyle("-fx-font-weight: bold;");
                } else {
                    dueDate.setFill(Color.BLACK);
                    dueDate.setStyle("-fx-font-weight: normal;");
                }
            } else {
                dueDate.setText("No due date");
                dueDate.setFill(Color.GRAY);
            }

            // Apply styling based on completion status
            if (todo.isCompleted()) {
                content.setStyle("-fx-opacity: 0.7;");
            } else {
                content.setStyle("-fx-opacity: 1.0;");
            }

            setGraphic(content);
        }
    }
}